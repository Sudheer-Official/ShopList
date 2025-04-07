package uk.ac.tees.mad.shoplist.data.remote.synchronizer

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity
import uk.ac.tees.mad.shoplist.data.remote.FirestoreResult
import uk.ac.tees.mad.shoplist.data.repository.FirebaseAuthRepository
import uk.ac.tees.mad.shoplist.data.repository.FirestoreRepository
import uk.ac.tees.mad.shoplist.data.repository.ShoppingItemRepository
import uk.ac.tees.mad.shoplist.data.repository.ShoppingListRepository

class ShopListSynchronizer(
    private val shoppingListRepository: ShoppingListRepository,
    private val shoppingItemRepository: ShoppingItemRepository,
    private val firestoreRepository: FirestoreRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) {
    private var initialSyncCompleted = false

    fun startSync() {
        Log.d("ShopListSync", "ShopListSynchronizer started")
        CoroutineScope(Dispatchers.IO).launch {
            val userId = firebaseAuthRepository.getCurrentUserId()
            if (userId != null) {
                if (!initialSyncCompleted) {
                    syncFromFirestoreToLocal(userId)
                    initialSyncCompleted = true
                }
                shoppingListRepository.getListsForSync().collectLatest { lists ->
                    Log.d("ShopListSync", "Found ${lists.size} lists to sync")
                    lists.forEach { list ->
                        //syncShoppingItems(userId, list)
                        if (list.firestoreId.isBlank() && !list.isDeleted) {
                            firestoreRepository.addShoppingList(userId, list)
                                .collectLatest { firestoreResult ->
                                    when (firestoreResult) {
                                        is FirestoreResult.Success -> {
                                            Log.d(
                                                "ShopListSync",
                                                "Added list to Firestore with ID: ${firestoreResult.data}"
                                            )
                                            shoppingListRepository.updateFirestoreId(
                                                list.id, firestoreResult.data
                                            )
                                        }

                                        is FirestoreResult.Error -> {
                                            Log.e(
                                                "ShopListSync",
                                                "Error adding list to Firestore",
                                                firestoreResult.exception
                                            )
                                        }

                                        else -> {}
                                    }
                                }
                        } else if (list.needsUpdate) {
                            if (!list.isDeleted) {
                                firestoreRepository.updateShoppingList(userId, list)
                                    .collectLatest { firestoreResult ->
                                        if (firestoreResult is FirestoreResult.Success) {
                                            Log.d(
                                                "ShopListSync",
                                                "Updated list in Firestore"
                                            )
                                            shoppingListRepository.updateShoppingList(
                                                list.copy(
                                                    needsUpdate = false
                                                )
                                            )
                                        }
                                    }
                            }
                        } else if (list.isDeleted) {
                            firestoreRepository.deleteShoppingList(userId, list)
                                .collectLatest { firestoreResult ->
                                    if (firestoreResult is FirestoreResult.Success) {
                                        Log.d(
                                            "ShopListSync",
                                            "Deleted list from Firestore"
                                        )
                                        shoppingListRepository.deleteShoppingList(list)
                                    }
                                }
                        }
                    }
                }
            } else {
                Log.e("ShopListSync", "User not logged in")
            }
        }
    }

    private fun syncShoppingItems(userId: String, list: ShoppingListEntity) {
        Log.d("ShopListSync", "Syncing items for list: ${list.title}")
        CoroutineScope(Dispatchers.IO).launch {
            shoppingItemRepository.getItemsForSync(list.id).collectLatest { items ->
                items.forEach { item ->
                    if (item.firestoreId.isBlank() && !item.isDeleted) {
                        firestoreRepository.addShoppingItem(userId, list.firestoreId, item)
                            .collectLatest { result ->
                                if (result is FirestoreResult.Success) {
                                    Log.d(
                                        "ShopListSync",
                                        "Added item to Firestore with ID: ${result.data}"
                                    )
                                    shoppingItemRepository.updateFirestoreId(item.id, result.data)
                                } else if (result is FirestoreResult.Error) {
                                    Log.e(
                                        "ShopListSync",
                                        "Error adding item to Firestore",
                                        result.exception
                                    )
                                }
                            }

                    } else if (item.needsUpdate) {
                        if (!item.isDeleted) {
                            firestoreRepository.updateShoppingItem(userId, list.firestoreId, item)
                                .collectLatest {
                                    if (it is FirestoreResult.Success) {
                                        Log.d(
                                            "ShopListSync",
                                            "Updated item in Firestore"
                                        )
                                        shoppingItemRepository.updateShoppingItem(
                                            item.copy(
                                                needsUpdate = false
                                            )
                                        )
                                    }else if (it is FirestoreResult.Error){
                                        Log.e(
                                            "ShopListSync",
                                            "Error updating item in Firestore",
                                            it.exception
                                        )
                                    }
                                }
                        }
                    } else if (item.isDeleted) {
                        firestoreRepository.deleteShoppingItem(userId, list.firestoreId, item)
                            .collectLatest {
                                if (it is FirestoreResult.Success) {
                                    Log.d(
                                        "ShopListSync",
                                        "Deleted item from Firestore"
                                    )
                                    shoppingItemRepository.deleteShoppingItem(item)
                                }else if (it is FirestoreResult.Error){
                                    Log.e(
                                        "ShopListSync",
                                        "Error deleting item in Firestore",
                                        it.exception
                                    )
                                }
                            }
                    }
                }
            }
        }
    }


    private suspend fun syncFromFirestoreToLocal(userId: String) {
        Log.d("ShopListSync", "Syncing Firestore to Local DB for user: $userId")
        val localLists =
            shoppingListRepository.getAllShoppingListsForUser(userId).firstOrNull() ?: emptyList()
        val localItems =
            shoppingItemRepository.getAllShoppingItemsForUser(userId).firstOrNull() ?: emptyList()
        firestoreRepository.getShoppingLists(userId).collectLatest { firestoreResult ->
            when (firestoreResult) {
                is FirestoreResult.Success -> {
                    val firestoreLists = firestoreResult.data
                    firestoreLists.forEach { firestoreList ->
                        val localList =
                            localLists.find { it.firestoreId == firestoreList.firestoreId }
                        if (localList == null) {
                            Log.d("ShopListSync", "Adding list to local DB: ${firestoreList.title}")
                            shoppingListRepository.insertShoppingList(firestoreList)
                        } else if (firestoreList != localList) {
                            Log.d("ShopListSync", "Updating list in local DB: ${firestoreList.title}")
                            shoppingListRepository.updateShoppingList(firestoreList)
                        }
                        firestoreRepository.getShoppingItems(userId, firestoreList.firestoreId)
                            .collectLatest { firestoreItemResult ->
                                when (firestoreItemResult) {
                                    is FirestoreResult.Success -> {
                                        val firestoreItems = firestoreItemResult.data
                                        firestoreItems.forEach { firestoreItem ->
                                            val localItem = shoppingItemRepository.getShoppingItemByFirestoreId(userId,firestoreItem.firestoreId).firstOrNull()
                                            if (localItem == null) {
                                                Log.d("ShopListSync", "Adding item to local DB: ${firestoreItem.name}")
                                                shoppingItemRepository.insertShoppingItem(
                                                    firestoreItem.copy(listFirestoreId = firestoreList.firestoreId)
                                                )
                                            } else if (firestoreItem != localItem) {
                                                Log.d("ShopListSync", "Updating item in local DB: ${firestoreItem.name}")
                                                shoppingItemRepository.updateShoppingItem(
                                                    firestoreItem.copy(listFirestoreId = firestoreList.firestoreId)
                                                )
                                            }
                                        }
                                    }

                                    is FirestoreResult.Error -> {
                                        Log.e(
                                            "ShopListSync",

                                            "Error fetching items from Firestore",
                                            firestoreItemResult.exception
                                        )
                                    }

                                    else -> {}
                                }
                            }
                    }

                }

                is FirestoreResult.Error -> {
                    Log.e(
                        "ShopListSync",
                        "Error fetching lists from Firestore",
                        firestoreResult.exception
                    )
                }

                else -> {}
            }
        }
    }
}
