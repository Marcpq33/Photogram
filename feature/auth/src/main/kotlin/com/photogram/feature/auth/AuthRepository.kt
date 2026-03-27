package com.photogram.feature.auth

import android.app.Activity
import com.photogram.core.common.PhotogramResult

interface AuthRepository {

    /**
     * Creates a new account with [email] and [password] via Supabase Email provider.
     *
     * Returns [PhotogramResult.Success] with [SignUpOutcome.SessionEstablished] when the account
     * is created and a session is active immediately ("Confirm email" disabled in dashboard).
     *
     * Returns [PhotogramResult.Success] with [SignUpOutcome.AwaitingEmailConfirmation] when the
     * account is created but the user must click the confirmation link before signing in
     * ("Confirm email" enabled in dashboard).
     *
     * Returns [PhotogramResult.Error] if the email already exists, password is too short,
     * email format is invalid, or a network error occurs.
     */
    suspend fun signUp(email: String, password: String): PhotogramResult<SignUpOutcome>

    /**
     * Signs in with [email] and [password] via Supabase Email provider.
     *
     * Returns [PhotogramResult.Success] when the session is established.
     * Returns [PhotogramResult.Error] if credentials are wrong, account does not exist, or network fails.
     *
     * Important: accounts created via magic link (OTP) have no password_hash.
     * Calling signIn for such an account returns "Invalid login credentials" (HTTP 400).
     * Those users must reset their password before using this flow.
     */
    suspend fun signIn(email: String, password: String): PhotogramResult<Unit>

    /**
     * Signs in with a Google account via Credential Manager (native Android bottom sheet).
     *
     * Requires [activity] for the Credential Manager UI. Uses GetSignInWithGoogleOption
     * with a SHA-256 nonce, then exchanges the Google ID token for a Supabase session via
     * the IDToken provider.
     *
     * Returns [PhotogramResult.Success] when the Supabase session is established.
     * Returns [PhotogramResult.Error] with a null message when the user cancels (silent — no UI error).
     * Returns [PhotogramResult.Error] with a message for all other failures.
     *
     * Prerequisite: GOOGLE_WEB_CLIENT_ID must be set in local.properties and match the
     * Web Client ID configured in Supabase → Authentication → Providers → Google.
     */
    suspend fun signInWithGoogle(activity: Activity): PhotogramResult<Unit>

    /**
     * Initiates Apple sign-in via Supabase OAuth browser flow.
     *
     * Opens the system browser (or Chrome Custom Tab) with the Apple OAuth URL.
     * The function returns after launching the browser — the session is established
     * asynchronously when the browser redirects to io.photogram://callback, which
     * MainActivity.handleAuthDeeplink() processes via supabaseClient.handleDeeplinks().
     *
     * Returns [PhotogramResult.Success] when the browser was launched successfully.
     * Returns [PhotogramResult.Error] if the OAuth URL cannot be generated or the browser
     * cannot be opened.
     */
    suspend fun initiateAppleSignIn(): PhotogramResult<Unit>
}
