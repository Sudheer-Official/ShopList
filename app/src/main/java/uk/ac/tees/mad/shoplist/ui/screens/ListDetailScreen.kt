package uk.ac.tees.mad.shoplist.ui.screens


import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.LabelOff
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingItemEntity
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity
import uk.ac.tees.mad.shoplist.ui.utils.ListHeader
import uk.ac.tees.mad.shoplist.ui.utils.LoadingState
import uk.ac.tees.mad.shoplist.ui.utils.RememberShakeSensor
import uk.ac.tees.mad.shoplist.ui.utils.getCurrentDateAndTime
import uk.ac.tees.mad.shoplist.ui.viewmodels.ListDetailViewModel
import uk.ac.tees.mad.shoplist.ui.viewmodels.ShoppingItemViewModel
import uk.ac.tees.mad.shoplist.ui.viewmodels.ShoppingListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    listId: Int,
    listTitle: String,
    onBackClick: () -> Unit,
    onAddClick: (Int, String) -> Unit,
    shoppingListViewModel: ShoppingListViewModel = koinViewModel<ShoppingListViewModel>(),
    shoppingItemViewModel: ShoppingItemViewModel = koinViewModel<ShoppingItemViewModel>(),
    listDetailViewModel: ListDetailViewModel = koinViewModel<ListDetailViewModel>()
) {

    val shoppingList by listDetailViewModel.shoppingList.collectAsStateWithLifecycle()
    val shoppingItems by listDetailViewModel.shoppingItems.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        listDetailViewModel.getShoppingListById(listId)
        listDetailViewModel.getShoppingItemsByListId(listId)
    }

    Scaffold(
        topBar = {
        TopAppBar(
            title = {
            Text(
                listTitle, fontWeight = FontWeight.Bold
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
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = {
                onAddClick(listId, listTitle)
            },
            containerColor = MaterialTheme.colorScheme.secondary,
            shape = CircleShape,
            modifier = Modifier
                .shadow(8.dp, CircleShape)
                .size(64.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add item",
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.size(32.dp)
            )
        }
    }, containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        ListDetailContent(
            shoppingList = shoppingList,
            shoppingItems = shoppingItems,
            modifier = Modifier.padding(paddingValues),
            shoppingListViewModel = shoppingListViewModel,
            shoppingItemViewModel = shoppingItemViewModel,
            context = context
        )
    }
}

@Composable
fun ListDetailContent(
    shoppingList: LoadingState<ShoppingListEntity>,
    shoppingItems: LoadingState<List<ShoppingItemEntity>>,
    modifier: Modifier = Modifier,
    shoppingListViewModel: ShoppingListViewModel,
    shoppingItemViewModel: ShoppingItemViewModel,
    context: Context
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
                    var checkedItems by remember { mutableIntStateOf(listState.data.completedItems) }
                    if (items.isEmpty()) {
                        Box(
                            modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No Items Yet",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap + to add items",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    } else {

                        RememberShakeSensor {
                            shoppingItemViewModel.deleteAllPurchasedItemsForList(
                                list.id,
                                list.title,
                                context
                            )
                            shoppingListViewModel.updateShoppingList(
                                list.copy(
                                    lastModified = getCurrentDateAndTime(),
                                    itemCount = list.itemCount - checkedItems,
                                    completedItems = 0
                                )
                            )
                        }

                        LazyColumn(
                            modifier = modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                ListHeader(list)
                            }
                            item {
                                Text(
                                    text = "Tip: Shake to delete all purchased items",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                            items(items, key = { it.id }) { item ->
                                ShoppingItemRow(
                                    item, onCheckedChange = { shoppingItem, isChecked ->
                                        checkedItems =
                                            if (isChecked) checkedItems + 1 else checkedItems - 1
                                        shoppingListViewModel.updateShoppingList(
                                            list.copy(
                                                lastModified = getCurrentDateAndTime(),
                                                completedItems = if (isChecked) list.completedItems + 1 else list.completedItems - 1
                                            )
                                        )
                                        shoppingItemViewModel.updateShoppingItem(
                                            shoppingItem.copy(isPurchased = isChecked)
                                        )
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ShoppingItemRow(
    item: ShoppingItemEntity, onCheckedChange: (ShoppingItemEntity, Boolean) -> Unit = { _, _ -> }
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        ListItem(leadingContent = {
            Icon(
                if (item.isPurchased) Icons.AutoMirrored.Filled.LabelOff else Icons.AutoMirrored.Filled.Label,
                contentDescription = "Localized description",
            )
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
            Checkbox(
                checked = item.isPurchased, onCheckedChange = {
                    onCheckedChange(item, it)
                }, colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.secondary
                )
            )
        })
    }
}