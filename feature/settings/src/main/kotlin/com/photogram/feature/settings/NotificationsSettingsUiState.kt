package com.photogram.feature.settings

data class NotificationsSettingsUiState(
    val newPhotos: Boolean = true,
    val reactions: Boolean = true,
    val messages: Boolean = true,
    val albumInvites: Boolean = true,
    val albumUpdates: Boolean = false,
    val eventReminders: Boolean = true,
)
