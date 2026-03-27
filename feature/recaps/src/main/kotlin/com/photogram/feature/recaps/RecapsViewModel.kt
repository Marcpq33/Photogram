package com.photogram.feature.recaps

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecapsViewModel @Inject constructor(
    private val userPreferences: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecapsUiState(featured = emptyList(), personal = emptyList()))
    val uiState: StateFlow<RecapsUiState> = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val navEvent: SharedFlow<String> = _navEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            if (userPreferences.userData.first().isDemoMode) {
                _uiState.value = RecapsUiState(
                    featured = RecapsMock.featured,
                    personal = RecapsMock.personal,
                )
            }
        }
    }

    fun onAction(action: RecapsUiAction) {
        when (action) {
            RecapsUiAction.BackClicked        -> viewModelScope.launch {
                _navEvent.emit(PhotogramDestination.Home.route)
            }
            RecapsUiAction.HomeNavClicked     -> viewModelScope.launch {
                _navEvent.emit(PhotogramDestination.Home.route)
            }
            RecapsUiAction.GalleryNavClicked  -> viewModelScope.launch {
                _navEvent.emit(PhotogramDestination.AlbumDetail.createRoute("1"))
            }
            RecapsUiAction.CreateNavClicked   -> viewModelScope.launch {
                _navEvent.emit(PhotogramDestination.Camera.route)
            }
            RecapsUiAction.ChatNavClicked     -> viewModelScope.launch {
                _navEvent.emit(PhotogramDestination.ChatList.route)
            }
            RecapsUiAction.ProfileNavClicked  -> viewModelScope.launch {
                _navEvent.emit(PhotogramDestination.Profile.route)
            }
            RecapsUiAction.SettingsClicked    -> { /* placeholder — settings in future milestone */ }
            RecapsUiAction.CreateRecapClicked -> { /* placeholder — create flow in future milestone */ }
            is RecapsUiAction.RecapClicked    -> { /* placeholder — playback in future milestone */ }
        }
    }
}
