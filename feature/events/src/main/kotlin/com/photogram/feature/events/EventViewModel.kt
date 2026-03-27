package com.photogram.feature.events

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photogram.core.datastore.UserPreferencesRepository
import com.photogram.core.navigation.PhotogramDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userPreferences: UserPreferencesRepository,
) : ViewModel() {

    private val albumId: String =
        checkNotNull(savedStateHandle[PhotogramDestination.EventList.ARG_ALBUM_ID])

    private val _uiState = MutableStateFlow(
        EventUiState(
            albumId    = albumId,
            event      = EventDetail(title = "", date = "", location = ""),
            countdown  = CountdownState(0, 0, 0, 0),
            timeline   = emptyList(),
            guestCount = 0,
        ),
    )
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<String>()
    val navEvent: SharedFlow<String> = _navEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            if (userPreferences.userData.first().isDemoMode) {
                _uiState.value = EventUiState(albumId = albumId)
            }
        }
    }

    fun onAction(action: EventUiAction) {
        when (action) {

            // ── Navigation ────────────────────────────────────────────────────
            EventUiAction.HomeNavClicked -> viewModelScope.launch {
                _navEvent.emit(PhotogramDestination.Home.route)
            }
            EventUiAction.GalleryNavClicked  -> viewModelScope.launch {
                _navEvent.emit(PhotogramDestination.AlbumDetail.createRoute("1"))
            }
            EventUiAction.CreateNavClicked -> viewModelScope.launch {
                _navEvent.emit(PhotogramDestination.Camera.route)
            }
            EventUiAction.ChatNavClicked -> viewModelScope.launch {
                _navEvent.emit(PhotogramDestination.ChatList.route)
            }
            EventUiAction.ProfileNavClicked -> viewModelScope.launch {
                _navEvent.emit(PhotogramDestination.Profile.route)
            }
            EventUiAction.UploadPhotosClicked -> { /* placeholder — upload flow in future milestone */ }
            EventUiAction.GuestListClicked    -> { /* placeholder — guest list detail in future milestone */ }

            // ── Edit sheet lifecycle ──────────────────────────────────────────
            EventUiAction.EditEventClicked -> {
                val s = _uiState.value
                _uiState.update { it.copy(
                    isEditSheetVisible = true,
                    editDraft = EditDraft(
                        title          = s.event.title,
                        date           = s.event.date,
                        location       = s.event.location,
                        guestCountText = s.guestCount.toString(),
                        timeline       = s.timeline.map { t ->
                            EditTimelineItem(t.id, t.time, t.title, t.location)
                        },
                    ),
                )}
            }

            EventUiAction.EditDismissed ->
                _uiState.update { it.copy(isEditSheetVisible = false) }

            EventUiAction.EditSaved -> {
                val draft = _uiState.value.editDraft
                val newGuests = draft.guestCountText.toIntOrNull() ?: _uiState.value.guestCount
                _uiState.update { state ->
                    state.copy(
                        isEditSheetVisible = false,
                        event = state.event.copy(
                            title    = draft.title.ifBlank { state.event.title },
                            date     = draft.date.ifBlank { state.event.date },
                            location = draft.location.ifBlank { state.event.location },
                        ),
                        guestCount = newGuests,
                        timeline = if (draft.timeline.isNotEmpty()) {
                            draft.timeline.map { d ->
                                TimelineEntry(
                                    id       = d.id,
                                    time     = d.time.ifBlank { "--:--" },
                                    title    = d.title.ifBlank { "(sin nombre)" },
                                    location = d.location,
                                )
                            }
                        } else {
                            state.timeline
                        },
                    )
                }
            }

            // ── Draft field changes ───────────────────────────────────────────
            is EventUiAction.DraftTitleChanged ->
                _uiState.update { it.copy(editDraft = it.editDraft.copy(title = action.v)) }
            is EventUiAction.DraftDateChanged ->
                _uiState.update { it.copy(editDraft = it.editDraft.copy(date = action.v)) }
            is EventUiAction.DraftLocationChanged ->
                _uiState.update { it.copy(editDraft = it.editDraft.copy(location = action.v)) }
            is EventUiAction.DraftGuestCountChanged ->
                _uiState.update { it.copy(editDraft = it.editDraft.copy(guestCountText = action.v)) }

            // ── Timeline CRUD ─────────────────────────────────────────────────
            EventUiAction.AddTimelineItem -> _uiState.update { state ->
                state.copy(
                    editDraft = state.editDraft.copy(
                        timeline = state.editDraft.timeline + EditTimelineItem(
                            id       = "new_${System.currentTimeMillis()}",
                            time     = "",
                            title    = "",
                            location = "",
                        ),
                    ),
                )
            }

            is EventUiAction.RemoveTimelineItem -> _uiState.update { state ->
                state.copy(
                    editDraft = state.editDraft.copy(
                        timeline = state.editDraft.timeline.filterNot { it.id == action.id },
                    ),
                )
            }

            is EventUiAction.DraftTimelineTimeChanged -> _uiState.update { state ->
                state.copy(
                    editDraft = state.editDraft.copy(
                        timeline = state.editDraft.timeline.map { item ->
                            if (item.id == action.id) item.copy(time = action.v) else item
                        },
                    ),
                )
            }

            is EventUiAction.DraftTimelineTitleChanged -> _uiState.update { state ->
                state.copy(
                    editDraft = state.editDraft.copy(
                        timeline = state.editDraft.timeline.map { item ->
                            if (item.id == action.id) item.copy(title = action.v) else item
                        },
                    ),
                )
            }

            is EventUiAction.DraftTimelineLocationChanged -> _uiState.update { state ->
                state.copy(
                    editDraft = state.editDraft.copy(
                        timeline = state.editDraft.timeline.map { item ->
                            if (item.id == action.id) item.copy(location = action.v) else item
                        },
                    ),
                )
            }
        }
    }
}
