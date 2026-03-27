package com.photogram.core.model

data class EventGuest(
    val eventId: String,
    val userId: String,
    val displayName: String,
    val avatarUrl: String?,
    val rsvpStatus: RsvpStatus,
)

enum class RsvpStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
}
