package uk.ac.tees.mad.shoplist.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import uk.ac.tees.mad.shoplist.data.local.dao.ShoppingItemDao
import uk.ac.tees.mad.shoplist.data.local.dao.ShoppingListDao
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingItemEntity
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity

@Database(
    entities = [ShoppingListEntity::class, ShoppingItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ShopListDatabase : RoomDatabase() {
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun shoppingItemDao(): ShoppingItemDao
}