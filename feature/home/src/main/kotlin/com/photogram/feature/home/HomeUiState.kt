package com.photogram.feature.home

internal data class HomeUiState(
    val stories: List<HomeStory> = emptyList(),
    val featuredMemory: HomeFeaturedMemory? = null,
    val albums: List<HomeAlbum> = emptyList(),
    val unreadNotifications: Int = 0,
    val unreadMessages: Int = 0,
    val showCameraSheet: Boolean = false,
    val currentUserAvatarUrl: String? = null,
)

internal data class HomeStory(
    val id: String,
    val label: String,
    val coverColorArgb: Long,
    val isAddNew: Boolean = false,
    val imageUrl: String? = null,
)

internal data class HomeFeaturedMemory(
    val title: String,
    val subtitle: String,
    val date: String,
    val imageUrl: String? = null,
)

internal data class HomeAlbum(
    val id: String,
    val name: String,
    val tagLabel: String,
    val tagLine: String,
    val newCount: Int,
    val mediaCount: Int,
    val isNew: Boolean,
    val isPremium: Boolean,
    val isFullWidth: Boolean,
    val coverColorArgb: Long,
    val coverColorArgb2: Long = 0L,
    val imageUrl: String? = null,
)

// ── Unsplash CDN helpers ──────────────────────────────────────────────────────
private fun unsplash(id: String, w: Int, h: Int) =
    "https://images.unsplash.com/photo-$id?w=$w&h=$h&fit=crop&q=85&auto=format"

private fun portrait(id: String)  = unsplash(id, w = 200, h = 200)
private fun hero(id: String)      = unsplash(id, w = 900, h = 600)
private fun cover(id: String)     = unsplash(id, w = 500, h = 640)

internal object HomeDefaults {

    // User avatar shown in bottom-nav profile circle
    val userAvatarUrl: String = portrait("1494790108377-be9c29b29330")

    val stories = listOf(
        HomeStory(id = "0", label = "NEW",    coverColorArgb = 0xFF050505L, isAddNew = true),
        HomeStory(id = "1", label = "ALEX",   coverColorArgb = 0xFFB87A5AL,
            imageUrl = portrait("1494790108377-be9c29b29330")),   // woman, warm auburn
        HomeStory(id = "2", label = "JORDAN", coverColorArgb = 0xFF6A8CA0L,
            imageUrl = portrait("1500648767791-00dcc994a43e")),   // man, dark jacket
        HomeStory(id = "3", label = "TAYLOR", coverColorArgb = 0xFFC4956AL,
            imageUrl = portrait("1534528741775-53994a69daeb")),   // woman, smiling
        HomeStory(id = "4", label = "MORG",   coverColorArgb = 0xFF7A6A8AL,
            imageUrl = portrait("1506794778202-cad84cf45f1d")),   // man, neutral
    )

    val featuredMemory = HomeFeaturedMemory(
        title    = "One year ago today\u2026",
        subtitle = "Lost in the misty giants of the North.",
        date     = "SEPT 14, 2023",
        imageUrl = hero("1448375240586-882707db888b"),            // redwood forest, light rays
    )

    val albums = listOf(
        HomeAlbum(
            id = "1", name = "Alpine Serenity",
            tagLabel = "STREET SOUL", tagLine = "",
            newCount = 12, mediaCount = 128,
            isNew = false, isPremium = false, isFullWidth = false,
            coverColorArgb = 0xFF2A5038L, coverColorArgb2 = 0xFF0A1A12L,
            imageUrl = cover("1506905925346-21bda4d32df4"),       // alpine lake + forest
        ),
        HomeAlbum(
            id = "2", name = "Ethereal Mornings",
            tagLabel = "DREAMSCAPE", tagLine = "",
            newCount = 0, mediaCount = 55,
            isNew = false, isPremium = false, isFullWidth = false,
            coverColorArgb = 0xFF8BA8C0L, coverColorArgb2 = 0xFFD4987AL,
            imageUrl = cover("1470770903676-69b98201ea1c"),       // misty dawn landscape
        ),
        HomeAlbum(
            id = "3", name = "Neon Nights",
            tagLabel = "", tagLine = "",
            newCount = 4, mediaCount = 42,
            isNew = false, isPremium = false, isFullWidth = false,
            coverColorArgb = 0xFF1E3055L, coverColorArgb2 = 0xFF0A1020L,
            imageUrl = cover("1477959858617-67f85cf4f1df"),       // city night skyline
        ),
        HomeAlbum(
            id = "4", name = "Object Study",
            tagLabel = "", tagLine = "",
            newCount = 0, mediaCount = 28,
            isNew = false, isPremium = false, isFullWidth = false,
            coverColorArgb = 0xFFB8B8B8L, coverColorArgb2 = 0xFF707070L,
            imageUrl = cover("1523275335684-37898b6baf30"),       // minimalist watch
        ),
    )
}
