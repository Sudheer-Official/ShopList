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

    override fun getShoppingItemsByListIdForUser(
        userId: String,
        listId: Int
    ): Flow<List<ShoppingItemEntity>> {
        return shoppingItemDao.getShoppingItemsByListIdForUser(userId, listId)
    }

    override fun getItemsForSync(listId: Int): Flow<List<ShoppingItemEntity>> {
        return shoppingItemDao.getItemsForSync(listId)
    }

    override suspend fun updateFirestoreId(id: Int, firestoreId: String) {
        return shoppingItemDao.updateFirestoreId(id, firestoreId)
    }

    override suspend fun markAsDeletedOnAllPurchasedItemsForListForUser(
        userId: String,
        listId: Int
    ) {
        return shoppingItemDao.markAsDeletedOnAllPurchasedItemsForListForUser(userId, listId)
    }

    override fun getAllShoppingItemsForUser(userId: String): Flow<List<ShoppingItemEntity>> {
        return shoppingItemDao.getAllShoppingItemsForUser(userId)
    }

    override fun getShoppingItemByFirestoreId(userId: String, firestoreId: String): Flow<ShoppingItemEntity?> {
        return shoppingItemDao.getShoppingItemByFirestoreId(userId, firestoreId)
    }

    override suspend fun updateAllItemsListFirestoreId(firestoreId: String, listFirestoreId: String) {
        return shoppingItemDao.updateAllItemsListFirestoreId( firestoreId , listFirestoreId)
    }

}