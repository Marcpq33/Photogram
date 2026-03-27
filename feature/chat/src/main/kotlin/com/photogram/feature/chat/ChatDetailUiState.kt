package com.photogram.feature.chat

import android.net.Uri

// ── Attachment types ──────────────────────────────────────────────────────────

internal sealed class MessageAttachment {
    data class Image(val uri: Uri) : MessageAttachment()
    data class Video(val uri: Uri) : MessageAttachment()
    data class Audio(val uri: Uri, val durationMs: Long) : MessageAttachment()
    /** Gradient mountain placeholder — no real asset required. */
    data object MockPhoto : MessageAttachment()
}

// ── Message model ─────────────────────────────────────────────────────────────

internal data class ChatDetailMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isMine: Boolean,
    val attachment: MessageAttachment? = null,
    /** When true, render as a date separator row; all other fields are ignored. */
    val isDateSeparator: Boolean = false,
    val dateSeparatorLabel: String = "",
    /** Optional delivery status shown beneath the timestamp (e.g. "Delivered"). */
    val deliveryLabel: String? = null,
)

// ── Screen state ──────────────────────────────────────────────────────────────

internal data class ChatDetailUiState(
    val albumId: String = "",
    val albumName: String = "",
    val inputText: String = "",
    val messages: List<ChatDetailMessage> = emptyList(),
    val isRecordingAudio: Boolean = false,
)
