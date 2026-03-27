package com.photogram.feature.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NotificationsSettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsSettingsUiState())
    val uiState: StateFlow<NotificationsSettingsUiState> = _uiState.asStateFlow()

    fun onAction(action: NotificationsSettingsUiAction) {
        when (action) {
            is NotificationsSettingsUiAction.NewPhotosToggled    -> _uiState.update { it.copy(newPhotos     = action.enabled) }
            is NotificationsSettingsUiAction.ReactionsToggled    -> _uiState.update { it.copy(reactions     = action.enabled) }
            is NotificationsSettingsUiAction.MessagesToggled     -> _uiState.update { it.copy(messages      = action.enabled) }
            is NotificationsSettingsUiAction.AlbumInvitesToggled -> _uiState.update { it.copy(albumInvites  = action.enabled) }
            is NotificationsSettingsUiAction.AlbumUpdatesToggled -> _uiState.update { it.copy(albumUpdates  = action.enabled) }
            is NotificationsSettingsUiAction.EventRemindersToggled -> _uiState.update { it.copy(eventReminders = action.enabled) }
        }
    }
}
