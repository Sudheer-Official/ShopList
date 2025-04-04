package uk.ac.tees.mad.shoplist.data.remote

sealed class FirebaseAuthResult<out T> {
    data object Loading : FirebaseAuthResult<Nothing>()
    data class Success<out T>(val data: T) : FirebaseAuthResult<T>()
    data class Error(val exception: Exception) : FirebaseAuthResult<Nothing>()
}