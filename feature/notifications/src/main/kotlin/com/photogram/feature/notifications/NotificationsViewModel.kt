package com.photogram.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class NotificationsViewModel @Inject constructor(
    private val userPreferences: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState(allItems = emptyList()))
    internal val uiState = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val navEvent: SharedFlow<String> = _navEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            if (userPreferences.userData.first().isDemoMode) {
                _uiState.value = NotificationsUiState(allItems = NotifDefaults.items)
            }
        }
    }

    internal fun onAction(action: NotificationsUiAction) {
        when (action) {
            is NotificationsUiAction.FilterSelected ->
                _uiState.update { it.copy(selectedFilter = action.filter) }

            NotificationsUiAction.FilterIconTapped ->
                _uiState.update { it.copy(showFilterSheet = true) }

            NotificationsUiAction.FilterSheetDismissed ->
                _uiState.update { it.copy(showFilterSheet = false) }

            is NotificationsUiAction.NotifTapped ->
                navigate(PhotogramDestination.AlbumDetail.createRoute("1"))

            NotificationsUiAction.HomeNavTapped ->
                navigate(PhotogramDestination.Home.route)

            NotificationsUiAction.GalleryNavTapped ->
                navigate(PhotogramDestination.Gallery.route)

            NotificationsUiAction.CreateNavTapped ->
                navigate(PhotogramDestination.Camera.route)

            NotificationsUiAction.ProfileNavTapped ->
                navigate(PhotogramDestination.Profile.route)
        }
    }

    private fun navigate(route: String) {
        viewModelScope.launch { _navEvent.emit(route) }
    }
}
