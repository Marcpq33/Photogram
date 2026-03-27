package com.photogram.feature.recaps

data class RecapsUiState(
    val featured: List<RecapItem> = emptyList(),
    val personal: List<RecapItem> = emptyList(),
)

data class RecapItem(
    val id: String,
    val title: String,
    val durationLabel: String,
    val thumbnailColor: Long,
    val seasonLabel: String = "",
    val imageUrl: String? = null,
    val photoCount: Int = 0,
)

object RecapsMock {
    val featured = listOf(
        RecapItem(
            id             = "r1",
            title          = "Golden Hour Reflections",
            durationLabel  = "0:45",
            thumbnailColor = 0xFF8B7355L,
            seasonLabel    = "SUMMER 2024",
            imageUrl       = "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?w=800&q=80",
        ),
        RecapItem(
            id             = "r2",
            title          = "Winter in Vienna",
            durationLabel  = "1:20",
            thumbnailColor = 0xFF7A8EA8L,
            seasonLabel    = "WINTER 2024",
            imageUrl       = "https://images.unsplash.com/photo-1467269204594-9661b134dd2b?w=800&q=80",
        ),
        RecapItem(
            id             = "r3",
            title          = "Autumn Wandering",
            durationLabel  = "0:58",
            thumbnailColor = 0xFF6B4A2AL,
            seasonLabel    = "AUTUMN 2024",
            imageUrl       = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&q=80",
        ),
    )
    val personal = listOf(
        RecapItem(
            id             = "p1",
            title          = "Morning Mist",
            durationLabel  = "0:32",
            thumbnailColor = 0xFF4A6A82L,
            photoCount     = 12,
            imageUrl       = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&q=75",
        ),
        RecapItem(
            id             = "p2",
            title          = "Daily Rituals",
            durationLabel  = "0:18",
            thumbnailColor = 0xFF3D3530L,
            photoCount     = 28,
            imageUrl       = "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400&q=75",
        ),
        RecapItem(
            id             = "p3",
            title          = "Deep Forest",
            durationLabel  = "0:55",
            thumbnailColor = 0xFF2D4A2AL,
            photoCount     = 8,
            imageUrl       = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=400&q=75",
        ),
        RecapItem(
            id             = "p4",
            title          = "Portraits",
            durationLabel  = "0:41",
            thumbnailColor = 0xFF5C3A3AL,
            photoCount     = 42,
            imageUrl       = "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=400&q=75",
        ),
    )
}
