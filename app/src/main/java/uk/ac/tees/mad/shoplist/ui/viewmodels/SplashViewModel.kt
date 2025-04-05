package uk.ac.tees.mad.shoplist.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.ac.tees.mad.shoplist.data.repository.FirebaseAuthRepository

class SplashViewModel(
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {
    private val _isSignedIn = MutableStateFlow(false)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn.asStateFlow()

    init {
        checkUserSignedIn()
    }

    fun checkUserSignedIn() {
        _isSignedIn.value = firebaseAuthRepository.isSignedIn()
    }

}