package com.photogram.feature.profile

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferences: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            displayName         = "",
            capturingSinceYear  = 0,
            totalPhotos         = 0,
            albumsCount         = 0,
            daysStreak          = 0,
            recaps              = emptyList(),
            albums              = emptyList(),
        ),
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<String>()
    val navEvent: SharedFlow<String> = _navEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            userPreferences.userData.collect { userData ->
                if (userData.isDemoMode) {
                    _uiState.value = ProfileUiState(
                        displayName        = "Alex Morgan",
                        capturingSinceYear = 2022,
                        totalPhotos        = 247,
                        albumsCount        = 4,
                        daysStreak         = 21,
                        recaps             = ProfileDefaults.recaps,
                        albums             = ProfileDefaults.albums,
                    )
                } else {
                    _uiState.update { it.copy(
                        displayName = userData.displayName,
                        username    = userData.username,
                        bio         = userData.bio,
                        avatarUri   = userData.avatarUri,
                    ) }
                }
            }
        }
    }

    fun onAction(action: ProfileUiAction) {
        when (action) {
            ProfileUiAction.HomeNavClicked -> viewModelScope.launch {
                _navEvent.emit(PhotogramDestination.Home.route)
            }
            ProfileUiAction.GalleryNavClicked -> viewModelScope.launch {
                _navEvent.emit(PhotogramDestination.AlbumDetail.createRoute("1"))
            }
            ProfileUiAction.SettingsClicked -> viewModelScope.launch {
                _navEvent.emit(PhotogramDestination.Settings.route)
            }
            ProfileUiAction.CreateClicked ->
                _uiState.update { it.copy(showNewPost = true) }

            ProfileUiAction.NewPostDismissed ->
                // reset uri so next open starts fresh
                _uiState.update { it.copy(showNewPost = false, newPostMediaUri = null) }

            ProfileUiAction.NewPostCameraClicked ->
                // reuse the validated CameraScreen — permissions + CameraX already handled there
                viewModelScope.launch { _navEvent.emit(PhotogramDestination.Camera.route) }

            is ProfileUiAction.NewPostMediaSelected ->
                _uiState.update { it.copy(newPostMediaUri = action.uri) }

            ProfileUiAction.ChatNavClicked -> viewModelScope.launch {
                _navEvent.emit(PhotogramDestination.ChatList.route)
            }

            ProfileUiAction.FavoritesClicked -> viewModelScope.launch {
                _navEvent.emit(PhotogramDestination.EventList.createRoute("mock_album_01"))
            }

            else -> { /* handled in future milestones */ }
        }
    }
}
