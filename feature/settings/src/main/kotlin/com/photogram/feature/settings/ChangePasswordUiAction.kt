package com.photogram.feature.settings

sealed interface ChangePasswordUiAction {
    data class CurrentPasswordChanged(val value: String)  : ChangePasswordUiAction
    data class NewPasswordChanged(val value: String)      : ChangePasswordUiAction
    data class ConfirmPasswordChanged(val value: String)  : ChangePasswordUiAction
    data object ToggleCurrentVisibility                   : ChangePasswordUiAction
    data object ToggleNewVisibility                       : ChangePasswordUiAction
    data object ToggleConfirmVisibility                   : ChangePasswordUiAction
    data object SaveClicked                               : ChangePasswordUiAction
}
