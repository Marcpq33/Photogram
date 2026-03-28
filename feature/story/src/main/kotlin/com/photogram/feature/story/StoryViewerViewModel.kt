package com.photogram.feature.story

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photogram.core.datastore.OwnStorySessionStore
import com.photogram.core.datastore.UserPreferencesRepository
import com.photogram.core.navigation.PhotogramDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userPreferences: UserPreferencesRepository,
    private val ownStorySessionStore: OwnStorySessionStore,
) : ViewModel() {

    private val albumId: String =
        checkNotNull(savedStateHandle[PhotogramDestination.StoryViewer.ARG_ALBUM_ID])

    private val _uiState = MutableStateFlow(buildInitialState(albumId))
    internal val uiState = _uiState.asStateFlow()

    init {
        // Populate own-story state. Runs only for the "my_story" album ID.
        if (albumId == "my_story") {
            viewModelScope.launch {
                val isDemoMode = userPreferences.userData.first().isDemoMode
                if (isDemoMode) {
                    _uiState.update { it.copy(
                        viewCount            = mockViewers.size,
                        viewers              = mockViewers,
                        allMentionCandidates = mockMentionUsers,
                        mentionCandidates    = mockMentionUsers,
                    ) }
                }
                // Load all persisted story URIs and build one StoryItem per entry.
                // The URI is stored as the item id so DeleteStory can target it precisely.
                val uris = ownStorySessionStore.storyUris.first()
                if (uris.isNotEmpty()) {
                    _uiState.update { state ->
                        state.copy(
                            stories = uris.map { uri ->
                                StoryItem(
                                    id             = uri,
                                    authorName     = "You",
                                    authorUsername = "me",
                                    timeAgo        = "JUST NOW",
                                    mediaColorArgb = 0xFF1A3A4AL,
                                    mediaUri       = uri,
                                    caption        = null,
                                    dateLabel      = "TODAY",
                                )
                            },
                        )
                    }
                }
            }
        }
    }

    /** One-shot: screen pops back when emitted. */
    private val _closeEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val closeEvent: SharedFlow<Unit> = _closeEvent.asSharedFlow()

    internal fun onAction(action: StoryViewerUiAction) {
        when (action) {

            // ── Shared ────────────────────────────────────────────────────────
            StoryViewerUiAction.Close ->
                viewModelScope.launch { _closeEvent.emit(Unit) }

            StoryViewerUiAction.TapNext -> _uiState.update { state ->
                val next = state.currentIndex + 1
                if (next > state.stories.lastIndex) {
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
                    replySent    = false,
                )
            }

            // ── Others'-story ─────────────────────────────────────────────────
            is StoryViewerUiAction.ReactionTapped ->
                _uiState.update {
                    it.copy(reactionSent = if (it.reactionSent == action.emoji) null else action.emoji)
                }

            is StoryViewerUiAction.ReplyTextChanged ->
                _uiState.update { it.copy(replyText = action.text) }

            StoryViewerUiAction.SendReply ->
                // Local ack — no backend send yet
                _uiState.update { it.copy(replyText = "", replySent = true) }

            // ── Own story: viewers ────────────────────────────────────────────
            StoryViewerUiAction.OpenViewers  ->
                _uiState.update { it.copy(showViewersSheet = true) }

            StoryViewerUiAction.CloseViewers ->
                _uiState.update { it.copy(showViewersSheet = false) }

            // ── Own story: settings ───────────────────────────────────────────
            StoryViewerUiAction.OpenSettings  ->
                _uiState.update { it.copy(showSettingsSheet = true) }

            StoryViewerUiAction.CloseSettings ->
                _uiState.update { it.copy(showSettingsSheet = false) }

            // Tapping "Delete" in settings sheet → show confirm dialog (does NOT delete yet).
            StoryViewerUiAction.DeleteStory ->
                _uiState.update { it.copy(showSettingsSheet = false, showDeleteConfirmDialog = true) }

            // User confirmed deletion in the dialog.
            StoryViewerUiAction.ConfirmDelete -> {
                val currentUri = _uiState.value.currentStory?.id
                _uiState.update { it.copy(showDeleteConfirmDialog = false) }
                viewModelScope.launch {
                    if (currentUri != null) ownStorySessionStore.deleteStory(currentUri)
                    _closeEvent.emit(Unit)
                }
            }

            StoryViewerUiAction.CancelDelete ->
                _uiState.update { it.copy(showDeleteConfirmDialog = false) }

            // Tapping "Archive" → show destination picker sheet.
            StoryViewerUiAction.ArchiveStory ->
                _uiState.update { it.copy(showSettingsSheet = false, showArchiveSheet = true) }

            is StoryViewerUiAction.ConfirmArchive -> {
                // Local only — destination: action.destination. Remote archive wired when backend available.
                _uiState.update { it.copy(showArchiveSheet = false) }
                viewModelScope.launch { _closeEvent.emit(Unit) }
            }

            StoryViewerUiAction.CancelArchive ->
                _uiState.update { it.copy(showArchiveSheet = false) }

            StoryViewerUiAction.MuteReplies ->
                // Local only — toggles flag; settings sheet stays open so label refreshes.
                _uiState.update { it.copy(repliesMuted = !it.repliesMuted) }

            // ── Own story: mentions ───────────────────────────────────────────
            StoryViewerUiAction.OpenMentions ->
                _uiState.update { it.copy(showMentionsSheet = true, mentionQuery = "") }

            StoryViewerUiAction.CloseMentions ->
                _uiState.update { it.copy(showMentionsSheet = false) }

            is StoryViewerUiAction.MentionQueryChanged -> _uiState.update { state ->
                val q = action.query
                // Preserve current selections while filtering
                val selectedIds = state.allMentionCandidates.filter { it.selected }.map { it.id }.toSet()
                val filtered = if (q.isBlank()) state.allMentionCandidates
                else state.allMentionCandidates.filter { u ->
                    u.displayName.contains(q, ignoreCase = true) ||
                    u.username.contains(q, ignoreCase = true)
                }
                state.copy(
                    mentionQuery       = q,
                    mentionCandidates  = filtered.map { u -> u.copy(selected = u.id in selectedIds) },
                )
            }

            is StoryViewerUiAction.ToggleMentionUser -> _uiState.update { state ->
                // Update both the filtered list and the master list so selection survives queries
                state.copy(
                    allMentionCandidates = state.allMentionCandidates.map { u ->
                        if (u.id == action.userId) u.copy(selected = !u.selected) else u
                    },
                    mentionCandidates = state.mentionCandidates.map { u ->
                        if (u.id == action.userId) u.copy(selected = !u.selected) else u
                    },
                )
            }

            StoryViewerUiAction.ConfirmMentions -> _uiState.update { state ->
                state.copy(
                    showMentionsSheet = false,
                    confirmedMentions = state.allMentionCandidates.filter { it.selected },
                )
            }

            // ── Own story: share ──────────────────────────────────────────────
            StoryViewerUiAction.OpenShare  ->
                _uiState.update { it.copy(showShareSheet = true) }

            StoryViewerUiAction.CloseShare ->
                _uiState.update { it.copy(showShareSheet = false) }
        }
    }

    // -------------------------------------------------------------------------
    // Initial state builders
    // -------------------------------------------------------------------------

    private fun buildInitialState(albumId: String): StoryViewerUiState = when (albumId) {
        "my_story" -> StoryViewerUiState(
            albumId              = albumId,
            viewCount            = 0,
            viewers              = emptyList(),
            allMentionCandidates = emptyList(),
            mentionCandidates    = emptyList(),
            // Stories are populated asynchronously from DataStore in init{}.
            // Starting empty is intentional — the viewer renders nothing until
            // the coroutine completes, which is typically <1 frame.
            stories              = emptyList(),
        )
        else -> StoryViewerUiState(
            albumId   = albumId,
            viewCount = 147,
            stories = listOf(
                StoryItem(
                    id             = "s1",
                    authorName     = "Elena Rossi",
                    authorUsername = "elena.rossi",
                    timeAgo        = "2H",
                    mediaColorArgb = 0xFFB85A18L,
                    caption        = "Golden hours by the shore",
                    dateLabel      = "SEPTEMBER 14, 2024 • AMALFI COAST",
                ),
                StoryItem(
                    id             = "s2",
                    authorName     = "Elena Rossi",
                    authorUsername = "elena.rossi",
                    timeAgo        = "2H",
                    mediaColorArgb = 0xFF1E4A5EL,
                    caption        = "Late evening light",
                    dateLabel      = "SEPTEMBER 14, 2024 • AMALFI COAST",
                ),
                StoryItem(
                    id             = "s3",
                    authorName     = "Elena Rossi",
                    authorUsername = "elena.rossi",
                    timeAgo        = "3H",
                    mediaColorArgb = 0xFF3A2518L,
                    caption        = null,
                    dateLabel      = "SEPTEMBER 15, 2024 • AMALFI COAST",
                ),
            ),
        )
    }
}

// ---------------------------------------------------------------------------
// Local mock data — replaced when story/user repositories are wired to backend
// ---------------------------------------------------------------------------

private val mockViewers = listOf(
    StoryViewerEntry("Sofia Martinez", "sofia.m",     "1H"),
    StoryViewerEntry("James Chen",     "james.chen",  "2H"),
    StoryViewerEntry("Anna Fischer",   "anna.f",      "3H"),
    StoryViewerEntry("Marco Ricci",    "marco.ricci", "5H"),
)

private val mockMentionUsers = listOf(
    StoryMentionUser("u1", "Sofia Martinez", "sofia.m"),
    StoryMentionUser("u2", "James Chen",     "james.chen"),
    StoryMentionUser("u3", "Anna Fischer",   "anna.f"),
    StoryMentionUser("u4", "Marco Ricci",    "marco.ricci"),
    StoryMentionUser("u5", "Elena Rossi",    "elena.rossi"),
    StoryMentionUser("u6", "Tomás Herrera",  "tomas.h"),
    StoryMentionUser("u7", "Yuki Tanaka",    "yuki.t"),
)
