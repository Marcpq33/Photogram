package com.photogram.feature.events

data class EventUiState(
    val albumId: String = "",
    val event: EventDetail = EventMock.event,
    val countdown: CountdownState = CountdownState(12, 8, 42, 15),
    val timeline: List<TimelineEntry> = EventMock.timeline,
    val guestCount: Int = 136,
    val additionalGuests: Int = 132,
    val isEditSheetVisible: Boolean = false,
    val editDraft: EditDraft = EditDraft(),
)

data class EventDetail(
    val title: String,
    val date: String,
    val location: String,
) {
    val dateLabel: String get() = "$date  ·  $location"
}

data class CountdownState(
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
) {
    companion object {
        fun fromSeconds(total: Long): CountdownState {
            val d = (total / 86400L).toInt()
            val rem = total % 86400L
            val h = (rem / 3600L).toInt()
            val m = ((rem % 3600L) / 60L).toInt()
            val s = (rem % 60L).toInt()
            return CountdownState(d, h, m, s)
        }
    }
}

data class TimelineEntry(
    val id: String,
    val time: String,
    val title: String,
    val location: String,
)

// ── Edit draft ────────────────────────────────────────────────────────────────

data class EditDraft(
    val title: String = "",
    val date: String = "",
    val location: String = "",
    val guestCountText: String = "",
    val timeline: List<EditTimelineItem> = emptyList(),
)

data class EditTimelineItem(
    val id: String,
    val time: String,
    val title: String,
    val location: String,
)

// ── Mock data ─────────────────────────────────────────────────────────────────

object EventMock {
    val event = EventDetail(
        title    = "Boda de Claudia &\nFernando",
        date     = "24 AGOSTO 2024",
        location = "HACIENDA EL RETIRO",
    )
    val timeline = listOf(
        TimelineEntry("t1", "17:00", "Ceremonia",  "Capilla de la Hacienda"),
        TimelineEntry("t2", "18:30", "Cóctel",     "Jardines Principales"),
        TimelineEntry("t3", "20:00", "Banquete",   "Salón de Cristal"),
    )
}
