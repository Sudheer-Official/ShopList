package uk.ac.tees.mad.shoplist.data.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.shoplist.data.local.dao.ShoppingListDao
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity

class ShoppingListRepositoryImpl(private val shoppingListDao: ShoppingListDao) :
    ShoppingListRepository {

    override suspend fun insertShoppingList(shoppingList: ShoppingListEntity) {
        shoppingListDao.insert(shoppingList)
    }

    override suspend fun updateShoppingList(shoppingList: ShoppingListEntity) {
        shoppingListDao.update(shoppingList)
    }

    override suspend fun deleteShoppingList(shoppingList: ShoppingListEntity) {
        shoppingListDao.delete(shoppingList)
    }

    override fun getAllShoppingListsForUser(userId: String): Flow<List<ShoppingListEntity>> {
        return shoppingListDao.getAllShoppingListsForUser(userId)
    }

    override fun getShoppingListById(id: Int): Flow<ShoppingListEntity> {
        return shoppingListDao.getShoppingListById(id)
    }

    override fun getShoppingListsByCategoryForUser(
        userId: String,
        category: String
    ): Flow<List<ShoppingListEntity>> {
        return shoppingListDao.getShoppingListsByCategoryForUser(userId, category)
    }

    override fun getListsForSync(): Flow<List<ShoppingListEntity>> {
        return shoppingListDao.getListsForSync()
    }

    override suspend fun updateFirestoreId(id: Int, firestoreId: String) {
        shoppingListDao.updateFirestoreId(id, firestoreId)
    }

}