package com.photogram.core.model

data class Message(
    val id: String,
    val albumId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatarUrl: String?,
    val content: String?,
    val mediaUrl: String?,
    val type: MessageType,
    val createdAt: Long,
    val isDeletedByMe: Boolean,
)

enum class MessageType {
    TEXT,
    AUDIO,
    VIDEO,
    IMAGE,
}
