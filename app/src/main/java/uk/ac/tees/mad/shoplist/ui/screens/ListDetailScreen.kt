package uk.ac.tees.mad.shoplist.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingItemEntity
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity
import uk.ac.tees.mad.shoplist.ui.utils.LoadingState
import uk.ac.tees.mad.shoplist.ui.utils.getCategoryColor
import uk.ac.tees.mad.shoplist.ui.viewmodels.ListDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    listId: Int,
    listTitle: String,
    onBackClick: () -> Unit,
    onAddClick: (Int, String) -> Unit,
    listDetailViewModel: ListDetailViewModel = koinViewModel<ListDetailViewModel>()
) {

    val shoppingList by listDetailViewModel.shoppingList.collectAsStateWithLifecycle()
    val shoppingItems by listDetailViewModel.shoppingItems.collectAsStateWithLifecycle()

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
                /* TODO: Navigate to Add Item Screen*/
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
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun ListDetailContent(
    shoppingList: LoadingState<ShoppingListEntity>,
    shoppingItems: LoadingState<List<ShoppingItemEntity>>,
    modifier: Modifier = Modifier
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
                        LazyColumn(
                            modifier = modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                ListHeader(list)
                            }
                            items(items) { item ->
                                ShoppingItemRow(item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListHeader(shoppingList: ShoppingListEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(getCategoryColor(shoppingList.category))
                ) {
                    Text(
                        text = shoppingList.title.first().toString(),
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${shoppingList.itemCount} items • ${shoppingList.completedItems} completed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Last modified: ${shoppingList.lastModified}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ShoppingItemRow(item: ShoppingItemEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (item.isPurchased) FontWeight.Normal else FontWeight.Medium,
                    color = if (item.isPurchased) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSurface
                )
                if (item.quantity > 1) {
                    Text(
                        text = "Qty: ${item.quantity}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Checkbox(
                checked = item.isPurchased, onCheckedChange = { }, colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
    }
}