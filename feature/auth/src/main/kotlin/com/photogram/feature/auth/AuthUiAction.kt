package com.photogram.feature.auth

import android.app.Activity

sealed interface AuthUiAction {
    data class EmailChanged(val email: String) : AuthUiAction
    data class PasswordChanged(val password: String) : AuthUiAction
    data class FullNameChanged(val fullName: String) : AuthUiAction
    data object ContinueClicked : AuthUiAction
    data object ErrorDismissed : AuthUiAction
    // Toggles sign-in ↔ create-account visual mode; no backend difference
    data object ToggleModeClicked : AuthUiAction
    data object TogglePasswordVisibility : AuthUiAction
    data object ToggleKeepSignedIn : AuthUiAction
    data object LanguageSelectorClicked : AuthUiAction
    data object LanguageSheetDismissed : AuthUiAction
    data class LanguageSelected(val code: String) : AuthUiAction
    // Activity required by Credential Manager to present the Google account picker sheet.
    data class GoogleSignInClicked(val activity: Activity) : AuthUiAction
    /**
     * Dismissed from the "check your inbox" screen shown after a signUp with email confirmation
     * required. Resets [AuthUiState.pendingEmailConfirmation] and switches to sign-in mode so the
     * user can sign in once they have confirmed their account.
     */
    data object ConfirmationAcknowledged : AuthUiAction

    /**
     * DEBUG only — "⚡ Dev bypass → Home" button.
     * Sets isProtoMode = true in DataStore, then emits a navigation event.
     * Navigation happens AFTER DataStore write so all destination ViewModels
     * read isDemoMode = true reliably.
     */
    data object DevBypassClicked : AuthUiAction
}
