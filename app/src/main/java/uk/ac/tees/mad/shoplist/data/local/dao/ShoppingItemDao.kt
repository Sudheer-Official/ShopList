package uk.ac.tees.mad.shoplist.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingItemEntity

@Dao
interface ShoppingItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shoppingItem: ShoppingItemEntity)

    @Update
    suspend fun update(shoppingItem: ShoppingItemEntity)

    @Delete
    suspend fun delete(shoppingItem: ShoppingItemEntity)

    @Query("SELECT * FROM shopping_items WHERE listId = :listId")
    fun getShoppingItemsByListId(listId: Int): Flow<List<ShoppingItemEntity>>

    @Query("SELECT * FROM shopping_items")
    fun getAllShoppingItems(): Flow<List<ShoppingItemEntity>>
}