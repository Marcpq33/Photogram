package com.photogram.feature.profile

import android.net.Uri

sealed interface ProfileUiAction {
    data object SettingsClicked : ProfileUiAction
    data object HomeNavClicked : ProfileUiAction
    data object GalleryNavClicked : ProfileUiAction
    data object CreateClicked : ProfileUiAction
    data object ChatNavClicked : ProfileUiAction
    data object NewPostDismissed : ProfileUiAction
    data object NewPostCameraClicked : ProfileUiAction
    data class  NewPostMediaSelected(val uri: Uri) : ProfileUiAction
    data object FavoritesClicked : ProfileUiAction
    data class RecapClicked(val id: String) : ProfileUiAction
    data class AlbumClicked(val id: String) : ProfileUiAction
}
