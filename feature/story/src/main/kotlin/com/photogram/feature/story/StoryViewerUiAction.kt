package com.photogram.feature.story

internal sealed interface StoryViewerUiAction {

    // ── Shared navigation / playback ──────────────────────────────────────────
    data object Close    : StoryViewerUiAction
    data object TapNext  : StoryViewerUiAction
    data object TapPrev  : StoryViewerUiAction

    // ── Others'-story: reactions + reply ──────────────────────────────────────
    data class ReactionTapped(val emoji: String) : StoryViewerUiAction
    data class ReplyTextChanged(val text: String) : StoryViewerUiAction
    data object SendReply : StoryViewerUiAction

    // ── Own story: viewers / activity sheet ───────────────────────────────────
    data object OpenViewers  : StoryViewerUiAction
    data object CloseViewers : StoryViewerUiAction

    // ── Own story: settings / management sheet ────────────────────────────────
    data object OpenSettings  : StoryViewerUiAction
    data object CloseSettings : StoryViewerUiAction
    data object DeleteStory   : StoryViewerUiAction
    data object ConfirmDelete : StoryViewerUiAction
    data object CancelDelete  : StoryViewerUiAction
    data object ArchiveStory  : StoryViewerUiAction
    data class  ConfirmArchive(val destination: String) : StoryViewerUiAction
    data object CancelArchive : StoryViewerUiAction
    data object MuteReplies   : StoryViewerUiAction

    // ── Own story: mention / tag users sheet ──────────────────────────────────
    data object OpenMentions  : StoryViewerUiAction
    data object CloseMentions : StoryViewerUiAction
    data class  MentionQueryChanged(val query: String) : StoryViewerUiAction
    data class  ToggleMentionUser(val userId: String)  : StoryViewerUiAction
    data object ConfirmMentions : StoryViewerUiAction

    // ── Own story: share sheet ────────────────────────────────────────────────
    data object OpenShare  : StoryViewerUiAction
    data object CloseShare : StoryViewerUiAction
}
