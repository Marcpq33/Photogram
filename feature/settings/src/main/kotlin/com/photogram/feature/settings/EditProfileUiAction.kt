package com.photogram.feature.settings

sealed interface EditProfileUiAction {
    data class DisplayNameChanged(val value: String) : EditProfileUiAction
    data class UsernameChanged(val value: String)    : EditProfileUiAction
    data class BioChanged(val value: String)         : EditProfileUiAction
    data object SaveClicked                          : EditProfileUiAction
}
