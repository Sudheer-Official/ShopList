package uk.ac.tees.mad.shoplist.data.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity

interface ShoppingListRepository {
    suspend fun insertShoppingList(shoppingList: ShoppingListEntity)
    suspend fun updateShoppingList(shoppingList: ShoppingListEntity)
    suspend fun deleteShoppingList(shoppingList: ShoppingListEntity)
    fun getAllShoppingLists(): Flow<List<ShoppingListEntity>>
    fun getShoppingListById(id: Int): Flow<ShoppingListEntity>
    suspend fun updateLastModified(id: Int, lastModified: String)
}