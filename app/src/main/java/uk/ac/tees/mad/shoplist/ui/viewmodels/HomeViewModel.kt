package uk.ac.tees.mad.shoplist.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity
import uk.ac.tees.mad.shoplist.data.remote.FirebaseAuthResult
import uk.ac.tees.mad.shoplist.data.remote.UserData
import uk.ac.tees.mad.shoplist.data.remote.UserDetails
import uk.ac.tees.mad.shoplist.data.repository.FirebaseAuthRepository
import uk.ac.tees.mad.shoplist.data.repository.ShoppingListRepository

class HomeViewModel(
    private val shoppingListRepository: ShoppingListRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _allShoppingLists = MutableStateFlow<List<ShoppingListEntity>>(emptyList())
    val allShoppingLists: StateFlow<List<ShoppingListEntity>> = _allShoppingLists.asStateFlow()

    private val _userDetails = MutableStateFlow<FirebaseAuthResult<UserDetails>>(FirebaseAuthResult.Loading)
    val userDetails: StateFlow<FirebaseAuthResult<UserDetails>> = _userDetails.asStateFlow()

    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData.asStateFlow()

    init {
        fetchUserDetails()
    }

    fun fetchUserDetails() {
        viewModelScope.launch {
            firebaseAuthRepository.getCurrentUserDetails().collect { result ->
                _userDetails.value = result
                if (result is FirebaseAuthResult.Success) {
                    _userData.update {
                        it.copy(
                            userDetails = result.data, userId = firebaseAuthRepository.getCurrentUserId()
                        )
                    }
                    getAllShoppingLists(userData.value.userId.toString())
                }
            }
        }
    }

    // Function to retrieve all shopping lists
    fun getAllShoppingLists(userId: String= userData.value.userId.toString()) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingListRepository.getAllShoppingListsForUser(userId).collectLatest { shoppingLists ->
                    _allShoppingLists.value = shoppingLists
                }
            }
        }
    }

    // Function to retrieve shopping lists by category
    fun getShoppingListsByCategory(category: String, userId: String = userData.value.userId.toString()) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingListRepository.getShoppingListsByCategoryForUser(userId,category)
                    .collectLatest { shoppingLists ->
                        _allShoppingLists.value = shoppingLists
                    }
            }
        }
    }

    fun logOut() {
        firebaseAuthRepository.signOut()
    }
}