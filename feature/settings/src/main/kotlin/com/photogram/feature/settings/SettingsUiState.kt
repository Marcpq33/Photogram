package com.photogram.feature.settings

data class SettingsUiState(
    val displayName: String = "Elena Marchetti",
    val email: String = "elena.marchetti@studio.it",
    val isDarkMode: Boolean = true,
    val selectedLanguageCode: String = "EN",
    val storageUsedGb: Float = 2.4f,
    val storageTotalGb: Float = 5f,
    val appVersion: String = "2.4.0",
    val showLogOutDialog: Boolean = false,
    val showDownloadDialog: Boolean = false,
)
