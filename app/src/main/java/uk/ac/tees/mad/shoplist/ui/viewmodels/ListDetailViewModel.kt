package uk.ac.tees.mad.shoplist.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingItemEntity
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity
import uk.ac.tees.mad.shoplist.data.repository.ShoppingItemRepository
import uk.ac.tees.mad.shoplist.data.repository.ShoppingListRepository
import uk.ac.tees.mad.shoplist.ui.utils.LoadingState

class ListDetailViewModel(
    private val shoppingListRepository: ShoppingListRepository,
    private val shoppingItemRepository: ShoppingItemRepository
) : ViewModel() {

    private val _shoppingList =
        MutableStateFlow<LoadingState<ShoppingListEntity>>(LoadingState.Loading)
    val shoppingList: StateFlow<LoadingState<ShoppingListEntity>> = _shoppingList.asStateFlow()

    private val _shoppingItems =
        MutableStateFlow<LoadingState<List<ShoppingItemEntity>>>(LoadingState.Loading)
    val shoppingItems: StateFlow<LoadingState<List<ShoppingItemEntity>>> =
        _shoppingItems.asStateFlow()

    fun getShoppingListById(id: Int = 0) {
        viewModelScope.launch {
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

    fun getShoppingItemsByListId(listId: Int) {
        viewModelScope.launch {
            shoppingItemRepository.getShoppingItemsByListId(listId).collectLatest { shoppingItems ->
                _shoppingItems.value = LoadingState.Success(shoppingItems)
            }
        }
    }

}