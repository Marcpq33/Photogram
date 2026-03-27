package com.photogram.feature.gallery

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

// ── Domain types (gallery-local, mock until backend is wired) ─────────────────

data class GalleryMonthTab(val year: Int, val month: String)

data class GalleryMediaItem(val id: String, val colorIndex: Int)

data class GalleryDaySection(
    val monthName: String,
    val day: Int,
    val items: List<GalleryMediaItem>,
)

// ── UiState ───────────────────────────────────────────────────────────────────

data class GalleryUiState(
    val months: List<GalleryMonthTab> = emptyList(),
    val selectedMonthIndex: Int = 0,
    val sections: List<GalleryDaySection> = emptyList(),
    val isSearchActive: Boolean = false,
    val searchQuery: String = "",
)

// ── UiAction ──────────────────────────────────────────────────────────────────

sealed interface GalleryUiAction {
    data class MonthSelected(val index: Int) : GalleryUiAction
    object SearchToggled : GalleryUiAction
    data class ImageTapped(val id: String) : GalleryUiAction
    data class SearchQueryChanged(val query: String) : GalleryUiAction
    object HomeNavClicked    : GalleryUiAction
    object GalleryNavClicked : GalleryUiAction
    object AlbumNavClicked   : GalleryUiAction
    object ProfileNavClicked : GalleryUiAction
    object RecapsNavClicked  : GalleryUiAction
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val userPreferences: UserPreferencesRepository,
) : ViewModel() {

    private val allData: List<Pair<GalleryMonthTab, List<GalleryDaySection>>> = buildMockData()

    // Start empty; populate with demo data only in demo/prototype mode.
    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val navEvent: SharedFlow<String> = _navEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            if (userPreferences.userData.first().isDemoMode) {
                _uiState.value = GalleryUiState(
                    months             = allData.map { it.first },
                    selectedMonthIndex = 0,
                    sections           = allData.first().second,
                )
            }
        }
    }

    fun onAction(action: GalleryUiAction) {
        when (action) {
            is GalleryUiAction.MonthSelected  -> selectMonth(action.index)
            GalleryUiAction.SearchToggled           -> toggleSearch()
            is GalleryUiAction.SearchQueryChanged   -> updateQuery(action.query)
            is GalleryUiAction.ImageTapped    -> navigate(PhotogramDestination.MediaViewer.createRoute(action.id))
            GalleryUiAction.HomeNavClicked    -> navigate(PhotogramDestination.Home.route)
            GalleryUiAction.GalleryNavClicked -> { /* already on Gallery */ }
            GalleryUiAction.AlbumNavClicked   -> navigate(PhotogramDestination.AlbumDetail.createRoute("1"))
            GalleryUiAction.ProfileNavClicked -> navigate(PhotogramDestination.Profile.route)
            GalleryUiAction.RecapsNavClicked  -> navigate(PhotogramDestination.RecapList.createRoute("all"))
        }
    }

    private fun selectMonth(index: Int) {
        val sections = allData.getOrNull(index)?.second ?: return
        _uiState.update { it.copy(selectedMonthIndex = index, sections = sections, searchQuery = "") }
    }

    // Closing search → reset query and restore selected month.
    // Opening search → keep current sections visible until user types.
    private fun toggleSearch() {
        _uiState.update { state ->
            val closing = state.isSearchActive
            state.copy(
                isSearchActive = !state.isSearchActive,
                searchQuery    = "",
                sections       = if (closing) {
                    allData.getOrNull(state.selectedMonthIndex)?.second ?: emptyList()
                } else {
                    state.sections
                },
            )
        }
    }

    // Searches across ALL months' sections — month name, day number, or item id.
    private fun updateQuery(query: String) {
        val filtered = if (query.isBlank()) {
            allData.getOrNull(_uiState.value.selectedMonthIndex)?.second ?: emptyList()
        } else {
            allData.flatMap { it.second }.filter { section ->
                section.monthName.contains(query, ignoreCase = true) ||
                section.day.toString().contains(query) ||
                section.items.any { item -> item.id.contains(query, ignoreCase = true) }
            }
        }
        _uiState.update { it.copy(searchQuery = query, sections = filtered) }
    }

    private fun navigate(route: String) {
        viewModelScope.launch { _navEvent.emit(route) }
    }
}

// ── Mock data ─────────────────────────────────────────────────────────────────

private fun buildMockData(): List<Pair<GalleryMonthTab, List<GalleryDaySection>>> = listOf(
    GalleryMonthTab(2023, "October") to listOf(
        GalleryDaySection(
            monthName = "October", day = 24,
            items = listOf(
                GalleryMediaItem("oct24-0", 0),
                GalleryMediaItem("oct24-1", 1),
                GalleryMediaItem("oct24-2", 2),
                GalleryMediaItem("oct24-3", 3),
                GalleryMediaItem("oct24-4", 4),
                GalleryMediaItem("oct24-5", 5),
                GalleryMediaItem("oct24-6", 6),
            ),
        ),
        GalleryDaySection(
            monthName = "October", day = 23,
            items = listOf(
                GalleryMediaItem("oct23-0", 7),
                GalleryMediaItem("oct23-1", 8),
                GalleryMediaItem("oct23-2", 9),
                GalleryMediaItem("oct23-3", 0),
            ),
        ),
    ),
    GalleryMonthTab(2023, "September") to listOf(
        GalleryDaySection(
            monthName = "September", day = 15,
            items = listOf(
                GalleryMediaItem("sep15-0", 2),
                GalleryMediaItem("sep15-1", 4),
                GalleryMediaItem("sep15-2", 6),
                GalleryMediaItem("sep15-3", 8),
            ),
        ),
    ),
    GalleryMonthTab(2023, "August") to listOf(
        GalleryDaySection(
            monthName = "August", day = 8,
            items = listOf(
                GalleryMediaItem("aug8-0", 5),
                GalleryMediaItem("aug8-1", 3),
                GalleryMediaItem("aug8-2", 1),
            ),
        ),
    ),
    GalleryMonthTab(2023, "July") to listOf(
        GalleryDaySection(
            monthName = "July", day = 20,
            items = listOf(
                GalleryMediaItem("jul20-0", 9),
                GalleryMediaItem("jul20-1", 7),
                GalleryMediaItem("jul20-2", 0),
                GalleryMediaItem("jul20-3", 5),
            ),
        ),
    ),
)
