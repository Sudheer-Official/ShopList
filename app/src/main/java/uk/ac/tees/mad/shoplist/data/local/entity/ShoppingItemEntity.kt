package uk.ac.tees.mad.shoplist.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_items", foreignKeys = [ForeignKey(
        entity = ShoppingListEntity::class,
        parentColumns = ["id"],
        childColumns = ["listId"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index("listId")]
)
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var firestoreId: String = "",
    val userId: String = "",
    val name: String = "",
    val quantity: Int = 1,
    val isPurchased: Boolean = false,
    val listId: Int, // Foreign key referencing ShoppingListEntity.id
    val listFirestoreId: String = "",
    val isDeleted: Boolean = false,
    val needsUpdate: Boolean = false
)