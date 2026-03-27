package com.photogram.feature.chat

import android.net.Uri

internal sealed interface ChatDetailUiAction {
    data object BackTapped                                                  : ChatDetailUiAction
    data class  InputChanged(val text: String)                             : ChatDetailUiAction
    data object SendTapped                                                 : ChatDetailUiAction
    data class  MediaPicked(val uri: Uri, val isVideo: Boolean)            : ChatDetailUiAction
    data object RecordingStarted                                           : ChatDetailUiAction
    data object RecordingCancelled                                         : ChatDetailUiAction
    data class  AudioRecordingFinished(val uri: Uri, val durationMs: Long) : ChatDetailUiAction
}
