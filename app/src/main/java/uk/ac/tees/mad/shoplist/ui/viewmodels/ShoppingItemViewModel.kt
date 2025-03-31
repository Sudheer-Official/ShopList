package uk.ac.tees.mad.shoplist.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingItemEntity
import uk.ac.tees.mad.shoplist.data.repository.ShoppingItemRepository
import uk.ac.tees.mad.shoplist.data.repository.ShoppingListRepository
import uk.ac.tees.mad.shoplist.ui.utils.LoadingState

class ShoppingItemViewModel(
    private val shoppingItemRepository: ShoppingItemRepository
) : ViewModel() {
    private val _shoppingItems =
        MutableStateFlow<LoadingState<List<ShoppingItemEntity>>>(LoadingState.Loading)
    val shoppingItems: StateFlow<LoadingState<List<ShoppingItemEntity>>> =
        _shoppingItems.asStateFlow()

    fun getAllShoppingItemsByListId(listId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingItemRepository.getShoppingItemsByListId(listId)
                    .collectLatest { shoppingItems ->
                        _shoppingItems.value = LoadingState.Success(shoppingItems)
                    }
            }
        }
    }

    fun insertShoppingItem(shoppingItem: ShoppingItemEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingItemRepository.insertShoppingItem(shoppingItem)
            }
        }
    }

    fun updateShoppingItem(shoppingItem: ShoppingItemEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingItemRepository.updateShoppingItem(shoppingItem)
            }
        }
    }

    fun deleteShoppingItem(shoppingItem: ShoppingItemEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
            shoppingItemRepository.deleteShoppingItem(shoppingItem)
            }
        }
    }

    fun deleteAllPurchasedItemsForList(listId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
            shoppingItemRepository.deleteAllPurchasedItemsForList(listId)
            }
        }
    }

}