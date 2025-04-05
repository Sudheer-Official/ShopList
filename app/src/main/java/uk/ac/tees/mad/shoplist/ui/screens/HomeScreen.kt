package uk.ac.tees.mad.shoplist.ui.screens


import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity
import uk.ac.tees.mad.shoplist.domain.ShoppingList
import uk.ac.tees.mad.shoplist.ui.utils.CustomFilterChip
import uk.ac.tees.mad.shoplist.ui.utils.DeleteDialogList
import uk.ac.tees.mad.shoplist.ui.utils.ShoppingListActionsSheet
import uk.ac.tees.mad.shoplist.ui.utils.getCategoryColor
import uk.ac.tees.mad.shoplist.ui.viewmodels.HomeViewModel
import uk.ac.tees.mad.shoplist.ui.viewmodels.ShoppingListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onListClick: (Int, String) -> Unit,
    onAddListClick: () -> Unit,
    onEditListClick: (Int) -> Unit,
    onLogOut: () -> Unit,
    shoppingListViewModel: ShoppingListViewModel = koinViewModel<ShoppingListViewModel>(),
    homeViewModel: HomeViewModel = koinViewModel<HomeViewModel>()
) {
    listOf(
        ShoppingList(1, "Weekly Groceries", 8, 2, "Mar 22, 2025", "Food"),
        ShoppingList(2, "Hardware Supplies", 5, 0, "Mar 21, 2025", "Home"),
        ShoppingList(3, "Gift Shopping", 3, 1, "Mar 20, 2025", "Personal")
    )
    val allShoppingLists by homeViewModel.allShoppingLists.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val categories = listOf("All", "Food", "Home", "Personal", "Others")
    var selectedCategory by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        homeViewModel.getAllShoppingLists()
    }

    LaunchedEffect(selectedCategory, allShoppingLists) {
        if (selectedCategory == "All") {
            homeViewModel.getAllShoppingLists()
        } else {
            homeViewModel.getShoppingListsByCategory(selectedCategory)
        }
    }

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
            actions = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = MaterialTheme.shapes.extraLarge,
                    onClick = {
                        homeViewModel.logOut()
                        onLogOut()
                    }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Log Out",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Log Out")
                }
            }
        )
    }, floatingActionButton = {
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
    }, containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        ShoppingListContent(
            shoppingLists = allShoppingLists,
            shoppingListViewModel = shoppingListViewModel,
            onListClick = onListClick,
            onEditListClick = onEditListClick,
            modifier = Modifier.padding(paddingValues),
            categories = categories,
            selectedCategory = selectedCategory,
            onFilterClick = {
                selectedCategory = it
            },
            context = context
        )
    }
}

@Composable
fun ShoppingListContent(
    shoppingLists: List<ShoppingListEntity>,
    shoppingListViewModel: ShoppingListViewModel,
    onListClick: (Int, String) -> Unit,
    onEditListClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    categories: List<String> = emptyList(),
    selectedCategory: String = "All",
    onFilterClick: (String) -> Unit,
    context: Context
) {
    val haptics = LocalHapticFeedback.current

    var longClickedList by remember { mutableStateOf<ShoppingListEntity?>(null) }

    var showListActionSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }


    AnimatedVisibility(showDeleteDialog) {
        DeleteDialogList(
            onDismiss = {
            showDeleteDialog = false
        }, onConfirm = {
            shoppingListViewModel.deleteShoppingList(longClickedList!!, context)
            showDeleteDialog = false
        }, shopingList = longClickedList!!
        )
    }

    if (shoppingLists.isEmpty() && categories.isNotEmpty() && selectedCategory == "All") {
        Box(
            modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
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
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (categories.isNotEmpty()) {
                        categories.forEach {
                            CustomFilterChip(
                                icon = Icons.Default.Done,
                                text = it,
                                selected = it == selectedCategory,
                                onClick = {
                                    onFilterClick(it)
                                })
                        }
                    }
                }
            }
            items(shoppingLists) {
                ShoppingListItem(
                    list = it,
                    onClick = { onListClick(it.id, it.title) },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        longClickedList = it
                        showListActionSheet = true
                    })
            }
            item {
                if (shoppingLists.isEmpty()) {
                    Box(
                        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "For $selectedCategory",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No Shopping Lists Yet",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap + to create Shopping List",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
        if (showListActionSheet && longClickedList != null) {
            ShoppingListActionsSheet(title = longClickedList!!.title, onDismissSheet = {
                showListActionSheet = false
                longClickedList = null
            }, onViewClick = {
                onListClick(longClickedList?.id ?: 0, longClickedList?.title ?: "")
                showListActionSheet = false
                longClickedList = null
            }, onEditClick = {
                onEditListClick(longClickedList?.id ?: 0)
                showListActionSheet = false
                longClickedList = null
            }, onDeleteClick = {
                showDeleteDialog = true
                showListActionSheet = false
            })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShoppingListItem(
    list: ShoppingListEntity, onClick: () -> Unit, onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick, onLongClick = onLongClick
            )
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