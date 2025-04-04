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
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity
import uk.ac.tees.mad.shoplist.data.repository.ShoppingListRepository
import uk.ac.tees.mad.shoplist.ui.utils.showNotification
import java.text.SimpleDateFormat
import java.util.Locale

class ShoppingListViewModel(
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
    fun getShoppingListsByCategory(category: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingListRepository.getShoppingListsByCategory(category)
                    .collectLatest { shoppingLists ->
                        _allShoppingLists.value = shoppingLists
                    }
            }
        }
    }

    // Function to insert a new shopping list
    fun insertShoppingList(shoppingList: ShoppingListEntity, context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingListRepository.insertShoppingList(shoppingList)
                showNotification(context, "List Created", "${shoppingList.title} created")
            }
        }
    }

    // Function to update an existing shopping list
    fun updateShoppingList(shoppingList: ShoppingListEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingListRepository.updateShoppingList(shoppingList)
            }
        }
    }

    // Function to delete a shopping list
    fun deleteShoppingList(shoppingList: ShoppingListEntity, context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shoppingListRepository.deleteShoppingList(shoppingList)
                showNotification(context, "List Deleted", "${shoppingList.title} deleted")
            }
        }
    }

    // Function to update last modified date of a shopping list
    fun updateLastModified(shoppingListId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val sdf = SimpleDateFormat("MMMM d, yyyy | hh:mm a", Locale.getDefault())
                val currentDateAndTime = sdf.format(System.currentTimeMillis())
                shoppingListRepository.updateLastModified(
                    shoppingListId, lastModified = currentDateAndTime
                )
            }
        }
    }

}