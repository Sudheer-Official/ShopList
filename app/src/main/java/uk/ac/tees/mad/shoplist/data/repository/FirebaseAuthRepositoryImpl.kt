package uk.ac.tees.mad.shoplist.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.shoplist.data.remote.FirebaseAuthResult
import uk.ac.tees.mad.shoplist.data.remote.UserDetails

class FirebaseAuthRepositoryImpl(private val auth: FirebaseAuth) : FirebaseAuthRepository {
    override fun signUp(email: String, pass: String): Flow<FirebaseAuthResult<Boolean>> = flow {
        try {
            emit(FirebaseAuthResult.Loading)
            auth.createUserWithEmailAndPassword(email, pass).await()
            emit(FirebaseAuthResult.Success(true))
        } catch (e: Exception) {
            emit(FirebaseAuthResult.Error(e))
        }
    }

    override fun signIn(email: String, pass: String): Flow<FirebaseAuthResult<Boolean>> = flow {
        try {
            emit(FirebaseAuthResult.Loading)
            auth.signInWithEmailAndPassword(email, pass).await()
            emit(FirebaseAuthResult.Success(true))
        } catch (e: Exception) {
            emit(FirebaseAuthResult.Error(e))
        }
    }

    override fun isSignedIn(): Boolean {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already authenticated
            return true
        } else {
            // User is not authenticated
            return false
        }
    }

    override fun getCurrentUserId(): String? {
        val currentUser = auth.currentUser
        return currentUser?.uid
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUserDetails(): Flow<FirebaseAuthResult<UserDetails>> = flow {
        emit(FirebaseAuthResult.Loading)
        try {
            val currentUser: FirebaseUser? = auth.currentUser
            if (currentUser != null) {
                val userDetails = UserDetails(
                    userId = currentUser.uid,
                    email = currentUser.email,
                    displayName = currentUser.displayName,
                    isEmailVerified = currentUser.isEmailVerified,
                    phoneNumber = currentUser.phoneNumber,
                    photoUrl = currentUser.photoUrl
                )
                emit(FirebaseAuthResult.Success(userDetails))
            } else {
                emit(FirebaseAuthResult.Error(Exception("No user logged in")))
            }
        } catch (e: Exception) {
            emit(FirebaseAuthResult.Error(e))
        }
    }

    override fun updateDisplayName(displayName: String): Flow<FirebaseAuthResult<Boolean>> = flow {
        emit(FirebaseAuthResult.Loading)
        try {
            val user = auth.currentUser
            if (user != null) {
                val profileUpdates =
                    UserProfileChangeRequest.Builder().setDisplayName(displayName).build()

                user.updateProfile(profileUpdates).await()
                emit(FirebaseAuthResult.Success(true))
                Log.d("AuthRepository", "User display name updated.")
            } else {
                emit(FirebaseAuthResult.Error(Exception("No user logged in")))
            }
        } catch (e: Exception) {
            emit(FirebaseAuthResult.Error(e))
            Log.e("AuthRepository", "Failed to update user display name.", e)
        }
    }

}