package com.photogram.feature.home

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

internal enum class HomeSnackbarKey {
    AlbumComingSoon,
    FeaturedComingSoon,
    SearchComingSoon,
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferences: UserPreferencesRepository,
) : ViewModel() {

    // Start empty; populate with demo defaults only when in demo/prototype mode.
    private val _uiState = MutableStateFlow(
        HomeUiState(
            stories              = emptyList(),
            featuredMemory       = null,
            albums               = emptyList(),
            unreadNotifications  = 0,
            unreadMessages       = 0,
        ),
    )
    internal val uiState = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val navEvent: SharedFlow<String> = _navEvent.asSharedFlow()

    private val _snackbarEvent = MutableSharedFlow<HomeSnackbarKey>(extraBufferCapacity = 1)
    internal val snackbarEvent: SharedFlow<HomeSnackbarKey> = _snackbarEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            if (userPreferences.userData.first().isDemoMode) {
                _uiState.value = HomeUiState(
                    stories             = HomeDefaults.stories,
                    featuredMemory      = HomeDefaults.featuredMemory,
                    albums              = HomeDefaults.albums,
                    currentUserAvatarUrl = HomeDefaults.userAvatarUrl,
                    unreadNotifications = 3,
                    unreadMessages      = 2,
                )
            }
        }
    }

    internal fun onAction(action: HomeUiAction) {
        when (action) {
            HomeUiAction.BellClicked ->
                navigate(PhotogramDestination.Notifications.route)

            HomeUiAction.SearchClicked ->
                snackbar(HomeSnackbarKey.SearchComingSoon)

            HomeUiAction.HomeNavClicked ->
                Unit // already on home

            HomeUiAction.GalleryNavClicked ->
                navigate(PhotogramDestination.AlbumDetail.createRoute("1"))

            HomeUiAction.ProfileNavClicked ->
                navigate(PhotogramDestination.Profile.route)

            is HomeUiAction.StoryClicked ->
                navigate(PhotogramDestination.StoryViewer.createRoute(action.storyId))

            is HomeUiAction.AlbumClicked ->
                navigate(PhotogramDestination.AlbumDetail.createRoute(action.albumId))

            HomeUiAction.FeaturedMemoryClicked ->
                navigate(PhotogramDestination.RecapList.createRoute("featured"))

            HomeUiAction.CreateClicked ->
                navigate(PhotogramDestination.Camera.route)

            HomeUiAction.CameraSheetDismissed ->
                _uiState.update { it.copy(showCameraSheet = false) }

            HomeUiAction.CameraStoryTapped -> {
                _uiState.update { it.copy(showCameraSheet = false) }
                navigate(PhotogramDestination.Camera.route)
            }

            HomeUiAction.CameraGalleryTapped -> {
                _uiState.update { it.copy(showCameraSheet = false) }
                navigate(PhotogramDestination.Camera.route)
            }

            HomeUiAction.CameraAlbumTapped -> {
                _uiState.update { it.copy(showCameraSheet = false) }
                navigate(PhotogramDestination.Camera.route)
            }

            HomeUiAction.ChatNavClicked ->
                navigate(PhotogramDestination.ChatList.route)
        }
    }

    private fun navigate(route: String) {
        viewModelScope.launch { _navEvent.emit(route) }
    }

    private fun snackbar(key: HomeSnackbarKey) {
        viewModelScope.launch { _snackbarEvent.emit(key) }
    }
}
