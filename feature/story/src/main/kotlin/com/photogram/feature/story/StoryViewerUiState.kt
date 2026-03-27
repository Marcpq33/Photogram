package com.photogram.feature.story

internal data class StoryItem(
    val id: String,
    val authorName: String,         // display name — e.g. "Elena Rossi"
    val authorUsername: String,
    val timeAgo: String,            // e.g. "2H"
    val mediaColorArgb: Long,       // gradient fill — placeholder until Coil loads real media
    val caption: String?,
    val dateLabel: String,          // e.g. "SEPTEMBER 14, 2024 • AMALFI COAST"
)

internal data class StoryViewerUiState(
    val albumId: String = "",
    val stories: List<StoryItem> = emptyList(),
    val currentIndex: Int = 0,
    val replyText: String = "",
    val reactionSent: String? = null,  // emoji — local feedback only, no backend yet
    val replySent: Boolean = false,    // local ack — no backend send yet
) {
    val currentStory: StoryItem? get() = stories.getOrNull(currentIndex)
}
