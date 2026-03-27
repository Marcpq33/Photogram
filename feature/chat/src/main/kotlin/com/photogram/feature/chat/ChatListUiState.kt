package com.photogram.feature.chat

// ── Filter tabs ───────────────────────────────────────────────────────────────

internal enum class ChatFilter(val label: String) {
    ALL("Todos"),
    VIAJES("Viajes"),
    EVENTOS("Eventos"),
    FAMILIA("Familia"),
}

// ── Preview type ──────────────────────────────────────────────────────────────

internal enum class PreviewType { TEXT, PHOTO, VOICE }

// ── Online status ─────────────────────────────────────────────────────────────

internal enum class OnlineStatus { ONLINE, OFFLINE, NONE }

// ── Item model ────────────────────────────────────────────────────────────────

internal data class ChatItem(
    val id: String,
    val albumName: String,
    val previewType: PreviewType,
    val previewText: String,
    val voiceDuration: String = "",
    val timestamp: String,
    val unreadCount: Int = 0,
    val onlineStatus: OnlineStatus = OnlineStatus.NONE,
    val coverColorArgb: Long,
    val filter: ChatFilter,
)

// ── Mock data ─────────────────────────────────────────────────────────────────

internal object ChatDefaults {
    val items = listOf(
        ChatItem(
            id              = "1",
            albumName       = "Neon Dreams 2024",
            previewType     = PreviewType.TEXT,
            previewText     = "Check out this lighting!",
            timestamp       = "14:28",
            unreadCount     = 3,
            onlineStatus    = OnlineStatus.ONLINE,
            coverColorArgb  = 0xFF7A8A72L,
            filter          = ChatFilter.VIAJES,
        ),
        ChatItem(
            id              = "2",
            albumName       = "Midnight Session",
            previewType     = PreviewType.VOICE,
            previewText     = "",
            voiceDuration   = "0:23",
            timestamp       = "11:05",
            unreadCount     = 1,
            onlineStatus    = OnlineStatus.ONLINE,
            coverColorArgb  = 0xFF1A1A22L,
            filter          = ChatFilter.EVENTOS,
        ),
        ChatItem(
            id              = "3",
            albumName       = "Summer Vibes '23",
            previewType     = PreviewType.TEXT,
            previewText     = "The group loved the new gallery...",
            timestamp       = "Yesterday",
            unreadCount     = 0,
            onlineStatus    = OnlineStatus.OFFLINE,
            coverColorArgb  = 0xFF6A7A58L,
            filter          = ChatFilter.FAMILIA,
        ),
        ChatItem(
            id              = "4",
            albumName       = "Mountain Retreat",
            previewType     = PreviewType.PHOTO,
            previewText     = "Sent a photo",
            timestamp       = "09:12",
            unreadCount     = 1,
            onlineStatus    = OnlineStatus.ONLINE,
            coverColorArgb  = 0xFFA09070L,
            filter          = ChatFilter.VIAJES,
        ),
        ChatItem(
            id              = "5",
            albumName       = "Street Photography",
            previewType     = PreviewType.TEXT,
            previewText     = "Alex: Ready for the meet?",
            timestamp       = "Tuesday",
            unreadCount     = 0,
            onlineStatus    = OnlineStatus.NONE,
            coverColorArgb  = 0xFF3A4838L,
            filter          = ChatFilter.VIAJES,
        ),
        ChatItem(
            id              = "6",
            albumName       = "The Wedding Shoots",
            previewType     = PreviewType.TEXT,
            previewText     = "Sarah: Those edits are fire!",
            timestamp       = "12 Oct",
            unreadCount     = 0,
            onlineStatus    = OnlineStatus.NONE,
            coverColorArgb  = 0xFF4A7A8AL,
            filter          = ChatFilter.EVENTOS,
        ),
        ChatItem(
            id              = "7",
            albumName       = "Midnight Solitude",
            previewType     = PreviewType.PHOTO,
            previewText     = "Sent a photo",
            timestamp       = "18:50",
            unreadCount     = 0,
            onlineStatus    = OnlineStatus.ONLINE,
            coverColorArgb  = 0xFF1A1020L,
            filter          = ChatFilter.VIAJES,
        ),
    )
}

// ── Screen state ──────────────────────────────────────────────────────────────

internal data class ChatListUiState(
    val selectedFilter: ChatFilter = ChatFilter.ALL,
    val searchQuery: String = "",
    val allItems: List<ChatItem> = emptyList(),
    val filters: List<ChatFilter> = listOf(
        ChatFilter.ALL,
        ChatFilter.VIAJES,
        ChatFilter.EVENTOS,
        ChatFilter.FAMILIA,
    ),
) {
    val displayed: List<ChatItem>
        get() {
            val filtered = when (selectedFilter) {
                ChatFilter.ALL -> allItems
                else           -> allItems.filter { it.filter == selectedFilter }
            }
            return if (searchQuery.isBlank()) filtered
            else filtered.filter { it.albumName.contains(searchQuery, ignoreCase = true) }
        }
}
