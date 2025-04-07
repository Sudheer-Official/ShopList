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
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingItemEntity
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity
import uk.ac.tees.mad.shoplist.data.remote.FirebaseAuthResult
import uk.ac.tees.mad.shoplist.data.remote.UserData
import uk.ac.tees.mad.shoplist.data.remote.UserDetails
import uk.ac.tees.mad.shoplist.data.repository.FirebaseAuthRepository
import uk.ac.tees.mad.shoplist.data.repository.ShoppingItemRepository
import uk.ac.tees.mad.shoplist.data.repository.ShoppingListRepository
import uk.ac.tees.mad.shoplist.ui.utils.LoadingState

class AddEditItemViewModel(
    private val shoppingListRepository: ShoppingListRepository,
    private val shoppingItemRepository: ShoppingItemRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _shoppingList =
        MutableStateFlow<LoadingState<ShoppingListEntity>>(LoadingState.Loading)
    val shoppingList: StateFlow<LoadingState<ShoppingListEntity>> = _shoppingList.asStateFlow()

    private val _shoppingItems =
        MutableStateFlow<LoadingState<List<ShoppingItemEntity>>>(LoadingState.Loading)
    val shoppingItems: StateFlow<LoadingState<List<ShoppingItemEntity>>> =
        _shoppingItems.asStateFlow()

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
                }
            }
        }
    }

    fun getShoppingListById(id: Int = 0) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (id == 0) {
                    _shoppingList.value = LoadingState.Success(
                        ShoppingListEntity(
                            title = "",
                            itemCount = 0,
                            completedItems = 0,
                            lastModified = "",
                            category = ""
                        )
                    )
                } else {
                    shoppingListRepository.getShoppingListById(id).collectLatest { shoppingList ->
                        _shoppingList.value = LoadingState.Success(shoppingList)
                    }
                }
            }
        }
    }

    fun getShoppingItemsByListId(listId: Int, userId: String = userData.value.userId.toString()) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingItemRepository.getShoppingItemsByListIdForUser(userId,listId)
                    .collectLatest { shoppingItems ->
                        _shoppingItems.value = LoadingState.Success(shoppingItems)
                    }
            }
        }
    }

}