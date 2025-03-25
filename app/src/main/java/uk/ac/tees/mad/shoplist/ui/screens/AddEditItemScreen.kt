package uk.ac.tees.mad.shoplist.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingItemEntity
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity
import uk.ac.tees.mad.shoplist.ui.utils.ListHeader
import uk.ac.tees.mad.shoplist.ui.utils.LoadingState
import uk.ac.tees.mad.shoplist.ui.viewmodels.AddEditItemViewModel
import uk.ac.tees.mad.shoplist.ui.viewmodels.ListDetailViewModel
import uk.ac.tees.mad.shoplist.ui.viewmodels.ShoppingItemViewModel
import uk.ac.tees.mad.shoplist.ui.viewmodels.ShoppingListViewModel
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(
    listId: Int,
    listTitle: String,
    onBackClick: () -> Unit,
    shoppingListViewModel: ShoppingListViewModel = koinViewModel<ShoppingListViewModel>(),
    shoppingItemViewModel: ShoppingItemViewModel = koinViewModel<ShoppingItemViewModel>(),
    addEditItemViewModel: AddEditItemViewModel = koinViewModel<AddEditItemViewModel>()
) {
    val shoppingList by addEditItemViewModel.shoppingList.collectAsStateWithLifecycle()
    val shoppingItems by addEditItemViewModel.shoppingItems.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        addEditItemViewModel.getShoppingListById(listId)
        addEditItemViewModel.getShoppingItemsByListId(listId)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                Text(
                    text = "Add/Edit Item in $listTitle", fontWeight = FontWeight.Bold
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
        AddEditItemContent(
            shoppingList = shoppingList,
            shoppingItems = shoppingItems,
            modifier = Modifier.padding(paddingValues),
            shoppingItemViewModel = shoppingItemViewModel,
            shoppingListViewModel = shoppingListViewModel
        )
    }
}

@Composable
fun AddEditItemContent(
    shoppingList: LoadingState<ShoppingListEntity>,
    shoppingItems: LoadingState<List<ShoppingItemEntity>>,
    modifier: Modifier = Modifier,
    shoppingItemViewModel: ShoppingItemViewModel,
    shoppingListViewModel: ShoppingListViewModel
) {

    when (val listState = shoppingList) {
        is LoadingState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text(text = "Error loading list")
            }
        }

        is LoadingState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is LoadingState.Success -> {
            when (val itemState = shoppingItems) {
                is LoadingState.Error -> {
                    Box(
                        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Error loading items")
                    }
                }

                is LoadingState.Loading -> {
                    Box(
                        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is LoadingState.Success -> {
                    val items = itemState.data
                    val list = listState.data

                    var showAddItemDialog by remember { mutableStateOf(false) }
                    var itemName by remember { mutableStateOf("") }
                    var itemQuantity by remember { mutableStateOf("") }

                    AnimatedVisibility(showAddItemDialog) {
                        var inputErrorI by remember { mutableStateOf(false) } // Flag for item name input error
                        var inputErrorQ by remember { mutableStateOf(false) } // Flag for item quantity input error
                        var inputErrorQNum by remember { mutableStateOf(false) } // Flag for item quantity input error
                        AlertDialog(onDismissRequest = {
                            showAddItemDialog = false
                            itemName = ""
                            itemQuantity = ""
                        }, confirmButton = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(onClick = {
                                    if (itemName.isNotBlank() && itemQuantity.isNotBlank()) {
                                        inputErrorQ = false
                                        if (itemQuantity.toIntOrNull() == null) {
                                            inputErrorQNum = true
                                        } else {
                                            inputErrorQNum = false

                                            // perform add item
                                            val shoppingItem = ShoppingItemEntity(
                                                listId = list.id,
                                                name = itemName,
                                                quantity = itemQuantity.toInt(),
                                                isPurchased = false
                                            )
                                            val sdf = SimpleDateFormat("MMMM d, yyyy | hh:mm a")
                                            val currentDateAndTime =
                                                sdf.format(System.currentTimeMillis())
                                            val shoppingList = ShoppingListEntity(
                                                id = list.id,
                                                title = list.title,
                                                itemCount = list.itemCount + 1,
                                                completedItems = list.completedItems,
                                                lastModified = currentDateAndTime,
                                                category = list.category)

                                            shoppingItemViewModel.insertShoppingItem(shoppingItem)
                                            shoppingListViewModel.updateShoppingList(shoppingList)

                                            showAddItemDialog = false
                                            itemName = ""
                                            itemQuantity = ""
                                        }
                                    } else {
                                        inputErrorI = itemName.isBlank()
                                        inputErrorQ = itemQuantity.isBlank()
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = Color.Green
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Add")
                                }
                                Button(onClick = { showAddItemDialog = false }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Cancel",
                                        tint = Color.Red
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Cancel")
                                }
                            }
                        }, title = {
                            Text(
                                text = "Add Shopping Item",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                minLines = 1
                            )
                        }, text = {
                            Column {
                                // Input field for item name
                                OutlinedTextField(value = itemName,
                                    onValueChange = { itemName = it },
                                    label = { Text(text = "Item Name") },
                                    isError = inputErrorI,
                                    supportingText = {
                                        if (inputErrorI) {
                                            Text(text = "Item Name cannot be empty")
                                        }
                                    },
                                    singleLine = true,
                                    maxLines = 1,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                )

                                // Input field for item quantity
                                OutlinedTextField(value = itemQuantity,
                                    onValueChange = { itemQuantity = it },
                                    label = { Text(text = "Item Quantity") },
                                    isError = inputErrorQ || inputErrorQNum,
                                    supportingText = {
                                        if (inputErrorQ) {
                                            Text(text = "Item Quantity cannot be empty")
                                        } else if (inputErrorQNum) {
                                            if (itemQuantity.isBlank()) {
                                                Text(text = "Item Quantity cannot be empty")
                                            } else if (itemQuantity.toIntOrNull() == null) {
                                                Text(text = "Item Quantity must be a number")
                                            } else {
                                                inputErrorQNum = false
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    maxLines = 1,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                )
                            }
                        })
                    }

                    LazyColumn(
                        modifier = modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            ListHeader(list)
                        }
                        item{
                            Button(
                                onClick = { showAddItemDialog = true },
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Add Item")
                            }
                        }
                        item{
                            if(items.isEmpty()){
                                Text(
                                    text = "No items to show",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        items(items) { item ->
                            Text("${item.name} - ${item.quantity}")
                        }
                    }
                }
            }
        }
    }

}