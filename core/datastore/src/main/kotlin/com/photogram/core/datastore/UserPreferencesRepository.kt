package com.photogram.core.datastore

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userData: Flow<UserData>
    suspend fun setUserId(userId: String?)
    suspend fun setOnboardingComplete(complete: Boolean)
    suspend fun clearSession()
    suspend fun setLanguage(code: String)
    /** Explicitly activate (true) or deactivate (false) prototype/demo mode. */
    suspend fun setProtoMode(enabled: Boolean)
    /** Persist the user's "keep me signed in" choice. */
    suspend fun setKeepSignedIn(enabled: Boolean)
    /** Increment invite welcome display count (max 3). Never resets on sign-out. */
    suspend fun incrementInviteWelcomeDisplayCount()
    /** Persist the authenticated user's email address. Called by AppViewModel on session auth. */
    suspend fun setEmail(email: String)
    /** Persist editable profile fields. Called by EditProfileViewModel on save. */
    suspend fun setProfile(displayName: String, username: String, bio: String)
    /** Persist local avatar content URI. Called by EditProfileViewModel on save. */
    suspend fun setAvatarUri(uri: String)
}
