package com.photogram

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photogram.core.datastore.UserPreferencesRepository
import com.photogram.core.navigation.PhotogramDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    supabaseClient: SupabaseClient,
) : ViewModel() {

    // Fired once when the app transitions from signed-out → signed-in during a live session.
    // Sessions restored from storage on app start do NOT emit here — startDestination handles that.
    // Consumed by PhotogramApp to imperatively navigate to MainGraph mid-session.
    private val _authEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val authEvent: SharedFlow<Unit> = _authEvent.asSharedFlow()

    // null = session state not yet resolved; PhotogramApp renders nothing until resolved.
    // isProtoMode = true routes straight to MainGraph so proto content persists across restarts.
    val startDestination: StateFlow<String?> = userPreferencesRepository.userData
        .map { userData ->
            if (userData.userId != null || userData.isProtoMode) PhotogramDestination.MainGraph.route
            else PhotogramDestination.AuthGraph.route
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val languageCode: StateFlow<String> = userPreferencesRepository.userData
        .map { it.language }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "EN",
        )

    init {
        observeSession(supabaseClient)
    }

    // Verified against supabase-kt 3.4.1 source (auth-kt-android-3.4.1-sources.jar):
    //   SessionStatus sealed interface is in io.github.jan.supabase.auth.status
    //   Subtypes: Authenticated(session, source) | NotAuthenticated(isSignOut) |
    //             Initializing | RefreshFailure(cause)
    //   Authenticated.isNew = source is SignIn | SignUp | External
    //     → true  for fresh sign-in (magic-link, OAuth, password)
    //     → false for session restored from storage on app start
    //   UserSession.user: UserInfo? — UserInfo.id: String (UUID)
    private fun observeSession(supabaseClient: SupabaseClient) {
        supabaseClient.auth.sessionStatus
            .onEach { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        val userId = status.session.user?.id ?: return@onEach
                        userPreferencesRepository.setUserId(userId)
                        // Only emit authEvent for new sessions. Storage-restored sessions
                        // are handled by startDestination (DataStore already has the userId).
                        if (status.isNew) {
                            _authEvent.tryEmit(Unit)
                        }
                    }
                    is SessionStatus.NotAuthenticated -> {
                        userPreferencesRepository.clearSession()
                    }
                    else -> Unit // Initializing, RefreshFailure — leave DataStore unchanged
                }
            }
            .launchIn(viewModelScope)
    }
}
