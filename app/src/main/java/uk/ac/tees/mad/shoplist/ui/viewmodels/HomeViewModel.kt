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
            shoppingListRepository.getAllShoppingLists().collectLatest { shoppingLists ->
                _allShoppingLists.value = shoppingLists
            }
        }
    }
}