package com.alexcao.starpx.screen.login

data class LoginUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSuccessful: Boolean = false,
)