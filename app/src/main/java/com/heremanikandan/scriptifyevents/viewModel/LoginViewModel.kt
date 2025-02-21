package com.heremanikandan.scriptifyevents.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heremanikandan.scriptifyevents.state.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

public class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChanged(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail, emailError = "")
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword, passwordError = "")
    }

    fun validateFields() {
        val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(_uiState.value.email).matches()
        val passwordValid = _uiState.value.password.length >= 6

        _uiState.value = _uiState.value.copy(
            emailError = if (!emailValid) "Invalid email format" else "",
            passwordError = if (!passwordValid) "Password must be at least 6 characters" else ""
        )
    }

    fun onSubmit() {
        viewModelScope.launch {
            validateFields()
            if (_uiState.value.emailError.isEmpty() && _uiState.value.passwordError.isEmpty()) {
                // Proceed with login logic
            }
        }
    }
}
