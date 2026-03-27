package com.photogram.feature.auth

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val fullName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCreateMode: Boolean = true,
    val isPasswordVisible: Boolean = false,
    val isLanguageSheetVisible: Boolean = false,
    val selectedLanguageCode: String = "EN",
    /**
     * True when signUp succeeded but Supabase requires email confirmation before a session
     * is issued ("Confirm email" enabled in dashboard). Shows a "check your inbox" screen.
     * Reset to false via [AuthUiAction.ConfirmationAcknowledged].
     */
    val pendingEmailConfirmation: Boolean = false,
)
