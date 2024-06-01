package com.alexcao.starpx.screen.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexcao.starpx.model.Account
import com.alexcao.starpx.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    private val _loginUiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()
    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    private fun hasLoggedIn(): Boolean {
        return repository.getJwt() != null
    }

    init {
        val savedUsername = repository.getUsername()
        username = savedUsername

        viewModelScope.launch {
            if (hasLoggedIn()) {
                _loginUiState.update { it.copy(isSuccessful = true) }
            } else {
                _loginUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onUsernameChange(newUsername: String) {
        username = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun clearError() {
        _loginUiState.update { it.copy(error = null) }
    }
    fun resetState() {
        _loginUiState.update { LoginUiState() }
    }

    fun login() {
        Log.d("LoginViewModel", "Logging in with username: $username and password: $password")
        repository.saveUsername(username)
        viewModelScope.launch {
            _loginUiState.update { it.copy(isLoading = true) }
            try {
                repository.login(Account(username = username, password = password))
            } catch (e: Exception) {
                _loginUiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Login failed"
                    )
                }
                return@launch
            }
            _loginUiState.update { it.copy(isLoading = false, isSuccessful = true) }
        }
    }
}