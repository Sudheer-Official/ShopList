package uk.ac.tees.mad.shoplist.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun getCategoryColor(category: String): Color {
    return when (category) {
        "Food" -> Color(0xFF4CAF50)
        "Home" -> Color(0xFF2196F3)
        "Personal" -> Color(0xFFE91E63)
        else -> Color(0xFF9E9E9E)
    }
}