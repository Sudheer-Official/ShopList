package uk.ac.tees.mad.shoplist.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uk.ac.tees.mad.shoplist.data.remote.FirebaseAuthResult
import uk.ac.tees.mad.shoplist.data.repository.FirebaseAuthRepository

class SignUpViewModel(
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {
    private val _signUpResult =
        MutableStateFlow<FirebaseAuthResult<Boolean>>(FirebaseAuthResult.Success(false))
    val signUpResult: StateFlow<FirebaseAuthResult<Boolean>> = _signUpResult.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible = _isPasswordVisible.asStateFlow()

    private val _isSignUpMode = MutableStateFlow(true)
    val isSignUpMode = _isSignUpMode.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun switchSignUpMode() {
        _isSignUpMode.value = !_isSignUpMode.value
    }

    fun signUp(email: String, pass: String) {
        firebaseAuthRepository.signUp(email, pass).onEach { result ->
            _signUpResult.value = result
        }.launchIn(viewModelScope)
    }
}