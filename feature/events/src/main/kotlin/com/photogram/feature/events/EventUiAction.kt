package com.photogram.feature.events

sealed interface EventUiAction {
    // ── Navigation ──────────────────────────────────────────────────────────────
    data object HomeNavClicked      : EventUiAction
    data object GalleryNavClicked   : EventUiAction
    data object CreateNavClicked    : EventUiAction
    data object ChatNavClicked      : EventUiAction
    data object ProfileNavClicked   : EventUiAction
    data object UploadPhotosClicked : EventUiAction
    data object GuestListClicked    : EventUiAction

    // ── Edit sheet lifecycle ─────────────────────────────────────────────────────
    data object EditEventClicked    : EventUiAction
    data object EditDismissed       : EventUiAction
    data object EditSaved           : EventUiAction

    // ── Draft field changes ──────────────────────────────────────────────────────
    data class DraftTitleChanged(val v: String)      : EventUiAction
    data class DraftDateChanged(val v: String)       : EventUiAction
    data class DraftLocationChanged(val v: String)   : EventUiAction
    data class DraftGuestCountChanged(val v: String) : EventUiAction

    // ── Timeline CRUD ────────────────────────────────────────────────────────────
    data object AddTimelineItem                                         : EventUiAction
    data class  RemoveTimelineItem(val id: String)                      : EventUiAction
    data class  DraftTimelineTimeChanged(val id: String, val v: String) : EventUiAction
    data class  DraftTimelineTitleChanged(val id: String, val v: String): EventUiAction
    data class  DraftTimelineLocationChanged(val id: String, val v: String): EventUiAction
}
