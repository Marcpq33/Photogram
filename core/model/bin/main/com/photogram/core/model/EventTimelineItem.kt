package com.photogram.core.model

data class EventTimelineItem(
    val id: String,
    val eventId: String,
    val title: String,
    val description: String?,
    val startsAt: Long,
    val endsAt: Long,
    val location: String?,
    val order: Int,
)
