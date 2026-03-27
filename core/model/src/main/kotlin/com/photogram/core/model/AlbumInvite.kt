package com.photogram.core.model

data class AlbumInvite(
    val id: String,
    val albumId: String,
    val albumName: String,
    val invitedBy: String,
    val token: String,
    val expiresAt: Long,
    val usedAt: Long?,
)
