package uk.ac.tees.mad.shoplist.data.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity

interface ShoppingListRepository {
    suspend fun insertShoppingList(shoppingList: ShoppingListEntity)
    suspend fun updateShoppingList(shoppingList: ShoppingListEntity)
    suspend fun deleteShoppingList(shoppingList: ShoppingListEntity)
    fun getAllShoppingListsForUser(userId: String): Flow<List<ShoppingListEntity>>
    fun getShoppingListById(id: Int): Flow<ShoppingListEntity>
    fun getShoppingListsByCategoryForUser(userId: String, category: String): Flow<List<ShoppingListEntity>>
    fun getListsForSync(): Flow<List<ShoppingListEntity>>
    suspend fun updateFirestoreId(id: Int, firestoreId: String)
}