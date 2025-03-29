package uk.ac.tees.mad.shoplist.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingItemEntity
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity
import uk.ac.tees.mad.shoplist.ui.utils.AddShoppingItemDialog
import uk.ac.tees.mad.shoplist.ui.utils.DeleteDialogItem
import uk.ac.tees.mad.shoplist.ui.utils.EditShoppingItemDialog
import uk.ac.tees.mad.shoplist.ui.utils.ListHeader
import uk.ac.tees.mad.shoplist.ui.utils.LoadingState
import uk.ac.tees.mad.shoplist.ui.utils.getCurrentDateAndTime
import uk.ac.tees.mad.shoplist.ui.viewmodels.AddEditItemViewModel
import uk.ac.tees.mad.shoplist.ui.viewmodels.ShoppingItemViewModel
import uk.ac.tees.mad.shoplist.ui.viewmodels.ShoppingListViewModel

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
                    var showEditItemDialog by remember { mutableStateOf(false) }
                    var selectedItem by remember { mutableStateOf<ShoppingItemEntity?>(null) }
                    var showDeleteDialog by remember { mutableStateOf(false) }

                    AnimatedVisibility(showAddItemDialog) {
                        AddShoppingItemDialog(
                            onDismiss = { showAddItemDialog = false },
                            onConfirm = { name, quantity ->
                                val shoppingItem = ShoppingItemEntity(
                                    listId = list.id,
                                    name = name,
                                    quantity = quantity,
                                    isPurchased = false
                                )
                                shoppingItemViewModel.insertShoppingItem(shoppingItem)
                                shoppingListViewModel.updateShoppingList(
                                    list.copy(
                                        itemCount = list.itemCount + 1,
                                        lastModified = getCurrentDateAndTime()
                                    )
                                )
                                showAddItemDialog = false
                            })
                    }

                    AnimatedVisibility(showEditItemDialog) {
                        EditShoppingItemDialog(
                            onDismiss = {
                            showEditItemDialog = false
                        }, onConfirm = { name, quantity ->
                            selectedItem?.let {
                                shoppingItemViewModel.updateShoppingItem(
                                    it.copy(
                                        name = name, quantity = quantity
                                    )
                                )
                                shoppingListViewModel.updateShoppingList(
                                    list.copy(
                                        lastModified = getCurrentDateAndTime()
                                    )
                                )
                                showEditItemDialog = false
                            }
                        }, item = selectedItem!!
                        )
                    }

                    AnimatedVisibility(showDeleteDialog) {
                        DeleteDialogItem(
                            onDismiss = {
                            showDeleteDialog = false
                        }, onConfirm = {
                            selectedItem?.let {
                                shoppingItemViewModel.deleteShoppingItem(it)
                                shoppingListViewModel.updateShoppingList(
                                    list.copy(
                                        itemCount = list.itemCount - 1,
                                        completedItems = if (it.isPurchased) list.completedItems - 1 else list.completedItems,
                                        lastModified = getCurrentDateAndTime()
                                    )
                                )
                                showDeleteDialog = false
                            }
                        }, shopingItem = selectedItem!!
                        )
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
                        item {
                            Button(
                                onClick = { showAddItemDialog = true },
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Add Item")
                            }
                        }
                        item {
                            if (items.isEmpty()) {
                                Text(
                                    text = "No items to show",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        items(items, key = { it.id }) { item ->
                            //Text("${item.name} - ${item.quantity}")
                            ItemListRow(item, onCheckedChange = { shoppingItem, isChecked ->
                                shoppingListViewModel.updateShoppingList(
                                    list.copy(
                                        lastModified = getCurrentDateAndTime(),
                                        completedItems = if (isChecked) list.completedItems + 1 else list.completedItems - 1
                                    )
                                )
                                shoppingItemViewModel.updateShoppingItem(
                                    shoppingItem.copy(isPurchased = isChecked)
                                )
                            }, onEditClick = { item ->
                                selectedItem = item
                                showEditItemDialog = true
                            }, onDeleteClick = { item ->
                                selectedItem = item
                                showDeleteDialog = true
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemListRow(
    item: ShoppingItemEntity,
    onCheckedChange: (ShoppingItemEntity, Boolean) -> Unit = { _, _ -> },
    onEditClick: (ShoppingItemEntity) -> Unit = { _ -> },
    onDeleteClick: (ShoppingItemEntity) -> Unit = { _ -> }
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        ListItem(leadingContent = {
            Checkbox(
                checked = item.isPurchased, onCheckedChange = {
                    onCheckedChange(item, it)
                }, colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.secondary
                )
            )
//            Icon(
//                if(item.isPurchased) Icons.AutoMirrored.Filled.LabelOff else Icons.AutoMirrored.Filled.Label,
//                contentDescription = "Localized description",
//            )
        }, headlineContent = {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (item.isPurchased) FontWeight.Normal else FontWeight.Medium,
                color = if (item.isPurchased) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.onSurface
            )
        }, supportingContent = {
            if (item.quantity > 1) {
                Text(
                    text = "Qty: ${item.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }, trailingContent = {
            Row {
                IconButton(
                    onClick = {
                        onEditClick(item)
                    }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = {
                        onDeleteClick(item)
                    }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

        })
    }
}