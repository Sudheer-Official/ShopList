package uk.ac.tees.mad.shoplist.data.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingItemEntity

interface ShoppingItemRepository {
    suspend fun insertShoppingItem(shoppingItem: ShoppingItemEntity)
    suspend fun updateShoppingItem(shoppingItem: ShoppingItemEntity)
    suspend fun deleteShoppingItem(shoppingItem: ShoppingItemEntity)
    fun getShoppingItemsByListIdForUser(userId: String, listId: Int): Flow<List<ShoppingItemEntity>>
    fun getItemsForSync(listId: Int): Flow<List<ShoppingItemEntity>>
    suspend fun updateFirestoreId(id: Int, firestoreId: String)
    suspend fun markAsDeletedOnAllPurchasedItemsForListForUser(userId: String, listId: Int)
    fun getAllShoppingItemsForUser(userId: String): Flow<List<ShoppingItemEntity>>
    fun getShoppingItemByFirestoreId(userId: String, firestoreId: String): Flow<ShoppingItemEntity?>
    suspend fun updateAllItemsListFirestoreId(firestoreId: String, listFirestoreId: String)
}