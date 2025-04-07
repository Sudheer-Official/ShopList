package uk.ac.tees.mad.shoplist.ui.viewmodels

import android.content.Context
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
import uk.ac.tees.mad.shoplist.data.remote.FirebaseAuthResult
import uk.ac.tees.mad.shoplist.data.remote.UserData
import uk.ac.tees.mad.shoplist.data.remote.UserDetails
import uk.ac.tees.mad.shoplist.data.repository.FirebaseAuthRepository
import uk.ac.tees.mad.shoplist.data.repository.FirestoreRepository
import uk.ac.tees.mad.shoplist.data.repository.ShoppingItemRepository
import uk.ac.tees.mad.shoplist.ui.utils.LoadingState
import uk.ac.tees.mad.shoplist.ui.utils.showNotification

class ShoppingItemViewModel(
    private val shoppingItemRepository: ShoppingItemRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
) : ViewModel() {

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

    fun getAllShoppingItemsByListId(listId: Int, userId: String = userData.value.userId.toString()) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingItemRepository.getShoppingItemsByListIdForUser(userId,listId)
                    .collectLatest { shoppingItems ->
                        _shoppingItems.value = LoadingState.Success(shoppingItems)
                    }
            }
        }
    }

    fun insertShoppingItem(shoppingItem: ShoppingItemEntity, listTitle: String, context: Context,userId: String = userData.value.userId.toString()) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingItemRepository.insertShoppingItem(shoppingItem.copy(userId = userId))
                showNotification(
                    context, "Item Added To List", "${shoppingItem.name} added to $listTitle"
                )
            }
        }
    }

    fun updateShoppingItem(shoppingItem: ShoppingItemEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingItemRepository.updateShoppingItem(shoppingItem.copy(needsUpdate = true))
            }
        }
    }

    fun deleteShoppingItem(shoppingItem: ShoppingItemEntity, listTitle: String, context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingItemRepository.updateShoppingItem(shoppingItem.copy(isDeleted = true))
                showNotification(
                    context,
                    "Item Deleted From List",
                    "${shoppingItem.name} deleted from $listTitle"
                )
            }
        }
    }

    fun deleteAllPurchasedItemsForList(listId: Int,userId: String = userData.value.userId.toString()) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingItemRepository.markAsDeletedOnAllPurchasedItemsForListForUser(userId,listId)
            }
        }
    }

}