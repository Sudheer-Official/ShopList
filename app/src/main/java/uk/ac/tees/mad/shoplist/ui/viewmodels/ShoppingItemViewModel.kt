package uk.ac.tees.mad.shoplist.ui.viewmodels

import android.content.Context
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
import uk.ac.tees.mad.shoplist.ui.utils.LoadingState
import uk.ac.tees.mad.shoplist.ui.utils.showNotification

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

    fun insertShoppingItem(shoppingItem: ShoppingItemEntity, listTitle: String, context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingItemRepository.insertShoppingItem(shoppingItem)
                showNotification(
                    context, "Item Added To List", "${shoppingItem.name} added to $listTitle"
                )
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

    fun deleteShoppingItem(shoppingItem: ShoppingItemEntity, listTitle: String, context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingItemRepository.deleteShoppingItem(shoppingItem)
                showNotification(
                    context,
                    "Item Deleted From List",
                    "${shoppingItem.name} deleted from $listTitle"
                )
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