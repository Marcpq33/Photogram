package com.photogram.feature.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun onAction(action: ChangePasswordUiAction) {
        when (action) {
            is ChangePasswordUiAction.CurrentPasswordChanged -> _uiState.update { it.copy(currentPassword    = action.value) }
            is ChangePasswordUiAction.NewPasswordChanged     -> _uiState.update { it.copy(newPassword        = action.value) }
            is ChangePasswordUiAction.ConfirmPasswordChanged -> _uiState.update { it.copy(confirmPassword    = action.value) }
            ChangePasswordUiAction.ToggleCurrentVisibility   -> _uiState.update { it.copy(showCurrentPassword = !it.showCurrentPassword) }
            ChangePasswordUiAction.ToggleNewVisibility       -> _uiState.update { it.copy(showNewPassword     = !it.showNewPassword) }
            ChangePasswordUiAction.ToggleConfirmVisibility   -> _uiState.update { it.copy(showConfirmPassword = !it.showConfirmPassword) }
            ChangePasswordUiAction.SaveClicked               -> { /* placeholder — no backend yet */ }
        }
    }
}
