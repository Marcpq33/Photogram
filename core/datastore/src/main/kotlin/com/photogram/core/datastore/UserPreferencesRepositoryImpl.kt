package com.photogram.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {

    override val userData: Flow<UserData> = dataStore.data.map { prefs ->
        UserData(
            userId                      = prefs[KEY_USER_ID],
            isOnboardingComplete        = prefs[KEY_ONBOARDING_COMPLETE] ?: false,
            language                    = prefs[KEY_LANGUAGE] ?: "EN",
            isProtoMode                 = prefs[KEY_PROTO_MODE] ?: false,
            keepSignedIn                = prefs[KEY_KEEP_SIGNED_IN] ?: false,
            inviteWelcomeDisplayCount   = prefs[KEY_INVITE_WELCOME_COUNT] ?: 0,
            displayName                 = prefs[KEY_DISPLAY_NAME] ?: "",
            username                    = prefs[KEY_USERNAME] ?: "",
            bio                         = prefs[KEY_BIO] ?: "",
            email                       = prefs[KEY_EMAIL] ?: "",
            avatarUri                   = prefs[KEY_AVATAR_URI] ?: "",
        )
    }

    override suspend fun setUserId(userId: String?) {
        dataStore.edit { prefs ->
            if (userId != null) {
                prefs[KEY_USER_ID]    = userId
                prefs[KEY_PROTO_MODE] = false  // entering real-user mode clears proto mode
            } else {
                prefs.remove(KEY_USER_ID)
            }
        }
    }

    override suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_ONBOARDING_COMPLETE] = complete }
    }

    override suspend fun clearSession() {
        // Clear auth keys and profile fields. Preserve language and proto mode flag so that
        // Supabase emitting NotAuthenticated during a proto session does not wipe demo content.
        dataStore.edit { prefs ->
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_ONBOARDING_COMPLETE)
            prefs.remove(KEY_KEEP_SIGNED_IN)
            prefs.remove(KEY_EMAIL)
            prefs.remove(KEY_DISPLAY_NAME)
            prefs.remove(KEY_USERNAME)
            prefs.remove(KEY_BIO)
            prefs.remove(KEY_AVATAR_URI)
        }
    }

    override suspend fun setLanguage(code: String) {
        dataStore.edit { it[KEY_LANGUAGE] = code }
    }

    override suspend fun setProtoMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_PROTO_MODE] = enabled
            if (enabled) prefs.remove(KEY_USER_ID)  // proto mode = no real user
        }
    }

    override suspend fun setKeepSignedIn(enabled: Boolean) {
        dataStore.edit { it[KEY_KEEP_SIGNED_IN] = enabled }
    }

    override suspend fun incrementInviteWelcomeDisplayCount() {
        dataStore.edit { prefs ->
            val current = prefs[KEY_INVITE_WELCOME_COUNT] ?: 0
            prefs[KEY_INVITE_WELCOME_COUNT] = current + 1
        }
    }

    override suspend fun setEmail(email: String) {
        dataStore.edit { it[KEY_EMAIL] = email }
    }

    override suspend fun setProfile(displayName: String, username: String, bio: String) {
        dataStore.edit { prefs ->
            prefs[KEY_DISPLAY_NAME] = displayName
            prefs[KEY_USERNAME]     = username
            prefs[KEY_BIO]          = bio
        }
    }

    override suspend fun setAvatarUri(uri: String) {
        dataStore.edit { it[KEY_AVATAR_URI] = uri }
    }

    private companion object {
        val KEY_USER_ID               = stringPreferencesKey("user_id")
        val KEY_ONBOARDING_COMPLETE   = booleanPreferencesKey("onboarding_complete")
        val KEY_LANGUAGE              = stringPreferencesKey("language")
        val KEY_PROTO_MODE            = booleanPreferencesKey("proto_mode")
        val KEY_KEEP_SIGNED_IN        = booleanPreferencesKey("keep_signed_in")
        val KEY_INVITE_WELCOME_COUNT  = intPreferencesKey("invite_welcome_count")
        val KEY_EMAIL                 = stringPreferencesKey("email")
        val KEY_DISPLAY_NAME          = stringPreferencesKey("display_name")
        val KEY_USERNAME              = stringPreferencesKey("username")
        val KEY_BIO                   = stringPreferencesKey("bio")
        val KEY_AVATAR_URI            = stringPreferencesKey("avatar_uri")
    }
}
