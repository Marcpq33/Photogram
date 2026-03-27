package com.photogram.feature.auth

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
}
