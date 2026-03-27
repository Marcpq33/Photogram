package com.photogram.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {

    override val userData: Flow<UserData> = dataStore.data.map { prefs ->
        UserData(
            userId               = prefs[KEY_USER_ID],
            isOnboardingComplete = prefs[KEY_ONBOARDING_COMPLETE] ?: false,
            language             = prefs[KEY_LANGUAGE] ?: "EN",
            isProtoMode          = prefs[KEY_PROTO_MODE] ?: false,
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
        // Clear auth keys only — preserve language preference and proto mode flag so that
        // Supabase emitting NotAuthenticated during a proto session does not wipe demo content.
        dataStore.edit { prefs ->
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_ONBOARDING_COMPLETE)
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

    private companion object {
        val KEY_USER_ID             = stringPreferencesKey("user_id")
        val KEY_ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val KEY_LANGUAGE            = stringPreferencesKey("language")
        val KEY_PROTO_MODE          = booleanPreferencesKey("proto_mode")
    }
}
