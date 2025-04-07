package uk.ac.tees.mad.shoplist.data.repository

import androidx.compose.animation.core.copy
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingItemEntity
import uk.ac.tees.mad.shoplist.data.local.entity.ShoppingListEntity
import uk.ac.tees.mad.shoplist.data.remote.FirestoreResult
import kotlin.text.category
import kotlin.text.get
import kotlin.text.set

class FirestoreRepository(
    private val firestore: FirebaseFirestore
) {
    private fun getShopListListCollection(userId: String) =
        firestore.collection("users").document(userId).collection("lists")

    private fun getShopItemListCollection(userId: String) =
        firestore.collection("users").document(userId).collection("items")

    fun addShoppingList(userId: String, shoppingList: ShoppingListEntity): Flow<FirestoreResult<String>> = flow {
        emit(FirestoreResult.Loading)
        try {
            val shoppingListMap = shoppingList.toMapForFirestore()
            val documentReference = getShopListListCollection(userId).add(shoppingListMap).await()
            val firestoreId = documentReference.id
            emit(FirestoreResult.Success(firestoreId))
        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }

    fun getShoppingLists(userId: String): Flow<FirestoreResult<List<ShoppingListEntity>>> = flow {
        emit(FirestoreResult.Loading)
        try {
            val querySnapshot = getShopListListCollection(userId).orderBy("lastModified", Query.Direction.ASCENDING).get().await()
            val shoppingLists = querySnapshot.documents.mapNotNull { document ->
                document.toObject(ShoppingListEntity::class.java)?.copy(firestoreId = document.id)
            }
            emit(FirestoreResult.Success(shoppingLists))
        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }

    fun updateShoppingList(userId: String, shoppingList: ShoppingListEntity): Flow<FirestoreResult<Boolean>> = flow {
        emit(FirestoreResult.Loading)
        try {
            if(shoppingList.firestoreId.isNotBlank()){
                getShopListListCollection(userId).document(shoppingList.firestoreId).set(shoppingList.toMapForFirestore()).await()
                emit(FirestoreResult.Success(true))
            }
        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }

    fun deleteShoppingList(userId: String, shoppingList: ShoppingListEntity): Flow<FirestoreResult<Boolean>> = flow {
        emit(FirestoreResult.Loading)
        try {
            getShopListListCollection(userId).document(shoppingList.firestoreId).delete().await()
            emit(FirestoreResult.Success(true))
        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }

    fun addShoppingItem(userId: String, listFirestoreId: String, shoppingItem: ShoppingItemEntity): Flow<FirestoreResult<String>> = flow {
        emit(FirestoreResult.Loading)
        try {
            val shoppingItemMap = shoppingItem.toMapForFirestore()
            val documentReference = getShopItemListCollection(userId).add(shoppingItemMap).await()
            val firestoreId = documentReference.id
            emit(FirestoreResult.Success(firestoreId))
        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }

    fun getShoppingItems(userId: String, listFirestoreId: String): Flow<FirestoreResult<List<ShoppingItemEntity>>> = flow {
        emit(FirestoreResult.Loading)
        try {
            val querySnapshot = getShopItemListCollection(userId).get().await()
            val shoppingItems = querySnapshot.documents.mapNotNull { document ->
                document.toObject(ShoppingItemEntity::class.java)?.copy(firestoreId = document.id)
            }
            emit(FirestoreResult.Success(shoppingItems))
        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }

    fun updateShoppingItem(userId: String, listFirestoreId: String, shoppingItem: ShoppingItemEntity): Flow<FirestoreResult<Boolean>> = flow {
        emit(FirestoreResult.Loading)
        try {
            if(shoppingItem.firestoreId.isNotBlank()){
                getShopItemListCollection(userId).document(shoppingItem.firestoreId).set(shoppingItem.toMapForFirestore()).await()
                emit(FirestoreResult.Success(true))
            }
        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }

    fun deleteShoppingItem(userId: String, listFirestoreId: String, shoppingItem: ShoppingItemEntity): Flow<FirestoreResult<Boolean>> = flow {
        emit(FirestoreResult.Loading)
        try {
            getShopItemListCollection(userId).document(shoppingItem.firestoreId).delete().await()
            emit(FirestoreResult.Success(true))
        } catch (e: Exception) {
            emit(FirestoreResult.Error(e))
        }
    }
}

fun ShoppingListEntity.toMapForFirestore(): Map<String, Any?> {
    return mapOf(
        "userId" to userId,
        "firestoreId" to firestoreId,
        "title" to title,
        "itemCount" to itemCount,
        "completedItems" to completedItems,
        "lastModified" to lastModified,
        "category" to category,
    )
}

fun ShoppingItemEntity.toMapForFirestore(): Map<String, Any?> {
    return mapOf(
        "userId" to userId,
        "name" to name,
        "quantity" to quantity,
        "isPurchased" to isPurchased,
        "listId" to listId,
        "listFirestoreId" to listFirestoreId,
        "isDeleted" to isDeleted,
        "needsUpdate" to needsUpdate,
    )
}