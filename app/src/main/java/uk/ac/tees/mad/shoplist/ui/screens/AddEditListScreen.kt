package uk.ac.tees.mad.shoplist.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.shoplist.R
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity
import uk.ac.tees.mad.shoplist.ui.utils.LoadingState
import uk.ac.tees.mad.shoplist.ui.utils.getCategoryColor
import uk.ac.tees.mad.shoplist.ui.viewmodels.AddEditListViewModel
import uk.ac.tees.mad.shoplist.ui.viewmodels.ShoppingListViewModel
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditListScreen(
    listId: Int,
    onBackClick: () -> Unit,
    shoppingListViewModel: ShoppingListViewModel = koinViewModel<ShoppingListViewModel>(),
    addEditListViewModel: AddEditListViewModel = koinViewModel<AddEditListViewModel>()
) {
    val shoppingList by addEditListViewModel.shoppingList.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        addEditListViewModel.getShoppingListById(listId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                Text(
                    text = if (listId == 0) stringResource(R.string.create_list) else stringResource(
                        R.string.edit_list
                    ), fontWeight = FontWeight.Bold
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
            list = shoppingList,
            modifier = Modifier.padding(paddingValues),
            shoppingListViewModel = shoppingListViewModel,
            onBackClick = onBackClick,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditListScreenContent(
    listId: Int,
    list: LoadingState<ShoppingListEntity>,
    modifier: Modifier = Modifier,
    shoppingListViewModel: ShoppingListViewModel,
    onBackClick: () -> Unit,
) {
    when (val state = list) {
        is LoadingState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is LoadingState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text(text = "Error loading list")
            }
        }

        is LoadingState.Success -> {
            val categories = listOf("Food", "Home", "Personal", "Others")
            var title by remember { mutableStateOf(state.data.title) }
            var category by remember { mutableStateOf(if (state.data.category.isEmpty()) "Others" else state.data.category) }
            var expanded by remember { mutableStateOf(false) }
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(getCategoryColor(category))
                            ) {
                                if (title.isEmpty() || title.isBlank()) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_shoplist_logo),
                                        contentDescription = "ShopList Logo",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .align(Alignment.Center)
                                    )
                                } else {
                                    Text(
                                        text = title.first().toString(),
                                        modifier = Modifier.align(Alignment.Center),
                                        color = Color.White,
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("List Title") },
                                singleLine = true,
                                maxLines = 1,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = it },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = category,
                                    onValueChange = { },
                                    label = { Text("Category") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expanded
                                        )
                                    },
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(
                                            MenuAnchorType.PrimaryNotEditable
                                        )
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded, onDismissRequest = {
                                        expanded = false
                                    }, modifier = Modifier.fillMaxWidth()
                                ) {
                                    categories.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(text = cat) },
                                            onClick = {
                                                category = cat
                                                expanded = false
                                            },
                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (listId == 0) {
                                        val sdf = SimpleDateFormat("MMMM d, yyyy | hh:mm a")
                                        val currentDateAndTime =
                                            sdf.format(System.currentTimeMillis())
                                        val shoppingList = ShoppingListEntity(
                                            title = title!!,
                                            itemCount = 0,
                                            completedItems = 0,
                                            lastModified = currentDateAndTime,
                                            category = category
                                        )
                                        shoppingListViewModel.insertShoppingList(shoppingList)
                                        onBackClick()
                                    } else {
                                        val sdf = SimpleDateFormat("MMMM d, yyyy | hh:mm a")
                                        val currentDateAndTime =
                                            sdf.format(System.currentTimeMillis())
                                        val shoppingList = ShoppingListEntity(
                                            id = listId,
                                            title = title!!,
                                            itemCount = 0,
                                            completedItems = 0,
                                            lastModified = currentDateAndTime,
                                            category = category
                                        )
                                        shoppingListViewModel.updateShoppingList(shoppingList)
                                        onBackClick()
                                    }
                                },
                                enabled = !(title.isEmpty() || title.isBlank()),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (listId == 0) stringResource(R.string.create_list) else stringResource(
                                        R.string.update_list
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}