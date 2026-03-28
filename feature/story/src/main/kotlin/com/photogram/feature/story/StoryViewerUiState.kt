package com.photogram.feature.story

internal data class StoryItem(
    val id: String,
    val authorName: String,
    val authorUsername: String,
    val timeAgo: String,
    val mediaColorArgb: Long,
    /** Local URI of real captured/imported media. Null when using gradient placeholder. */
    val mediaUri: String? = null,
    val caption: String?,
    val dateLabel: String,
)

/**
 * A single viewer entry for the own-story activity sheet.
 * Local/mock — replaced when story repository is wired to backend.
 */
internal data class StoryViewerEntry(
    val displayName: String,
    val username: String,
    val timeAgo: String,
)

/**
 * A user that can be tagged/mentioned in the own story.
 * Local/mock — replaced when user search repository is wired.
 */
internal data class StoryMentionUser(
    val id: String,
    val displayName: String,
    val username: String,
    val selected: Boolean = false,
)

internal data class StoryViewerUiState(
    val albumId: String = "",
    val stories: List<StoryItem> = emptyList(),
    val currentIndex: Int = 0,

    // Others'-story interaction fields
    val replyText: String = "",
    val reactionSent: String? = null,
    val replySent: Boolean = false,

    // Own-story fields (only used when albumId == "my_story")
    val viewCount: Int = 0,
    val viewers: List<StoryViewerEntry> = emptyList(),
    val showViewersSheet: Boolean = false,
    val showSettingsSheet: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val showArchiveSheet: Boolean = false,
    val showMentionsSheet: Boolean = false,
    val showShareSheet: Boolean = false,
    val repliesMuted: Boolean = false,

    // Mention state
    val mentionQuery: String = "",
    /** Filtered subset shown in the search list. */
    val mentionCandidates: List<StoryMentionUser> = emptyList(),
    /** Full unfiltered list — used as the source for filtering. */
    val allMentionCandidates: List<StoryMentionUser> = emptyList(),
    /** Confirmed mentions attached to this story (local only). */
    val confirmedMentions: List<StoryMentionUser> = emptyList(),

    /** Set to true after DeleteStory — signals the screen to pop back. */
    val isDeleted: Boolean = false,
) {
    val currentStory: StoryItem? get() = stories.getOrNull(currentIndex)
    val isOwnStory: Boolean get() = albumId == "my_story"
}
