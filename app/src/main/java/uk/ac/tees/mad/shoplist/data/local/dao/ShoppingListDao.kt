package uk.ac.tees.mad.shoplist.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity

@Dao
interface ShoppingListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shoppingList: ShoppingListEntity)

    @Update
    suspend fun update(shoppingList: ShoppingListEntity)

    @Delete
    suspend fun delete(shoppingList: ShoppingListEntity)

    @Query("SELECT * FROM shopping_lists WHERE userId = :userId AND isDeleted = 0 ORDER BY lastModified DESC")
    fun getAllShoppingListsForUser(userId: String): Flow<List<ShoppingListEntity>>

    @Query("SELECT * FROM shopping_lists WHERE id = :id")
    fun getShoppingListById(id: Int): Flow<ShoppingListEntity>

    @Query("SELECT * FROM shopping_lists WHERE userId = :userId AND category = :category AND isDeleted = 0")
    fun getShoppingListsByCategoryForUser(userId: String, category: String): Flow<List<ShoppingListEntity>>

    @Query("SELECT * FROM shopping_lists WHERE (firestoreId ='' OR needsUpdate = 1 OR isDeleted= 1)")
    fun getListsForSync(): Flow<List<ShoppingListEntity>>

    @Query("UPDATE shopping_lists SET firestoreId = :firestoreId WHERE id = :id")
    suspend fun updateFirestoreId(id: Int, firestoreId: String)

}