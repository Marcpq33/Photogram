package com.photogram.feature.story

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
class StoryViewerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val albumId: String =
        checkNotNull(savedStateHandle[PhotogramDestination.StoryViewer.ARG_ALBUM_ID])

    private val _uiState = MutableStateFlow(buildInitialState(albumId))
    internal val uiState = _uiState.asStateFlow()

    // One-shot: screen should pop back stack when emitted
    private val _closeEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val closeEvent: SharedFlow<Unit> = _closeEvent.asSharedFlow()

    internal fun onAction(action: StoryViewerUiAction) {
        when (action) {
            StoryViewerUiAction.Close ->
                viewModelScope.launch { _closeEvent.emit(Unit) }

            StoryViewerUiAction.TapNext -> _uiState.update { state ->
                val next = state.currentIndex + 1
                if (next > state.stories.lastIndex) {
                    // Past the last story — close the viewer
                    viewModelScope.launch { _closeEvent.emit(Unit) }
                    state
                } else {
                    state.copy(currentIndex = next, reactionSent = null, replySent = false)
                }
            }

            StoryViewerUiAction.TapPrev -> _uiState.update { state ->
                state.copy(
                    currentIndex = (state.currentIndex - 1).coerceAtLeast(0),
                    reactionSent = null,
                    replySent = false,
                )
            }

            is StoryViewerUiAction.ReactionTapped ->
                // Toggle: tap same emoji again to deselect
                _uiState.update {
                    it.copy(reactionSent = if (it.reactionSent == action.emoji) null else action.emoji)
                }

            is StoryViewerUiAction.ReplyTextChanged ->
                _uiState.update { it.copy(replyText = action.text) }

            StoryViewerUiAction.SendReply ->
                // Placeholder: acknowledged locally — no backend send yet
                _uiState.update { it.copy(replyText = "", replySent = true) }
        }
    }

    // ---------------------------------------------------------------------------
    // Placeholder data — replaced when the Story repository is wired
    // ---------------------------------------------------------------------------
    private fun buildInitialState(albumId: String) = StoryViewerUiState(
        albumId = albumId,
        stories = listOf(
            StoryItem(
                id            = "s1",
                authorName    = "Elena Rossi",
                authorUsername = "elena.rossi",
                timeAgo       = "2H",
                mediaColorArgb = 0xFFB85A18L,
                caption       = "Golden hours by the shore",
                dateLabel     = "SEPTEMBER 14, 2024 • AMALFI COAST",
            ),
            StoryItem(
                id            = "s2",
                authorName    = "Elena Rossi",
                authorUsername = "elena.rossi",
                timeAgo       = "2H",
                mediaColorArgb = 0xFF1E4A5EL,
                caption       = "Late evening light",
                dateLabel     = "SEPTEMBER 14, 2024 • AMALFI COAST",
            ),
            StoryItem(
                id            = "s3",
                authorName    = "Elena Rossi",
                authorUsername = "elena.rossi",
                timeAgo       = "3H",
                mediaColorArgb = 0xFF3A2518L,
                caption       = null,
                dateLabel     = "SEPTEMBER 15, 2024 • AMALFI COAST",
            ),
        ),
    )
}
