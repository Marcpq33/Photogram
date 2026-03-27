package com.photogram.core.model

data class Chat(
    val albumId: String,
    val albumName: String,
    val coverUrl: String?,
    val lastMessage: Message?,
    val unreadCount: Int,
    val updatedAt: Long,
)
