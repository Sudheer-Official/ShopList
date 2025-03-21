package uk.ac.tees.mad.shoplist.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity
import uk.ac.tees.mad.shoplist.data.repository.ShoppingListRepository

class AddEditListViewModel(
    private val shoppingListRepository: ShoppingListRepository
) : ViewModel() {

    private val _shoppingList =
        MutableStateFlow<LoadingState<ShoppingListEntity>>(LoadingState.Loading)
    val shoppingList: StateFlow<LoadingState<ShoppingListEntity>> = _shoppingList.asStateFlow()

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

}

sealed class LoadingState<out T> {
    object Loading : LoadingState<Nothing>()
    data class Success<out T>(val data: T) : LoadingState<T>()
    data class Error(val message: String) : LoadingState<Nothing>()
}