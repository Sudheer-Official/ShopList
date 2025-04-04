package uk.ac.tees.mad.shoplist.data.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.shoplist.data.remote.FirebaseAuthResult
import uk.ac.tees.mad.shoplist.data.remote.UserDetails

interface FirebaseAuthRepository {
    fun signUp(email: String, pass: String): Flow<FirebaseAuthResult<Boolean>>
    fun signIn(email: String, pass: String): Flow<FirebaseAuthResult<Boolean>>
    fun isSignedIn(): Boolean
    fun getCurrentUserId(): String?
    fun signOut()
    fun getCurrentUserDetails(): Flow<FirebaseAuthResult<UserDetails>>
    fun updateDisplayName(displayName: String): Flow<FirebaseAuthResult<Boolean>>
}