package com.photogram.feature.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    fun onAction(action: EditProfileUiAction) {
        when (action) {
            is EditProfileUiAction.DisplayNameChanged -> _uiState.update { it.copy(displayName = action.value) }
            is EditProfileUiAction.UsernameChanged    -> _uiState.update { it.copy(username    = action.value) }
            is EditProfileUiAction.BioChanged         -> _uiState.update { it.copy(bio         = action.value) }
            EditProfileUiAction.SaveClicked           -> { /* placeholder — no persistence yet */ }
        }
    }
}
