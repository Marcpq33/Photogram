package com.photogram.feature.story

internal sealed interface StoryViewerUiAction {
    data object Close : StoryViewerUiAction
    data object TapNext : StoryViewerUiAction
    data object TapPrev : StoryViewerUiAction
    data class ReactionTapped(val emoji: String) : StoryViewerUiAction
    data class ReplyTextChanged(val text: String) : StoryViewerUiAction
    data object SendReply : StoryViewerUiAction
}
