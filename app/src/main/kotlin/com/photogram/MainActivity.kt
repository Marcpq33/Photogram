package com.photogram

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.photogram.core.designsystem.PhotogramTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Field-injected by Hilt before onCreate. Used to process magic-link deep links.
    // Assumption: SupabaseClient is a @Singleton provided by core:network's NetworkModule.
    @Inject lateinit var supabaseClient: SupabaseClient

    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Handle a deep link that cold-started the app (e.g. user clicked magic link
        // while the app was not running).
        handleAuthDeeplink(intent)
        setContent {
            val startDestination by appViewModel.startDestination.collectAsStateWithLifecycle()
            val languageCode by appViewModel.languageCode.collectAsStateWithLifecycle()
            PhotogramTheme {
                val dest = startDestination
                if (dest == null) {
                    SplashScreen()
                } else {
                    PhotogramApp(
                        startDestination = dest,
                        authEvent = appViewModel.authEvent,
                        languageCode = languageCode,
                        onSignOut = { appViewModel.signOut() },
                    )
                }
            }
        }
    }

    // Called when the app is already running (singleTask) and the user clicks the magic link.
    // The OS delivers the deep-link intent here instead of creating a new MainActivity.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleAuthDeeplink(intent)
    }

    /**
     * Processes a Supabase auth redirect URI via the SDK's handleDeeplinks extension.
     *
     * Expected URI shape: io.photogram://callback?code=<pkce_code>
     *                 or: io.photogram://callback#access_token=<token>&...  (implicit flow)
     *
     * handleDeeplinks is NOT suspend — it launches internally on authScope.
     * On success, supabaseClient.auth.sessionStatus emits SessionStatus.Authenticated, which
     * AppViewModel observes, persists to DataStore, and signals via authEvent for navigation.
     */
    private fun handleAuthDeeplink(intent: Intent) {
        supabaseClient.handleDeeplinks(
            intent = intent,
            onError = { e -> Log.w(TAG, "Auth deep-link error: ${e.message}") },
        )
    }

    private companion object {
        const val TAG = "MainActivity"
    }
}
