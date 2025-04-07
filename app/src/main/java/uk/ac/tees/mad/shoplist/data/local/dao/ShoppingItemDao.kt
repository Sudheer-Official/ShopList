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

    @Query("SELECT * FROM shopping_items WHERE userId = :userId AND listId = :listId AND isDeleted = 0 ORDER BY isPurchased ASC")
    fun getShoppingItemsByListIdForUser(userId: String, listId: Int): Flow<List<ShoppingItemEntity>>

    @Query("SELECT * FROM shopping_items WHERE (firestoreId ='' OR needsUpdate = 1 OR isDeleted= 1 OR firestoreId = '') AND listId = :listId")
    fun getItemsForSync(listId: Int): Flow<List<ShoppingItemEntity>>

    @Query("UPDATE shopping_items SET firestoreId = :firestoreId WHERE id = :id")
    suspend fun updateFirestoreId(id: Int, firestoreId: String)

    @Query("UPDATE shopping_items SET listFirestoreId = :listFirestoreId WHERE firestoreId = :firestoreId")
    suspend fun updateAllItemsListFirestoreId( firestoreId: String, listFirestoreId: String)

    @Query("UPDATE shopping_items SET isDeleted = 1 WHERE userId = :userId AND listId = :listId AND isPurchased = 1")
    suspend fun markAsDeletedOnAllPurchasedItemsForListForUser(userId: String, listId: Int)

    @Query("SELECT * FROM shopping_items WHERE userId = :userId")
    fun getAllShoppingItemsForUser(userId: String): Flow<List<ShoppingItemEntity>>

    @Query("SELECT * FROM shopping_items WHERE userId = :userId AND firestoreId = :firestoreId")
    fun getShoppingItemByFirestoreId(userId: String, firestoreId: String): Flow<ShoppingItemEntity?>
}