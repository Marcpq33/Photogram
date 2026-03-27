package com.photogram.feature.privacy

sealed interface PrivacyUiAction {
    data class ProfileModeSelected(val mode: ProfileMode) : PrivacyUiAction
    data class ShowPublicPhotosToggled(val enabled: Boolean) : PrivacyUiAction
    data class ShowStatsToggled(val enabled: Boolean) : PrivacyUiAction
    data class RequireApprovalToggled(val enabled: Boolean) : PrivacyUiAction
    data object SaveChangesClicked : PrivacyUiAction
}
