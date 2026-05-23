package com.finpulse.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finpulse.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun updateUsername(v: String) = _state.update { it.copy(username = v, error = null) }
    fun updatePassword(v: String) = _state.update { it.copy(password = v, error = null) }
    fun updateDisplayName(v: String) = _state.update { it.copy(displayName = v, error = null) }

    fun login(onSuccess: (Long) -> Unit) {
        val s = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            authRepository.login(s.username, s.password)
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false) }
                    onSuccess(user.id)
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun register(onSuccess: (Long) -> Unit) {
        val s = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            authRepository.register(s.username, s.password, s.displayName)
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false) }
                    onSuccess(user.id)
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
