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
}
