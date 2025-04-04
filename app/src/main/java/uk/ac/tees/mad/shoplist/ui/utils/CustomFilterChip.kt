package uk.ac.tees.mad.shoplist.ui.utils

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun CustomFilterChip(icon: ImageVector, text: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected, onClick = {
        onClick()
    }, label = { Text(text) }, leadingIcon = if (selected) {
        {
            Icon(
                imageVector = icon,
                contentDescription = text,
            )
        }
    } else {
        null
    })
}