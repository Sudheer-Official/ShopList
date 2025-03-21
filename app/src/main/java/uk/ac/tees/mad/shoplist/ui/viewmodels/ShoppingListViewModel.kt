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

class ShoppingListViewModel(
    private val shoppingListRepository: ShoppingListRepository
) : ViewModel() {

    init {
        getAllShoppingLists()
    }

    private val _allShoppingLists = MutableStateFlow<List<ShoppingListEntity>>(emptyList())
    val allShoppingLists: StateFlow<List<ShoppingListEntity>> = _allShoppingLists.asStateFlow()

    private val _shoppingList = MutableStateFlow<ShoppingListEntity?>(null)
    val shoppingList: StateFlow<ShoppingListEntity?> = _shoppingList.asStateFlow()

    // Function to retrieve all shopping lists
    fun getAllShoppingLists() {
        viewModelScope.launch {
            shoppingListRepository.getAllShoppingLists().collectLatest { shoppingLists ->
                _allShoppingLists.value = shoppingLists
            }
        }
    }

    fun getShoppingListById(id: Int) {
        viewModelScope.launch {
            shoppingListRepository.getShoppingListById(id).collectLatest { shoppingList ->
                _shoppingList.value = shoppingList
            }
        }
    }

    // Function to insert a new shopping list
    fun insertShoppingList(shoppingList: ShoppingListEntity) {
        viewModelScope.launch {
            shoppingListRepository.insertShoppingList(shoppingList)
        }
    }

    // Function to update an existing shopping list
    fun updateShoppingList(shoppingList: ShoppingListEntity) {
        viewModelScope.launch {
            shoppingListRepository.updateShoppingList(shoppingList)
        }
    }

    // Function to delete a shopping list
    fun deleteShoppingList(shoppingList: ShoppingListEntity) {
        viewModelScope.launch {
            shoppingListRepository.deleteShoppingList(shoppingList)
        }
    }

}