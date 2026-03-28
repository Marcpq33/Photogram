package com.photogram.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photogram.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val languageHolder: SettingsLanguageHolder,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Seed the holder with the persisted language so Settings and
            // LanguagePicker show the correct language on first open.
            val persisted = userPreferencesRepository.userData.first().language
            languageHolder.set(persisted)
            // Observe subsequent changes (from LanguagePickerViewModel).
            languageHolder.code.collect { code ->
                _uiState.update { it.copy(selectedLanguageCode = code) }
            }
        }
        viewModelScope.launch {
            // Observe real user profile data so the Settings header reflects
            // the authenticated user instead of hardcoded placeholder content.
            userPreferencesRepository.userData.collect { userData ->
                _uiState.update { it.copy(
                    displayName = userData.displayName,
                    email       = userData.email,
                    avatarUri   = userData.avatarUri,
                ) }
            }
        }
    }

    fun onAction(action: SettingsUiAction) {
        when (action) {
            is SettingsUiAction.DarkModeToggled   ->
                _uiState.update { it.copy(isDarkMode = action.enabled) }
            SettingsUiAction.LogOutClicked         ->
                _uiState.update { it.copy(showLogOutDialog = true) }
            SettingsUiAction.LogOutDismissed       ->
                _uiState.update { it.copy(showLogOutDialog = false) }
            SettingsUiAction.DownloadPhotosClicked ->
                _uiState.update { it.copy(showDownloadDialog = true) }
            SettingsUiAction.DownloadDismissed     ->
                _uiState.update { it.copy(showDownloadDialog = false) }
            SettingsUiAction.DownloadConfirmed     ->
                _uiState.update { it.copy(showDownloadDialog = false) }
            // Navigation handled via callbacks in SettingsScreen
            else -> Unit
        }
    }
}
