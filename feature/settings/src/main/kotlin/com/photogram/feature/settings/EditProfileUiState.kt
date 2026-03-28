package com.photogram.feature.settings

data class EditProfileUiState(
    val displayName: String = "",
    val username: String = "",
    val bio: String = "",
    val email: String = "",
    val avatarUri: String = "",
    val isLoading: Boolean = true,
    val saveSuccess: Boolean = false,
)
