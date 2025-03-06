package uk.ac.tees.mad.shoplist.domain


data class ShoppingList(
    val id: Int,
    val title: String,
    val itemCount: Int,
    val completedItems: Int,
    val lastModified: String,
    val category: String
)