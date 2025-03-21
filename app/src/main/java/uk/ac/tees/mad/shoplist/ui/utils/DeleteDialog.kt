package uk.ac.tees.mad.shoplist.ui.utils

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity

@Composable
fun DeleteDialog(
    onDismiss: () -> Unit, onConfirm: () -> Unit, shopingList: ShoppingListEntity
) {
    AlertDialog(
        onDismissRequest = {
        onDismiss()
    },
        title = { Text(text = "Confirm Delete") },
        text = { Text(text = "Are you sure you want to delete ${shopingList.title}?") },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) { Text("Dismiss") }
        })
}