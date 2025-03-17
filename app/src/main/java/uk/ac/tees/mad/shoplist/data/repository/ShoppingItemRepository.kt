package uk.ac.tees.mad.shoplist.data.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingItemEntity

interface ShoppingItemRepository {
    suspend fun insertShoppingItem(shoppingItem: ShoppingItemEntity)
    suspend fun updateShoppingItem(shoppingItem: ShoppingItemEntity)
    suspend fun deleteShoppingItem(shoppingItem: ShoppingItemEntity)
    fun getShoppingItemsByListId(listId: Int): Flow<List<ShoppingItemEntity>>
    fun getAllShoppingItems(): Flow<List<ShoppingItemEntity>>
}