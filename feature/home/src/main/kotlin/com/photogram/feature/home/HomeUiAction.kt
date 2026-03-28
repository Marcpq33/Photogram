package com.photogram.feature.home

internal sealed interface HomeUiAction {
    data object BellClicked : HomeUiAction
    data object SearchClicked : HomeUiAction
    data object HomeNavClicked : HomeUiAction
    data class StoryClicked(val storyId: String) : HomeUiAction
    // Tapped the ADD NEW dashed bubble — open camera for story creation.
    data object AddNewStoryClicked : HomeUiAction
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
    // Fired only after the user completes a real/local story publish in Camera.
    data object StoryPublished : HomeUiAction
    // Invite welcome popup
    data object InviteWelcomeDismissed : HomeUiAction
    data object InviteLinkCopied : HomeUiAction
    // Fired on any share action — persists "shown" flag without closing the popup,
    // so the user can keep choosing other share options after returning to the app.
    data object InviteSharedOnce : HomeUiAction
}
