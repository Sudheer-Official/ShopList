package uk.ac.tees.mad.shoplist.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListActionsSheet(
    title: String,
    onDismissSheet: () -> Unit,
    onViewClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissSheet
    ) {
        ListItem(
            headlineContent = { Text("View List: $title") },
            leadingContent = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
            modifier = Modifier.clickable(
                onClick = onViewClick
            ),
            colors = ListItemDefaults.colors(
                containerColor = BottomSheetDefaults.ContainerColor
            )
        )
        ListItem(
            headlineContent = { Text("Edit List: $title") },
            leadingContent = { Icon(Icons.Default.Edit, contentDescription = null) },
            modifier = Modifier.clickable(
                onClick = onEditClick
            ),
            colors = ListItemDefaults.colors(
                containerColor = BottomSheetDefaults.ContainerColor
            )
        )
        ListItem(
            headlineContent = { Text("Delete List: $title") },
            leadingContent = { Icon(Icons.Default.Delete, contentDescription = null) },
            modifier = Modifier.clickable(
                onClick = onDeleteClick
            ),
            colors = ListItemDefaults.colors(
                containerColor = BottomSheetDefaults.ContainerColor
            )
        )
    }
}