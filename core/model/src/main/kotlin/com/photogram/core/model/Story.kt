package com.photogram.core.model

data class Story(
    val id: String,
    val albumId: String,
    val authorId: String,
    val mediaUrl: String,
    val thumbnailUrl: String?,
    val isVideo: Boolean,
    val caption: String?,
    val viewCount: Int,
    val createdAt: Long,
    val expiresAt: Long,
    val viewedByMe: Boolean,
)
