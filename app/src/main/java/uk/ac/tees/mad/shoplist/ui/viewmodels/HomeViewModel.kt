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
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity
import uk.ac.tees.mad.shoplist.data.repository.ShoppingListRepository

class HomeViewModel(
    private val shoppingListRepository: ShoppingListRepository
) : ViewModel() {
    init {
        getAllShoppingLists()
    }

    private val _allShoppingLists = MutableStateFlow<List<ShoppingListEntity>>(emptyList())
    val allShoppingLists: StateFlow<List<ShoppingListEntity>> = _allShoppingLists.asStateFlow()


    // Function to retrieve all shopping lists
    fun getAllShoppingLists() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingListRepository.getAllShoppingLists().collectLatest { shoppingLists ->
                    _allShoppingLists.value = shoppingLists
                }
            }
        }
    }

    // Function to retrieve shopping lists by category
    fun getShoppingListsByCategory(category: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                shoppingListRepository.getShoppingListsByCategory(category).collectLatest { shoppingLists ->
                    _allShoppingLists.value = shoppingLists
                }
            }
        }
    }
}