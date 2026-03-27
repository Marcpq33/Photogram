package com.photogram.feature.recaps

sealed interface RecapsUiAction {
    data object HomeNavClicked     : RecapsUiAction
    data object GalleryNavClicked  : RecapsUiAction
    data object CreateNavClicked   : RecapsUiAction
    data object ChatNavClicked     : RecapsUiAction
    data object ProfileNavClicked  : RecapsUiAction
    data object BackClicked        : RecapsUiAction
    data object SettingsClicked    : RecapsUiAction
    data object CreateRecapClicked : RecapsUiAction
    data class  RecapClicked(val id: String) : RecapsUiAction
}
