package com.photogram.feature.profile

import android.net.Uri

data class ProfileUiState(
    val displayName: String = "",
    val username: String = "",
    val bio: String = "",
    val avatarUri: String = "",
    val capturingSinceYear: Int = 2025,
    val totalPhotos: Int = 0,
    val albumsCount: Int = 0,
    val daysStreak: Int = 0,
    val recaps: List<ProfileRecap> = emptyList(),
    val albums: List<ProfileAlbum> = emptyList(),
    val showNewPost: Boolean = false,
    val newPostMediaUri: Uri? = null,    // set when user picks from gallery; null = show placeholder ring
)

data class ProfileRecap(
    val id: String,
    val label: String,
    val coverColorArgb: Long,
)

data class ProfileAlbum(
    val id: String,
    val name: String,
    val coverColorArgb: Long,
    val isFullWidth: Boolean = false,
    val isTall: Boolean = false,
)

internal object ProfileDefaults {
    val recaps = listOf(
        ProfileRecap("r1", "SPRING '24", 0xFFCEAE90L),
        ProfileRecap("r2", "WINTER '23", 0xFF6A7A8EL),
    )
    val albums = listOf(
        ProfileAlbum("a1", "Portraits", 0xFFD4A07AL, isTall = true),
        ProfileAlbum("a2", "Nature",    0xFFE8E0D0L),
        ProfileAlbum("a3", "Street",    0xFFD0CBC0L),
        ProfileAlbum("a4", "Minimal",   0xFF1A3A34L, isFullWidth = true),
    )
}
