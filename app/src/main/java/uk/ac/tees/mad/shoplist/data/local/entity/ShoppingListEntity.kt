package uk.ac.tees.mad.shoplist.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import uk.ac.tees.mad.shoplist.ui.utils.getCurrentDateAndTime

@Entity(tableName = "shopping_lists")
data class ShoppingListEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var firestoreId: String = "",
    val userId: String = "",
    val title: String = "",
    val itemCount: Int = 0,
    val completedItems: Int = 0,
    val lastModified: String = getCurrentDateAndTime(),
    val category: String = "Others",
    val isDeleted: Boolean = false,
    val needsUpdate: Boolean = false
)