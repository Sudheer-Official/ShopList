package uk.ac.tees.mad.shoplist.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import uk.ac.tees.mad.shoplist.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity
import uk.ac.tees.mad.shoplist.ui.viewmodels.ShoppingListViewModel
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditListScreen(
    listId: Int,
    onBackClick: () -> Unit,
    shoppingListViewModel: ShoppingListViewModel = koinViewModel<ShoppingListViewModel>()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                Text(
                    text = if(listId == 0) stringResource(R.string.create_list) else stringResource(R.string.edit_list), fontWeight = FontWeight.Bold
                )
            }, navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
            )
        }, containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        AddEditListScreenContent(
            listId = listId,
            modifier = Modifier.padding(paddingValues),
            shoppingListViewModel = shoppingListViewModel,
            onBackClick = onBackClick,
        )
    }
}

@Composable
fun AddEditListScreenContent(
    listId: Int,
    modifier: Modifier = Modifier,
    shoppingListViewModel: ShoppingListViewModel,
    onBackClick: () -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Home") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val categories = listOf("Home", "Personal", "Food")
    val shoppingList by shoppingListViewModel.shoppingList.collectAsStateWithLifecycle()
    // Load data if editing
    if(listId != 0) {
        //TODO: Load the list information from the DB and assign it to title and category
        shoppingListViewModel.getShoppingListById(listId)
        title = shoppingList?.title ?: ""
        category = shoppingList?.category ?: "Home"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("List Title") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )
        Box(modifier = Modifier
            .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = { },
                label = { Text("Category") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { isDropdownExpanded = !isDropdownExpanded }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.ArrowDropDown,
                            contentDescription = "Category Dropdown"
                        )
                    }
                }
            )
            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(text = { Text(text = cat) }, onClick = {
                        category = cat
                        isDropdownExpanded = false
                    })
                }
            }
        }

        Button(
            onClick = {
                val shoppingList = if (listId == 0) {
                    val sdf = SimpleDateFormat("MMMM d, yyyy | hh:mm a")
                    val currentDateAndTime = sdf.format(System.currentTimeMillis())
                    ShoppingListEntity(title = title, itemCount = 0, completedItems = 0, lastModified = currentDateAndTime, category = category)
                } else {
                    //TODO: Get the id from the DB
                    val sdf = SimpleDateFormat("MMMM d, yyyy | hh:mm a")
                    val currentDateAndTime = sdf.format(System.currentTimeMillis())
                    ShoppingListEntity(id = listId, title = title, itemCount = 0, completedItems = 0, lastModified = currentDateAndTime, category = category)
                }
                if (listId == 0) {
                    shoppingListViewModel.insertShoppingList(shoppingList)
                } else {
                    shoppingListViewModel.updateShoppingList(shoppingList)
                }
                onBackClick() // Go back after saving
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Save")
        }
    }
}