package com.photogram.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photogram.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val userData = userPreferencesRepository.userData.first()
            _uiState.update {
                it.copy(
                    displayName = userData.displayName,
                    username    = userData.username,
                    bio         = userData.bio,
                    email       = userData.email,
                    avatarUri   = userData.avatarUri,
                    isLoading   = false,
                )
            }
        }
    }

    fun onAction(action: EditProfileUiAction) {
        when (action) {
            is EditProfileUiAction.DisplayNameChanged -> _uiState.update { it.copy(displayName = action.value) }
            is EditProfileUiAction.UsernameChanged    -> _uiState.update { it.copy(username    = action.value) }
            is EditProfileUiAction.BioChanged         -> _uiState.update { it.copy(bio         = action.value) }
            is EditProfileUiAction.AvatarSelected     -> _uiState.update { it.copy(avatarUri   = action.uri) }
            EditProfileUiAction.SaveClicked           -> saveProfile()
        }
    }

    private fun saveProfile() {
        val state = _uiState.value
        viewModelScope.launch {
            userPreferencesRepository.setProfile(
                displayName = state.displayName.trim(),
                username    = state.username.trim(),
                bio         = state.bio.trim(),
            )
            userPreferencesRepository.setAvatarUri(state.avatarUri)
            _uiState.update { it.copy(saveSuccess = true) }
            delay(2_500)
            _uiState.update { it.copy(saveSuccess = false) }
        }
    }
}
