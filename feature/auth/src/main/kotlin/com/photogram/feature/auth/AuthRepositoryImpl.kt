package com.photogram.feature.auth

import android.util.Log
import com.photogram.core.common.PhotogramResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

// Assumption: supabase-kt 3.x API.
//   - supabaseClient.auth.signUpWith(Email) { email = "..."; password = "..." }
//   - supabaseClient.auth.signInWith(Email) { email = "..."; password = "..." }
//   Both are suspend functions that return Unit on success and throw on failure.
//   On success the session is established immediately and sessionStatus emits Authenticated.
private const val TAG = "AuthRepository"

internal class AuthRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
) : AuthRepository {

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
            PhotogramResult.Error(
                exception = e,
                message = mapSignUpError(e),
            )
        }
    }

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
            Log.e(TAG, "signIn: failed [${e::class.simpleName}] ${e.message}", e)
            PhotogramResult.Error(
                exception = e,
                message = "Sign in failed. Check your email and password and try again.",
            )
        }
    }
}
