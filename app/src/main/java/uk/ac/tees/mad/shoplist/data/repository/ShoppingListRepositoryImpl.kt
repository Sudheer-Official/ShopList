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

    override fun getAllShoppingLists(): Flow<List<ShoppingListEntity>> {
        return shoppingListDao.getAllShoppingLists()
    }

    override fun getShoppingListById(id: Int): Flow<ShoppingListEntity> {
        return shoppingListDao.getShoppingListById(id)
    }

    override suspend fun updateLastModified(id: Int, lastModified: String) {
        shoppingListDao.updateLastModified(id, lastModified)
    }

    override fun getShoppingListsByCategory(category: String): Flow<List<ShoppingListEntity>> {
        return shoppingListDao.getShoppingListsByCategory(category)
    }
}