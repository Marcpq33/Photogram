package com.photogram.feature.privacy

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PrivacyViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PrivacyUiState())
    val uiState: StateFlow<PrivacyUiState> = _uiState.asStateFlow()

    fun onAction(action: PrivacyUiAction) {
        when (action) {
            is PrivacyUiAction.ProfileModeSelected ->
                _uiState.update { it.copy(profileMode = action.mode) }
            is PrivacyUiAction.ShowPublicPhotosToggled ->
                _uiState.update { it.copy(showPublicPhotos = action.enabled) }
            is PrivacyUiAction.ShowStatsToggled ->
                _uiState.update { it.copy(showStats = action.enabled) }
            is PrivacyUiAction.RequireApprovalToggled ->
                _uiState.update { it.copy(requireApproval = action.enabled) }
            PrivacyUiAction.SaveChangesClicked -> { /* handled in future milestones */ }
        }
    }
}
