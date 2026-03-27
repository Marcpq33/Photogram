package com.photogram.feature.auth

/**
 * Outcome of a successful signUp call (no exception thrown).
 *
 * Supabase can be configured in two ways:
 *  - "Confirm email" disabled → session is established immediately → [SessionEstablished]
 *  - "Confirm email" enabled  → user must click the link first; no session yet → [AwaitingEmailConfirmation]
 *
 * Toggle in Supabase dashboard: Authentication → Providers → Email → Confirm email.
 */
sealed interface SignUpOutcome {
    /** Account created and session active. AppViewModel.authEvent will fire → navigate to Home. */
    data object SessionEstablished : SignUpOutcome

    /** Account created but email confirmation is required before a session is issued. */
    data object AwaitingEmailConfirmation : SignUpOutcome
}
