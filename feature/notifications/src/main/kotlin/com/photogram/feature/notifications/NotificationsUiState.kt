package com.photogram.feature.notifications

// ── Filter tabs ───────────────────────────────────────────────────────────────

internal enum class NotifFilter(val label: String) {
    ALL("All"),
    REACTIONS("Reactions"),
    NEW_PHOTOS("New photos"),
    COMMENTS("Comments"),
}

// ── Time groups ───────────────────────────────────────────────────────────────

internal enum class NotifGroup { TODAY, YESTERDAY, THIS_WEEK }

/** Display order of groups. */
internal val notifGroupOrder = listOf(
    NotifGroup.TODAY,
    NotifGroup.YESTERDAY,
    NotifGroup.THIS_WEEK,
)

// ── Notification category ─────────────────────────────────────────────────────

internal enum class NotifCategory { RECAP, REACTION, NEW_PHOTO, COMMENT }

// ── Right-side element variants ───────────────────────────────────────────────

internal enum class ThumbStyle {
    NONE,               // system notifications — no right element
    ROUND,              // single circular thumbnail
    STACKED_WITH_BADGE, // two overlapping circles + "+N" count
    FOLLOW_BUTTON,      // white-outlined "FOLLOW" pill
}

// ── Item model ────────────────────────────────────────────────────────────────

internal data class NotifItem(
    val id: String,
    val category: NotifCategory,
    val group: NotifGroup,
    /** Bold prefix (author name or "System"). Empty string → show no name prefix. */
    val displayName: String,
    /** Body text appended after displayName. */
    val body: String,
    /** Exact phrase within body that should render as italic serif. Null = none. */
    val bodyItalicPhrase: String? = null,
    val timestampLabel: String,
    val avatarColorArgb: Long,
    /** True → rounded-square system icon; false → circular user avatar. */
    val isSystemNotif: Boolean = false,
    /** Show terracotta unread dot on avatar. */
    val isUnread: Boolean = false,
    val thumbStyle: ThumbStyle = ThumbStyle.ROUND,
    val thumbColorArgb: Long = 0xFF2A2F3AL,
    /** Badge count for STACKED_WITH_BADGE style (e.g. 2 → "+2"). */
    val thumbExtraCount: Int = 0,
)

// ── Mock data — matches reference screenshot exactly ──────────────────────────

internal object NotifDefaults {
    val items = listOf(

        // ── TODAY ─────────────────────────────────────────────────────────────

        NotifItem(
            id               = "1",
            category         = NotifCategory.REACTION,
            group            = NotifGroup.TODAY,
            displayName      = "Elena Rossi",
            body             = " liked your photo from the Summer Solstice album.",
            bodyItalicPhrase = "Summer Solstice",
            timestampLabel   = "2 HOURS AGO",
            avatarColorArgb  = 0xFF8B7060L,   // warm brunette tones
            isUnread         = true,
            thumbStyle       = ThumbStyle.ROUND,
            thumbColorArgb   = 0xFF929FA8L,   // grey-blue — B&W landscape feel
        ),

        NotifItem(
            id              = "2",
            category        = NotifCategory.COMMENT,
            group           = NotifGroup.TODAY,
            displayName     = "Marcus Thorne",
            body            = " commented: \u201CThe lighting here is purely cinematic. Exceptional work.\u201D",
            timestampLabel  = "5 HOURS AGO",
            avatarColorArgb = 0xFF3A4A5AL,   // dark-suit, cooler tones
            isUnread        = true,
            thumbStyle      = ThumbStyle.ROUND,
            thumbColorArgb  = 0xFF252528L,   // near-black architectural photo
        ),

        // ── YESTERDAY ─────────────────────────────────────────────────────────

        NotifItem(
            id              = "3",
            category        = NotifCategory.REACTION,
            group           = NotifGroup.YESTERDAY,
            displayName     = "Sophie Chen",
            body            = " and 12 others reacted to your latest post.",
            timestampLabel  = "YESTERDAY, 14:20",
            avatarColorArgb = 0xFF5A7840L,   // auburn hair, green background
            isUnread        = false,
            thumbStyle      = ThumbStyle.STACKED_WITH_BADGE,
            thumbColorArgb  = 0xFF2A3A50L,   // dark ocean photo
            thumbExtraCount = 2,
        ),

        NotifItem(
            id               = "4",
            category         = NotifCategory.RECAP,
            group            = NotifGroup.YESTERDAY,
            displayName      = "System",
            body             = ": Your album Urban Textures was featured in today\u2019s curator picks.",
            bodyItalicPhrase = "Urban Textures",
            timestampLabel   = "YESTERDAY, 09:15",
            avatarColorArgb  = 0xFF1A1A22L,   // very dark for system icon
            isSystemNotif    = true,
            isUnread         = false,
            thumbStyle       = ThumbStyle.NONE,
        ),

        NotifItem(
            id              = "5",
            category        = NotifCategory.NEW_PHOTO,
            group           = NotifGroup.YESTERDAY,
            displayName     = "Julian Vane",
            body            = " started following your gallery.",
            timestampLabel  = "YESTERDAY, 07:00",
            avatarColorArgb = 0xFF2A2E38L,   // dark hair, dramatic portrait
            isUnread        = false,
            thumbStyle      = ThumbStyle.FOLLOW_BUTTON,
        ),
    )
}

// ── Screen state ──────────────────────────────────────────────────────────────

internal data class NotificationsUiState(
    val selectedFilter: NotifFilter = NotifFilter.ALL,
    val showFilterSheet: Boolean = false,
    val allItems: List<NotifItem> = emptyList(),
) {
    /** Items visible given the active filter tab. */
    val displayed: List<NotifItem>
        get() = when (selectedFilter) {
            NotifFilter.ALL        -> allItems
            NotifFilter.REACTIONS  -> allItems.filter { it.category == NotifCategory.REACTION }
            NotifFilter.NEW_PHOTOS -> allItems.filter { it.category == NotifCategory.NEW_PHOTO }
            NotifFilter.COMMENTS   -> allItems.filter { it.category == NotifCategory.COMMENT }
        }
}
