package com.photogram.core.model

data class AppNotification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val albumId: String?,
    val mediaId: String?,
    val isRead: Boolean,
    val createdAt: Long,
)

enum class NotificationType {
    NEW_PHOTO,
    REACTION,
    MESSAGE,
    STORY,
    EVENT_REMINDER,
    RECAP_READY,
    INVITE,
}
