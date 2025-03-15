package uk.ac.tees.mad.shoplist.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_lists")
data class ShoppingListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val itemCount: Int,
    val completedItems: Int,
    val lastModified: String,
    val category: String
)