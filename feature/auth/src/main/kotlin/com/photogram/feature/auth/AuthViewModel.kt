package com.photogram.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photogram.core.common.PhotogramResult
import com.photogram.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    // Emits Unit after proto mode is written to DataStore so AuthScreen navigates
    // only after the flag is persisted — eliminates the race between setProtoMode()
    // and the destination ViewModel's userData.first() read.
    private val _devBypassNavEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val devBypassNavEvent: SharedFlow<Unit> = _devBypassNavEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.userData.collect { userData ->
                _uiState.update { it.copy(selectedLanguageCode = userData.language) }
            }
        }
    }

    fun onAction(action: AuthUiAction) {
        when (action) {
            is AuthUiAction.EmailChanged -> _uiState.update {
                it.copy(email = action.email, error = null)
            }
            is AuthUiAction.PasswordChanged -> _uiState.update {
                it.copy(password = action.password, error = null)
            }
            is AuthUiAction.FullNameChanged -> _uiState.update {
                it.copy(fullName = action.fullName, error = null)
            }
            AuthUiAction.ContinueClicked -> authenticate()
            AuthUiAction.ErrorDismissed -> _uiState.update { it.copy(error = null) }
            AuthUiAction.ToggleModeClicked -> _uiState.update {
                it.copy(isCreateMode = !it.isCreateMode, error = null)
            }
            AuthUiAction.TogglePasswordVisibility -> _uiState.update {
                it.copy(isPasswordVisible = !it.isPasswordVisible)
            }
            AuthUiAction.LanguageSelectorClicked -> _uiState.update {
                it.copy(isLanguageSheetVisible = true)
            }
            AuthUiAction.LanguageSheetDismissed -> _uiState.update {
                it.copy(isLanguageSheetVisible = false)
            }
            is AuthUiAction.LanguageSelected -> {
                _uiState.update { it.copy(selectedLanguageCode = action.code, isLanguageSheetVisible = false) }
                viewModelScope.launch { userPreferencesRepository.setLanguage(action.code) }
            }
            // TODO: integrate Google Sign-In SDK (e.g. Credential Manager + GoogleIdTokenRequestOptions)
            AuthUiAction.GoogleSignInClicked -> _uiState.update {
                val strings = AuthStrings.forCode(it.selectedLanguageCode)
                it.copy(error = strings.errorGoogleNotIntegrated)
            }
            // TODO: integrate Sign in with Apple via AppAuth or backend token exchange
            AuthUiAction.AppleSignInClicked -> _uiState.update {
                val strings = AuthStrings.forCode(it.selectedLanguageCode)
                it.copy(error = strings.errorAppleNotIntegrated)
            }
            // Dismissed from the "check your inbox" screen: reset to sign-in mode so the
            // user can sign in once they have confirmed their account.
            AuthUiAction.ConfirmationAcknowledged -> _uiState.update {
                it.copy(
                    pendingEmailConfirmation = false,
                    isCreateMode = false,
                    password = "",
                    error = null,
                )
            }

            // DEBUG only. Persists isProtoMode = true BEFORE emitting the nav event so that
            // destination ViewModels read isDemoMode = true on their first userData.first() call.
            AuthUiAction.DevBypassClicked -> viewModelScope.launch {
                userPreferencesRepository.setProtoMode(true)
                _devBypassNavEvent.emit(Unit)
            }
        }
    }

    private fun authenticate() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        if (email.isBlank() || password.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            if (_uiState.value.isCreateMode) {
                handleSignUp(email, password)
            } else {
                handleSignIn(email, password)
            }
        }
    }

    private suspend fun handleSignUp(email: String, password: String) {
        when (val result = authRepository.signUp(email, password)) {
            is PhotogramResult.Success -> when (result.data) {
                // Session established → AppViewModel.observeSession picks up
                // SessionStatus.Authenticated(isNew=true) and emits authEvent,
                // which PhotogramApp uses to navigate to MainGraph.
                SignUpOutcome.SessionEstablished -> _uiState.update { it.copy(isLoading = false) }
                // No session yet — Supabase "Confirm email" is enabled. Show the inbox screen.
                // AppViewModel.authEvent will fire once the user confirms and signs in.
                SignUpOutcome.AwaitingEmailConfirmation -> _uiState.update {
                    it.copy(isLoading = false, pendingEmailConfirmation = true)
                }
            }
            is PhotogramResult.Error -> _uiState.update {
                it.copy(
                    isLoading = false,
                    error = result.message ?: "An error occurred. Please try again.",
                )
            }
            is PhotogramResult.Loading -> Unit
        }
    }

    private suspend fun handleSignIn(email: String, password: String) {
        when (val result = authRepository.signIn(email, password)) {
            is PhotogramResult.Success -> {
                // Session established → AppViewModel handles navigation via authEvent.
                _uiState.update { it.copy(isLoading = false) }
            }
            is PhotogramResult.Error -> _uiState.update {
                it.copy(
                    isLoading = false,
                    error = result.message ?: "An error occurred. Please try again.",
                )
            }
            is PhotogramResult.Loading -> Unit
        }
    }
}
