package uk.ac.tees.mad.shoplist.ui.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun AddShoppingItemDialog(
    onDismiss: () -> Unit, onConfirm: (String, Int) -> Unit
) {
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("1") }
    AlertDialog(onDismissRequest = {
        onDismiss()

    }, confirmButton = {
        Button(
            onClick = {
                onConfirm(itemName, itemQuantity.toInt())
                itemName = ""
                itemQuantity = "1"
            },
            enabled = itemName.isNotBlank() && itemQuantity.isNotBlank() && itemQuantity.toIntOrNull() != null && itemQuantity.toInt() > 0
        ) {
            Text(text = "Add Item")
        }
    }, dismissButton = {
        Button(onClick = {
            onDismiss()
        }) {
            Text(text = "Cancel")
        }
    }, title = {
        Text(text = "Add Shopping Item")
    }, text = {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text(text = "Item Name") },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                isError = itemName.isBlank()
            )
            OutlinedTextField(
                value = itemQuantity,
                onValueChange = { itemQuantity = it },
                label = { Text(text = "Item Quantity") },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                isError = itemQuantity.isNotBlank() && itemQuantity.toIntOrNull() == null || itemQuantity.isNotBlank() && itemQuantity.toInt() <= 0
            )
        }
    })
}