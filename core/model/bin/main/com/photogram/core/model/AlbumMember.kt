package com.photogram.core.model

data class AlbumMember(
    val albumId: String,
    val userId: String,
    val displayName: String,
    val avatarUrl: String?,
    val role: AlbumRole,
    val joinedAt: Long,
)

enum class AlbumRole {
    OWNER,
    MEMBER,
}
