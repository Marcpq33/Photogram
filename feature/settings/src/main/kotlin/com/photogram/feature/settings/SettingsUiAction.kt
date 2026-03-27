package com.photogram.feature.settings

sealed interface SettingsUiAction {
    data object EditProfileClicked    : SettingsUiAction
    data object PasswordClicked       : SettingsUiAction
    data object PrivacyClicked        : SettingsUiAction
    data class  DarkModeToggled(val enabled: Boolean) : SettingsUiAction
    data object LanguageClicked       : SettingsUiAction
    data object NotificationsClicked  : SettingsUiAction
    data object StorageClicked        : SettingsUiAction
    data object DownloadPhotosClicked : SettingsUiAction
    data object DownloadDismissed     : SettingsUiAction
    data object DownloadConfirmed     : SettingsUiAction
    data object LogOutClicked         : SettingsUiAction
    data object LogOutDismissed       : SettingsUiAction
    data object LogOutConfirmed       : SettingsUiAction
}
