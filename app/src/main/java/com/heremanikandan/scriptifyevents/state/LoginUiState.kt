package com.heremanikandan.scriptifyevents.state
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String = "",
    val passwordError: String = ""
)
