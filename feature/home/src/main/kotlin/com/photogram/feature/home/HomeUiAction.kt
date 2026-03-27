package com.photogram.feature.home

internal sealed interface HomeUiAction {
    data object BellClicked : HomeUiAction
    data object SearchClicked : HomeUiAction
    data object HomeNavClicked : HomeUiAction
    data class StoryClicked(val storyId: String) : HomeUiAction
    data class AlbumClicked(val albumId: String) : HomeUiAction
    data object FeaturedMemoryClicked : HomeUiAction
    data object GalleryNavClicked : HomeUiAction
    data object CreateClicked : HomeUiAction
    data object ChatNavClicked : HomeUiAction
    data object ProfileNavClicked : HomeUiAction
    // Camera sheet
    data object CameraSheetDismissed : HomeUiAction
    data object CameraStoryTapped : HomeUiAction
    data object CameraGalleryTapped : HomeUiAction
    data object CameraAlbumTapped : HomeUiAction
}
