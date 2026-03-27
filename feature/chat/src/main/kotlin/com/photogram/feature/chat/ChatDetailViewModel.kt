package com.photogram.feature.chat

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photogram.core.navigation.PhotogramDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val albumId: String =
        checkNotNull(savedStateHandle[PhotogramDestination.ChatDetail.ARG_ALBUM_ID])

    private val _uiState = MutableStateFlow(buildInitialState(albumId))
    internal val uiState = _uiState.asStateFlow()

    private val _navBack = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navBack: SharedFlow<Unit> = _navBack.asSharedFlow()

    internal fun onAction(action: ChatDetailUiAction) {
        when (action) {
            ChatDetailUiAction.BackTapped ->
                viewModelScope.launch { _navBack.emit(Unit) }

            is ChatDetailUiAction.InputChanged ->
                _uiState.update { it.copy(inputText = action.text) }

            ChatDetailUiAction.SendTapped ->
                sendText()

            is ChatDetailUiAction.MediaPicked ->
                appendMediaMessage(action)

            ChatDetailUiAction.RecordingStarted ->
                _uiState.update { it.copy(isRecordingAudio = true) }

            ChatDetailUiAction.RecordingCancelled ->
                _uiState.update { it.copy(isRecordingAudio = false) }

            is ChatDetailUiAction.AudioRecordingFinished ->
                appendAudioMessage(action)
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun sendText() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty()) return
        val msg = ChatDetailMessage(
            id         = System.currentTimeMillis().toString(),
            senderName = "Me",
            text       = text,
            timestamp  = "Now",
            isMine     = true,
        )
        _uiState.update { it.copy(inputText = "", messages = it.messages + msg) }
    }

    private fun appendMediaMessage(action: ChatDetailUiAction.MediaPicked) {
        val attachment = if (action.isVideo) {
            MessageAttachment.Video(action.uri)
        } else {
            MessageAttachment.Image(action.uri)
        }
        val msg = ChatDetailMessage(
            id         = System.currentTimeMillis().toString(),
            senderName = "Me",
            text       = "",
            timestamp  = "Now",
            isMine     = true,
            attachment = attachment,
        )
        _uiState.update { it.copy(messages = it.messages + msg) }
    }

    private fun appendAudioMessage(action: ChatDetailUiAction.AudioRecordingFinished) {
        val msg = ChatDetailMessage(
            id         = System.currentTimeMillis().toString(),
            senderName = "Me",
            text       = "",
            timestamp  = "Now",
            isMine     = true,
            attachment = MessageAttachment.Audio(action.uri, action.durationMs),
        )
        _uiState.update { it.copy(isRecordingAudio = false, messages = it.messages + msg) }
    }

    private fun buildInitialState(id: String): ChatDetailUiState {
        val item = ChatDefaults.items.firstOrNull { it.id == id }
        val messages = when {
            item?.albumName == "Midnight Solitude" -> buildMidnightSolitudeConversation()
            item != null -> listOf(
                ChatDetailMessage(
                    id         = "p1",
                    senderName = item.albumName,
                    text       = item.previewText.ifBlank { "🎙 Voice message" },
                    timestamp  = item.timestamp,
                    isMine     = false,
                )
            )
            else -> emptyList()
        }
        return ChatDetailUiState(
            albumId   = id,
            albumName = item?.albumName ?: id,
            messages  = messages,
        )
    }

    private fun buildMidnightSolitudeConversation(): List<ChatDetailMessage> = listOf(
        ChatDetailMessage(
            id                 = "sep_oct24",
            senderName         = "",
            text               = "",
            timestamp          = "",
            isMine             = false,
            isDateSeparator    = true,
            dateSeparatorLabel = "OCTOBER 24, 2023",
        ),
        ChatDetailMessage(
            id         = "msg_photo",
            senderName = "Elena Rossi",
            text       = "The lighting was perfect for 3 minutes. I managed to capture the obsidian tones in the shadows.",
            timestamp  = "18:42",
            isMine     = false,
            attachment = MessageAttachment.MockPhoto,
        ),
        ChatDetailMessage(
            id            = "msg_out1",
            senderName    = "Me",
            text          = "It looks editorial. The texture on those peaks is incredible. Is this the Leica set?",
            timestamp     = "18:45",
            isMine        = true,
            deliveryLabel = "Delivered",
        ),
        ChatDetailMessage(
            id         = "msg_audio",
            senderName = "Elena Rossi",
            text       = "",
            timestamp  = "18:47",
            isMine     = false,
            attachment = MessageAttachment.Audio(Uri.EMPTY, 14_000L),
        ),
        ChatDetailMessage(
            id        = "msg_out2",
            senderName = "Me",
            text      = "Send me the contact sheet when you're back. I'd love to see the whole sequence.",
            timestamp = "18:50",
            isMine    = true,
        ),
    )
}
