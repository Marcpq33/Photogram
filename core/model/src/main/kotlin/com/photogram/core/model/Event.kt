package com.photogram.core.model

data class Event(
    val id: String,
    val albumId: String,
    val name: String,
    val date: Long,
    val durationHours: Float,
    val location: String?,
    val description: String?,
    val coverUrl: String?,
    val type: EventType,
    val createdAt: Long,
)

enum class EventType {
    COCKTAIL,
    CEREMONY,
    BANQUET,
    CUSTOM,
}
