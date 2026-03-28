package com.photogram.feature.home

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

internal enum class HomeSnackbarKey {
    AlbumComingSoon,
    FeaturedComingSoon,
    SearchComingSoon,
    LinkCopied,
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferences: UserPreferencesRepository,
    private val ownStorySessionStore: OwnStorySessionStore,
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

    /**
     * In-memory set of story IDs already seen this session.
     * Declared before init so the property is never null when the init coroutine runs,
     * even if seenStoryIds.first() / liveSeenStoryIds.value return synchronously.
     */
    private val seenStoryIds = mutableSetOf<String>()

    /** Cached avatar URI for the current real user — used in myStoryBubble() and nav bar. */
    private var currentAvatarUri: String = ""

    init {
        viewModelScope.launch {
            // Restore seen IDs from two sources and merge them:
            //
            //   (a) DataStore (persisted) — covers cold starts and process restarts where
            //       OwnStorySessionStore.liveSeenStoryIds is empty (no markSeenStoryId call yet).
            //
            //   (b) liveSeenStoryIds (in-memory singleton) — covers same-session ViewModel
            //       recreation: when the user forward-navigates to Home from another screen
            //       (e.g. Profile bottom-nav → Home), a new NavBackStackEntry and a new
            //       HomeViewModel are created. The DataStore write from markSeenStoryId may
            //       not have been replayed yet, but liveSeenStoryIds is updated synchronously
            //       before the DataStore write, so it always reflects the true current state.
            //
            // Merging both eliminates the async-read race without requiring a CoroutineScope
            // at the OwnStorySessionStore singleton level.
            val persisted = ownStorySessionStore.seenStoryIds.first()
            val live      = ownStorySessionStore.liveSeenStoryIds.value
            seenStoryIds.addAll(persisted)
            seenStoryIds.addAll(live)

            val firstUserData = userPreferences.userData.first()
            if (firstUserData.isDemoMode) {
                _uiState.value = HomeUiState(
                    stories             = sortedStories(HomeDefaults.stories.map { s ->
                        s.copy(isSeen = s.id in seenStoryIds)
                    }),
                    featuredMemory      = HomeDefaults.featuredMemory,
                    albums              = HomeDefaults.albums,
                    currentUserAvatarUrl = HomeDefaults.userAvatarUrl,
                    unreadNotifications = 3,
                    unreadMessages      = 2,
                )
            } else {
                // Show invite welcome popup up to 3 times on first entries.
                // Counter is incremented here (at show time) and never resets on sign-out.
                if (firstUserData.shouldShowInviteWelcome) {
                    _uiState.update { it.copy(showInviteWelcome = true) }
                    userPreferences.incrementInviteWelcomeDisplayCount()
                }
                // Observe userData reactively so avatar changes (e.g. after Edit Profile Save)
                // are reflected immediately in the nav bar and YOU bubble.
                launch {
                    userPreferences.userData.collect { userData ->
                        currentAvatarUri = userData.avatarUri
                        _uiState.update { it.copy(
                            currentUserAvatarUrl = currentAvatarUri.ifBlank { null },
                        ) }
                    }
                }
                // Real user — observe story store reactively.
                // Handles cold start with existing stories, new stories added, and
                // story deletions from the viewer (reverts to ADD NEW when list is empty).
                // seenStoryIds is already populated above so isSeen is preserved on re-emission.
                ownStorySessionStore.storyUris.collect { uris ->
                    _uiState.update { state ->
                        val updated = if (uris.isNotEmpty()) listOf(myStoryBubble())
                                      else listOf(HomeDefaults.stories.first())
                        state.copy(
                            stories = sortedStories(updated.map { s ->
                                s.copy(isSeen = s.id in seenStoryIds)
                            }),
                        )
                    }
                }
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

            is HomeUiAction.StoryClicked -> {
                markStoryAsSeen(action.storyId)
                navigate(PhotogramDestination.StoryViewer.createRoute(action.storyId))
            }

            HomeUiAction.AddNewStoryClicked ->
                navigate(PhotogramDestination.Camera.route)

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

            HomeUiAction.StoryPublished ->
                activateMyStory()

            HomeUiAction.InviteWelcomeDismissed ->
                dismissInviteWelcome()

            HomeUiAction.InviteLinkCopied -> {
                // Popup stays open so user can keep trying other share options.
                markInviteWelcomeShown()
                snackbar(HomeSnackbarKey.LinkCopied)
            }

            HomeUiAction.InviteSharedOnce ->
                markInviteWelcomeShown()
        }
    }

    private fun dismissInviteWelcome() {
        // Counter was already incremented when the popup was shown — just hide it.
        _uiState.update { it.copy(showInviteWelcome = false) }
    }

    /** No-op: counter is incremented at show time, not at dismiss or share time. */
    private fun markInviteWelcomeShown() = Unit

    // Transitions real-user stories row from ADD NEW → MY STORY with canAddMore=true.
    // Only fires when the list is exactly [ADD NEW] (real user, first story publish this session).
    // DataStore persistence in OwnStorySessionStore ensures the bubble survives cold starts.
    private fun activateMyStory() {
        _uiState.update { state ->
            if (state.stories.size == 1 && state.stories.first().isAddNew) {
                state.copy(stories = listOf(myStoryBubble()))
            } else state
        }
    }

    /**
     * Marks a story bubble as seen and re-sorts the row:
     * [isAddNew] → [unseen] → [seen], preserving original relative order within each group.
     * Persists the seen ID to DataStore so it survives ViewModel recreation.
     * No-ops if already seen.
     */
    private fun markStoryAsSeen(storyId: String) {
        if (!seenStoryIds.add(storyId)) return          // already tracked — no state update needed
        viewModelScope.launch { ownStorySessionStore.markSeenStoryId(storyId) }
        _uiState.update { state ->
            val updated = state.stories.map { s ->
                if (s.id == storyId) s.copy(isSeen = true) else s
            }
            state.copy(stories = sortedStories(updated))
        }
    }

    /**
     * Stable sort: ADD_NEW pin first, then unseen bubbles, then seen bubbles.
     * Original relative order is preserved within each group.
     */
    private fun sortedStories(stories: List<HomeStory>): List<HomeStory> {
        val pinned = stories.filter { it.isAddNew }
        val unseen = stories.filter { !it.isAddNew && !it.isSeen }
        val seen   = stories.filter { !it.isAddNew &&  it.isSeen }
        return pinned + unseen + seen
    }

    /** Canonical own-story bubble: gold ring + canAddMore badge. */
    private fun myStoryBubble() = HomeStory(
        id             = "my_story",
        label          = "YOU",
        coverColorArgb = 0xFFC9A96EL, // Gold
        isAddNew       = false,
        canAddMore     = true,
        imageUrl       = currentAvatarUri.ifBlank { null },
    )

    private fun navigate(route: String) {
        viewModelScope.launch { _navEvent.emit(route) }
    }

    private fun snackbar(key: HomeSnackbarKey) {
        viewModelScope.launch { _snackbarEvent.emit(key) }
    }
}
