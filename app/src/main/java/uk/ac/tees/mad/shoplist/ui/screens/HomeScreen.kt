package uk.ac.tees.mad.shoplist.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onListClick: (Int) -> Unit,
    onAddListClick: () -> Unit
) {
    val sampleLists = listOf(
        ShoppingList(1, "Weekly Groceries", 8, 2, "Mar 22, 2025", "Food"),
        ShoppingList(2, "Hardware Supplies", 5, 0, "Mar 21, 2025", "Home"),
        ShoppingList(3, "Gift Shopping", 3, 1, "Mar 20, 2025", "Personal")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("ShopList", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddListClick,
                containerColor = MaterialTheme.colorScheme.secondary,
                shape = CircleShape,
                modifier = Modifier
                    .shadow(8.dp, CircleShape)
                    .size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new list",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        ShoppingListContent(
            shoppingLists = sampleLists,
            onListClick = onListClick,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun ShoppingListContent(
    shoppingLists: List<ShoppingList>,
    onListClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (shoppingLists.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No Shopping Lists Yet",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap + to create your first list",
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
            items(shoppingLists) { list ->
                ShoppingListItem(
                    list = list,
                    onClick = { onListClick(list.id) }
                )
            }
        }
    }
}

@Composable
fun ShoppingListItem(
    list: ShoppingList,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getCategoryColor(list.category))
            ) {
                Text(
                    text = list.title.first().toString(),
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = list.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "${list.itemCount} items • ${list.completedItems} done",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Last modified: ${list.lastModified}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun getCategoryColor(category: String): Color {
    return when (category) {
        "Food" -> Color(0xFF4CAF50)
        "Home" -> Color(0xFF2196F3)
        "Personal" -> Color(0xFFE91E63)
        else -> Color(0xFF9E9E9E)
    }
}