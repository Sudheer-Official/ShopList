package uk.ac.tees.mad.shoplist.data.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.shoplist.data.local.dao.ShoppingItemDao
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingItemEntity

class ShoppingItemRepositoryImpl(private val shoppingItemDao: ShoppingItemDao) :
    ShoppingItemRepository {

    override suspend fun insertShoppingItem(shoppingItem: ShoppingItemEntity) {
        shoppingItemDao.insert(shoppingItem)
    }

    override suspend fun updateShoppingItem(shoppingItem: ShoppingItemEntity) {
        shoppingItemDao.update(shoppingItem)
    }

    override suspend fun deleteShoppingItem(shoppingItem: ShoppingItemEntity) {
        shoppingItemDao.delete(shoppingItem)
    }

    override fun getShoppingItemsByListId(listId: Int): Flow<List<ShoppingItemEntity>> {
        return shoppingItemDao.getShoppingItemsByListId(listId)
    }

    override fun getAllShoppingItems(): Flow<List<ShoppingItemEntity>> {
        return shoppingItemDao.getAllShoppingItems()
    }

    override suspend fun deleteAllPurchasedItemsForList(listId: Int) {
        shoppingItemDao.deleteAllPurchasedItemsForList(listId)
    }
}