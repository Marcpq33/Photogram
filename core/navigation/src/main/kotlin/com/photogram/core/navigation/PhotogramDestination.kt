package com.photogram.core.navigation

sealed class PhotogramDestination(val route: String) {

    // ── Auth graph ────────────────────────────────────────────────────────────
    data object AuthGraph : PhotogramDestination("auth_graph")
    data object Login : PhotogramDestination("login")
    data object Register : PhotogramDestination("register")

    // ── Main graph ────────────────────────────────────────────────────────────
    data object MainGraph : PhotogramDestination("main_graph")
    data object Home : PhotogramDestination("home")
    data object Gallery : PhotogramDestination("gallery")
    data object Notifications : PhotogramDestination("notifications")
    data object Profile : PhotogramDestination("profile")
    data object Settings : PhotogramDestination("settings")
    data object Privacy              : PhotogramDestination("privacy")

    // ── Settings sub-screens ──────────────────────────────────────────────────
    data object EditProfile          : PhotogramDestination("edit_profile")
    data object ChangePassword       : PhotogramDestination("change_password")
    data object NotificationsSettings: PhotogramDestination("notifications_settings")
    data object LanguagePicker       : PhotogramDestination("language_picker")
    data object StorageDetail        : PhotogramDestination("storage_detail")

    // ── Camera / Create ───────────────────────────────────────────────────────
    data object Camera : PhotogramDestination("camera")

    // ── Albums ────────────────────────────────────────────────────────────────
    data object AlbumList : PhotogramDestination("albums")
    data object AlbumDetail : PhotogramDestination("album/{albumId}") {
        const val ARG_ALBUM_ID = "albumId"
        fun createRoute(albumId: String) = "album/$albumId"
    }

    // ── Media ─────────────────────────────────────────────────────────────────
    data object MediaViewer : PhotogramDestination("media/{mediaId}") {
        const val ARG_MEDIA_ID = "mediaId"
        fun createRoute(mediaId: String) = "media/$mediaId"
    }

    // ── Chat ──────────────────────────────────────────────────────────────────
    data object ChatList : PhotogramDestination("chat")
    data object ChatDetail : PhotogramDestination("chat/{albumId}") {
        const val ARG_ALBUM_ID = "albumId"
        fun createRoute(albumId: String) = "chat/$albumId"
    }

    // ── Stories ───────────────────────────────────────────────────────────────
    data object StoryViewer : PhotogramDestination("stories/{albumId}") {
        const val ARG_ALBUM_ID = "albumId"
        fun createRoute(albumId: String) = "stories/$albumId"
    }

    // ── Events ────────────────────────────────────────────────────────────────
    data object EventList : PhotogramDestination("events/{albumId}") {
        const val ARG_ALBUM_ID = "albumId"
        fun createRoute(albumId: String) = "events/$albumId"
    }

    // ── Recaps ────────────────────────────────────────────────────────────────
    data object RecapList : PhotogramDestination("recaps/{albumId}") {
        const val ARG_ALBUM_ID = "albumId"
        fun createRoute(albumId: String) = "recaps/$albumId"
    }
}
