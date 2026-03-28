package com.photogram

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photogram.core.datastore.UserPreferencesRepository
import com.photogram.core.navigation.PhotogramDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val supabaseClient: SupabaseClient,
) : ViewModel() {

    // Fired once when the app transitions from signed-out → signed-in during a live session.
    // Sessions restored from storage on app start do NOT emit here — startDestination handles that.
    // Consumed by PhotogramApp to imperatively navigate to MainGraph mid-session.
    private val _authEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val authEvent: SharedFlow<Unit> = _authEvent.asSharedFlow()

    // null = session state not yet resolved; PhotogramApp renders nothing until resolved.
    // isProtoMode = true routes straight to MainGraph so proto content persists across restarts.
    // keepSignedIn must be true for a stored userId to auto-enter; false forces AuthGraph on cold start.
    val startDestination: StateFlow<String?> = userPreferencesRepository.userData
        .map { userData ->
            if ((userData.userId != null && userData.keepSignedIn) || userData.isProtoMode) PhotogramDestination.MainGraph.route
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
                        // Persist email from Supabase auth so Edit Profile can read it
                        // without requiring a Postgrest dependency in the settings module.
                        val email = status.session.user?.email
                        if (!email.isNullOrEmpty()) {
                            userPreferencesRepository.setEmail(email)
                        }
                        // On fresh sign-in, seed displayName from OAuth user metadata
                        // (Google/Apple provide full_name; email sign-up does not).
                        // Only seeds if DataStore displayName is still blank so that a user
                        // who edited their display name via Edit Profile is not overwritten.
                        if (status.isNew) {
                            val metaName = try {
                                status.session.user?.userMetadata
                                    ?.get("full_name")
                                    ?.jsonPrimitive
                                    ?.contentOrNull
                                    ?.trim()
                                    ?.takeIf { it.isNotBlank() }
                            } catch (_: Exception) { null }
                            if (!metaName.isNullOrBlank()) {
                                val current = userPreferencesRepository.userData.first()
                                if (current.displayName.isBlank()) {
                                    userPreferencesRepository.setProfile(
                                        displayName = metaName,
                                        username    = current.username,
                                        bio         = current.bio,
                                    )
                                }
                            }
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

    /**
     * Signs out from Supabase and clears the local session.
     *
     * Calling [supabaseClient.auth.signOut] emits [SessionStatus.NotAuthenticated], which
     * [observeSession] catches and uses to call [userPreferencesRepository.clearSession].
     * This ensures DataStore is wiped so the next cold start lands on AuthGraph.
     *
     * Navigation to AuthGraph is the caller's responsibility (handled in PhotogramApp.onLogOut).
     * If the network sign-out fails, we clear the local session anyway so the user is not
     * stuck on MainGraph.
     */
    fun signOut() {
        viewModelScope.launch {
            try {
                supabaseClient.auth.signOut()
            } catch (e: Exception) {
                // Remote sign-out failed (network error, token already expired, etc.).
                // Clear local session regardless so DataStore userId is wiped.
                userPreferencesRepository.clearSession()
            }
        }
    }
}
