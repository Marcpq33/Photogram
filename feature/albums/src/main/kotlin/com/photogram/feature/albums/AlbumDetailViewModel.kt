package com.photogram.feature.albums

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

internal enum class AlbumSnackbarKey { ShareComingSoon, UploadComingSoon, PhotoComingSoon, DownloadComingSoon, CreateAlbumComingSoon }

// ── Domain types ──────────────────────────────────────────────────────────────

enum class AlbumTab { Photos, Calendar, Recaps, Members }

data class AlbumPhoto(
    val id: String,
    val placeholderColor: Long,
    val hasReaction: Boolean = false,
)

/**
 * Represents one day cell in the calendar grid.
 * [thumbnailColorArgb] non-null → render a circular photo placeholder.
 * [hasRing] → render a terracotta outer ring (selected/featured day).
 */
data class CalendarDay(
    val day: Int,
    val thumbnailColorArgb: Long? = null,
    val hasRing: Boolean = false,
)

private fun buildSeptember2025(): List<CalendarDay> {
    // September 2025: 30 days, starts on Monday (offset 0 in L-M-X-J-V-S-D)
    val thumbnails = mapOf(
        3  to Pair(0xFFD4B870L, true),
        6  to Pair(0xFF7A5040L, false),
        11 to Pair(0xFFD49060L, false),
    )
    return (1..30).map { day ->
        val entry = thumbnails[day]
        CalendarDay(
            day                = day,
            thumbnailColorArgb = entry?.first,
            hasRing            = entry?.second ?: false,
        )
    }
}

// ── Year calendar data ─────────────────────────────────────────────────────────

data class MonthCalendarData(
    val title:       String,
    val startOffset: Int,            // 0 = Monday … 6 = Sunday (L-M-X-J-V-S-D)
    val days:        List<CalendarDay>,
)

private val kMonthNames2025 = listOf(
    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre",
)
// Day-of-week offset for 1st of each month in 2025 (0 = Mon … 6 = Sun)
private val kMonthOffsets2025 = listOf(2, 5, 5, 1, 3, 6, 1, 4, 0, 2, 5, 0)
private val kMonthLengths2025 = listOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

// Sparse mock uploaded-photo data: monthIndex (0-based) → day → (color, hasRing)
private val kMockPhotosByMonth: Map<Int, Map<Int, Pair<Long, Boolean>>> = mapOf(
    2  to mapOf(15 to Pair(0xFF4A7A50L, false)),
    5  to mapOf(7  to Pair(0xFF4A6A8AL, false)),
    7  to mapOf(22 to Pair(0xFFD4904AL, false), 25 to Pair(0xFF8BA07AL, false)),
    8  to mapOf(3  to Pair(0xFFD4B870L, true),  6  to Pair(0xFF7A5040L, false), 11 to Pair(0xFFD49060L, false)),
    11 to mapOf(24 to Pair(0xFF7A8EA8L, false), 25 to Pair(0xFFD4B870L, true)),
)

private fun buildAllMonths2025(): List<MonthCalendarData> =
    kMonthNames2025.indices.map { i ->
        val photos = kMockPhotosByMonth[i] ?: emptyMap()
        MonthCalendarData(
            title       = kMonthNames2025[i],
            startOffset = kMonthOffsets2025[i],
            days        = (1..kMonthLengths2025[i]).map { day ->
                val entry = photos[day]
                CalendarDay(
                    day                = day,
                    thumbnailColorArgb = entry?.first,
                    hasRing            = entry?.second ?: false,
                )
            },
        )
    }

data class AlbumDetailUiState(
    val albumTitle:    String    = "Cristina y Fer",
    val albumSubtitle: String    = "SEPTIEMBRE 2025",
    val calendarTitle: String    = "Septiembre 2025",
    val streakDays:    Int       = 12,
    val memberCount:   Int       = 6,
    val selectedTab:   AlbumTab  = AlbumTab.Photos,
    val calendarDays:  List<CalendarDay> = buildSeptember2025(),
    val calendarPage:  Int       = 8,             // 0-indexed month (8 = September)
    val showYearView:  Boolean   = false,
    val allMonthsData: List<MonthCalendarData> = buildAllMonths2025(),
    val photos: List<AlbumPhoto> = listOf(
        AlbumPhoto("1",  0xFFD49060L, false),
        AlbumPhoto("2",  0xFFB09878L, false),
        AlbumPhoto("3",  0xFF8BA07AL, false),
        AlbumPhoto("4",  0xFF251810L, true),
        AlbumPhoto("5",  0xFF6B9B5AL, false),
        AlbumPhoto("6",  0xFFF0E8D8L, false),
        AlbumPhoto("7",  0xFFD4904AL, false),
        AlbumPhoto("8",  0xFF8090A8L, false),
        AlbumPhoto("9",  0xFF7A2A32L, false),
        AlbumPhoto("10", 0xFFD44A3AL, false),
        AlbumPhoto("11", 0xFF7A5040L, false),
        AlbumPhoto("12", 0xFF9A8878L, false),
    ),
)

sealed interface AlbumDetailUiAction {
    data object BackClicked      : AlbumDetailUiAction
    data object ShareClicked     : AlbumDetailUiAction
    data object UploadClicked    : AlbumDetailUiAction
    data object HomeNavClicked   : AlbumDetailUiAction
    data object GalleryNavClicked: AlbumDetailUiAction
    data object CreateNavClicked : AlbumDetailUiAction
    data object ChatNavClicked   : AlbumDetailUiAction
    data object ProfileNavClicked: AlbumDetailUiAction
    data class  TabSelected(val tab: AlbumTab) : AlbumDetailUiAction
    data class  PhotoTapped(val photoId: String) : AlbumDetailUiAction
    data class  CalendarPageChanged(val page: Int) : AlbumDetailUiAction
    data object YearViewToggled    : AlbumDetailUiAction
    data object DownloadClicked    : AlbumDetailUiAction
    data object CreateAlbumClicked : AlbumDetailUiAction
}

// ── Empty-state calendar helpers ──────────────────────────────────────────────

/** Calendar days for the current month with no photo thumbnails. */
private fun buildEmptyCalendarDays(): List<CalendarDay> =
    (1..30).map { CalendarDay(day = it) }

/** Full year calendar with no photo thumbnails — preserves grid structure. */
private fun buildEmptyAllMonths(): List<MonthCalendarData> =
    kMonthNames2025.indices.map { i ->
        MonthCalendarData(
            title       = kMonthNames2025[i],
            startOffset = kMonthOffsets2025[i],
            days        = (1..kMonthLengths2025[i]).map { CalendarDay(day = it) },
        )
    }

// ── ViewModel ─────────────────────────────────────────────────────────────────

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val userPreferences: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AlbumDetailUiState(
            albumTitle    = "",
            albumSubtitle = "",
            calendarTitle = "",
            streakDays    = 0,
            memberCount   = 0,
            photos        = emptyList(),
            calendarDays  = buildEmptyCalendarDays(),
            allMonthsData = buildEmptyAllMonths(),
        ),
    )
    val uiState: StateFlow<AlbumDetailUiState> = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val navEvent: SharedFlow<String> = _navEvent.asSharedFlow()

    private val _snackbarEvent = MutableSharedFlow<AlbumSnackbarKey>(extraBufferCapacity = 1)
    internal val snackbarEvent: SharedFlow<AlbumSnackbarKey> = _snackbarEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            if (userPreferences.userData.first().isDemoMode) {
                _uiState.value = AlbumDetailUiState()
            }
        }
    }

    fun onAction(action: AlbumDetailUiAction) {
        when (action) {
            AlbumDetailUiAction.BackClicked ->
                viewModelScope.launch { _navEvent.emit("back") }

            AlbumDetailUiAction.HomeNavClicked ->
                viewModelScope.launch { _navEvent.emit(PhotogramDestination.Home.route) }

            AlbumDetailUiAction.GalleryNavClicked ->
                viewModelScope.launch { _navEvent.emit(PhotogramDestination.AlbumDetail.createRoute("1")) }

            AlbumDetailUiAction.CreateNavClicked ->
                viewModelScope.launch { _navEvent.emit(PhotogramDestination.Camera.route) }

            AlbumDetailUiAction.ChatNavClicked ->
                viewModelScope.launch { _navEvent.emit(PhotogramDestination.ChatList.route) }

            AlbumDetailUiAction.ProfileNavClicked ->
                viewModelScope.launch { _navEvent.emit(PhotogramDestination.Profile.route) }

            AlbumDetailUiAction.ShareClicked ->
                snackbar(AlbumSnackbarKey.ShareComingSoon)

            is AlbumDetailUiAction.TabSelected ->
                when (action.tab) {
                    AlbumTab.Recaps ->
                        viewModelScope.launch {
                            _navEvent.emit(PhotogramDestination.RecapList.createRoute("1"))
                        }
                    AlbumTab.Members ->
                        viewModelScope.launch {
                            _navEvent.emit(PhotogramDestination.EventList.createRoute("1"))
                        }
                    else ->
                        _uiState.update { it.copy(selectedTab = action.tab) }
                }

            AlbumDetailUiAction.UploadClicked ->
                snackbar(AlbumSnackbarKey.UploadComingSoon)

            is AlbumDetailUiAction.PhotoTapped ->
                snackbar(AlbumSnackbarKey.PhotoComingSoon)

            is AlbumDetailUiAction.CalendarPageChanged ->
                _uiState.update { it.copy(calendarPage = action.page) }

            AlbumDetailUiAction.YearViewToggled ->
                _uiState.update { it.copy(showYearView = !it.showYearView) }

            AlbumDetailUiAction.DownloadClicked    -> snackbar(AlbumSnackbarKey.DownloadComingSoon)
            AlbumDetailUiAction.CreateAlbumClicked -> snackbar(AlbumSnackbarKey.CreateAlbumComingSoon)
        }
    }

    private fun snackbar(key: AlbumSnackbarKey) =
        viewModelScope.launch { _snackbarEvent.emit(key) }
}
