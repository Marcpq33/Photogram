package com.photogram.feature.auth

import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.photogram.core.common.PhotogramResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Apple
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.CancellationException
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

// API assumptions: supabase-kt 3.4.1
//   Email:   auth.signUpWith(Email) / auth.signInWith(Email) — suspend, throws on failure
//   IDToken: auth.signInWith(IDToken) { idToken; provider; nonce } — native Google token exchange
//   Apple:   auth.signInWith(Apple) { redirectUrl } — opens browser OAuth, returns after launch;
//            session arrives later via io.photogram://callback deep link → MainActivity
private const val TAG = "AuthRepository"

internal class AuthRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
) : AuthRepository {

    // ── Email ─────────────────────────────────────────────────────────────────

    override suspend fun signUp(email: String, password: String): PhotogramResult<SignUpOutcome> {
        if (supabaseClient.supabaseUrl.isBlank()) {
            return PhotogramResult.Error(
                exception = IllegalStateException("Supabase credentials are not configured"),
                message = "Auth service not configured. Add SUPABASE_URL and SUPABASE_ANON_KEY to local.properties and rebuild.",
            )
        }
        Log.d(TAG, "signUp: ${email.take(3)}***")
        return try {
            supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            // If "Confirm email" is enabled in the Supabase dashboard, signUpWith succeeds
            // (no exception) but no session is created until the user clicks the link.
            // currentSessionOrNull() == null in that case.
            val sessionActive = supabaseClient.auth.currentSessionOrNull() != null
            Log.d(TAG, "signUp: success — sessionActive=$sessionActive")
            PhotogramResult.Success(
                if (sessionActive) SignUpOutcome.SessionEstablished
                else SignUpOutcome.AwaitingEmailConfirmation
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "signUp: failed [${e::class.simpleName}]")
            PhotogramResult.Error(exception = e, message = mapSignUpError(e))
        }
    }

    override suspend fun signIn(email: String, password: String): PhotogramResult<Unit> {
        if (supabaseClient.supabaseUrl.isBlank()) {
            return PhotogramResult.Error(
                exception = IllegalStateException("Supabase credentials are not configured"),
                message = "Auth service not configured. Add SUPABASE_URL and SUPABASE_ANON_KEY to local.properties and rebuild.",
            )
        }
        Log.d(TAG, "signIn: ${email.take(3)}***")
        return try {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Log.d(TAG, "signIn: success")
            PhotogramResult.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "signIn: failed [${e::class.simpleName}]")
            PhotogramResult.Error(
                exception = e,
                message = "Sign in failed. Check your email and password and try again.",
            )
        }
    }

    // ── Google (Credential Manager + Supabase IDToken) ────────────────────────

    override suspend fun signInWithGoogle(activity: Activity): PhotogramResult<Unit> {
        if (BuildConfig.GOOGLE_WEB_CLIENT_ID.isBlank()) {
            Log.e(TAG, "signInWithGoogle: GOOGLE_WEB_CLIENT_ID is blank — add it to local.properties and rebuild")
            return PhotogramResult.Error(
                exception = IllegalStateException("GOOGLE_WEB_CLIENT_ID not configured"),
                message = "[Config] GOOGLE_WEB_CLIENT_ID is empty. Add the Web Client ID from Google Cloud Console to local.properties and rebuild.",
            )
        }
        Log.d(TAG, "signInWithGoogle: client id present (${BuildConfig.GOOGLE_WEB_CLIENT_ID.take(8)}…), launching Credential Manager")
        return try {
            // Nonce: raw value sent to Supabase; hashed value sent to Google.
            // Supabase verifies that sha256(rawNonce) matches the nonce in the Google ID token.
            val rawNonce = UUID.randomUUID().toString()
            val hashedNonce = sha256Hex(rawNonce)

            val option = GetSignInWithGoogleOption
                .Builder(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .setNonce(hashedNonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build()

            Log.d(TAG, "signInWithGoogle: calling CredentialManager.getCredential()")
            val response = CredentialManager.create(activity)
                .getCredential(context = activity, request = request)

            val credential = response.credential
            Log.d(TAG, "signInWithGoogle: credential received, type=${credential.type}")
            if (credential !is CustomCredential ||
                credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                return PhotogramResult.Error(
                    exception = IllegalStateException("Unexpected credential type: ${credential.type}"),
                    message = "[CredentialType] Unexpected credential type: ${credential.type}. Expected Google ID token.",
                )
            }

            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            Log.d(TAG, "signInWithGoogle: Google ID token obtained, exchanging with Supabase")

            // Exchange the Google ID token for a Supabase session.
            // rawNonce (not hashed) is passed here — Supabase hashes it internally for verification.
            supabaseClient.auth.signInWith(IDToken) {
                idToken = googleCredential.idToken
                provider = Google
                nonce = rawNonce
            }

            Log.d(TAG, "signInWithGoogle: success — Supabase session established")
            PhotogramResult.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: GetCredentialCancellationException) {
            // User dismissed the account picker — treat as silent cancel, no error shown.
            Log.d(TAG, "signInWithGoogle: cancelled by user")
            PhotogramResult.Error(exception = e, message = null)
        } catch (e: NoCredentialException) {
            Log.w(TAG, "signInWithGoogle: NoCredentialException — ${e.message}")
            PhotogramResult.Error(
                exception = e,
                message = "[NoCredential] No Google account found on this device. Add one in Settings → Accounts and try again.",
            )
        } catch (e: Exception) {
            // Common causes: wrong SHA-1 in Google Cloud Console, Supabase Google provider
            // not enabled, token rejected (nonce mismatch), no Play Services on emulator.
            Log.e(TAG, "signInWithGoogle: failed [${e::class.simpleName}] ${e.message}")
            val cause = when {
                e.message?.contains("10:", ignoreCase = true) == true ->
                    "[SHA-1] Error code 10 — add your debug SHA-1 fingerprint to Google Cloud Console → Credentials → Android app."
                e.message?.contains("developer_error", ignoreCase = true) == true ->
                    "[Config] developer_error — check SHA-1 fingerprint and OAuth client ID match in Google Cloud Console."
                e.message?.contains("sign_in_failed", ignoreCase = true) == true ->
                    "[OAuth] sign_in_failed — verify Google provider is enabled in Supabase → Authentication → Providers."
                e.message?.contains("invalid_grant", ignoreCase = true) == true ||
                e.message?.contains("nonce", ignoreCase = true) == true ->
                    "[Nonce] Token rejected — nonce mismatch between Google and Supabase. This is usually a library version issue."
                e.message?.contains("supabase", ignoreCase = true) == true ||
                e.message?.contains("401", ignoreCase = true) == true ->
                    "[Supabase] Token rejected by Supabase — verify Client ID and Client Secret in Supabase → Authentication → Providers → Google."
                else ->
                    "[${e::class.simpleName}] ${e.message?.take(120) ?: "unknown error"}"
            }
            PhotogramResult.Error(exception = e, message = cause)
        }
    }

    // ── Apple (Supabase OAuth browser flow) ───────────────────────────────────

    override suspend fun initiateAppleSignIn(): PhotogramResult<Unit> {
        // supabase-kt 3.x on Android: signInWith(Apple) generates a PKCE-protected OAuth URL,
        // opens it in the system browser (Intent.FLAG_ACTIVITY_NEW_TASK via ApplicationContext),
        // then returns. The session arrives later via the io.photogram://callback deep link,
        // which MainActivity.handleAuthDeeplink() processes → AppViewModel emits authEvent → Home.
        return try {
            // Redirect URL is derived automatically from the scheme/host configured in
            // SupabaseClientProvider: scheme = "io.photogram", host = "callback"
            // → io.photogram://callback. No block needed.
            supabaseClient.auth.signInWith(Apple)
            Log.d(TAG, "initiateAppleSignIn: browser launched")
            PhotogramResult.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "initiateAppleSignIn: failed [${e::class.simpleName}]")
            PhotogramResult.Error(
                exception = e,
                message = "Apple sign-in is unavailable. Please try again.",
            )
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun sha256Hex(input: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

    private fun mapSignUpError(e: Exception): String = when {
        e.message?.contains("already registered", ignoreCase = true) == true ->
            "An account with this email already exists. Try signing in instead."
        e.message?.contains("Password should be at least", ignoreCase = true) == true ->
            "Password must be at least 6 characters."
        e.message?.contains("Unable to validate email", ignoreCase = true) == true ||
            e.message?.contains("invalid format", ignoreCase = true) == true ->
            "Please enter a valid email address."
        else -> "Sign up failed. Please try again."
    }
}
