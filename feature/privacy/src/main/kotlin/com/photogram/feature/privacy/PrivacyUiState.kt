package com.photogram.feature.privacy

enum class ProfileMode { SOLO_YO, MIEMBROS }

data class PrivacyUiState(
    val profileMode: ProfileMode = ProfileMode.SOLO_YO,
    val totalPhotos: Int = 1_248,
    val publicAlbums: Int = 0,
    val showPublicPhotos: Boolean = true,
    val showStats: Boolean = false,
    val requireApproval: Boolean = true,
    val whoCanInvite: String = "Solo yo",
    val albumsPrivate: Int = 3,
    val albumsWithLink: Int = 1,
    val albumsPublic: Int = 0,
)
