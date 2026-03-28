package com.photogram.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persistent store for the user's own published story URIs.
 *
 * Backed by [DataStore<Preferences>] — survives process death and app restarts.
 *
 * URIs are stored as a newline-separated string so ordering is preserved and
 * no additional serialization dependency is needed.
 * Android content:// and file:// URIs never contain newlines, so the delimiter is safe.
 *
 * All mutating functions are suspend — callers must invoke them from a coroutine scope
 * (e.g. viewModelScope.launch { ... }).
 */
@Singleton
class OwnStorySessionStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    /**
     * Ordered list of published story media URIs, oldest-first.
     * Emits an empty list when no stories have been published.
     */
    val storyUris: Flow<List<String>> = dataStore.data.map { prefs ->
        val raw = prefs[KEY_OWN_STORY_URIS] ?: ""
        if (raw.isBlank()) emptyList()
        else raw.split("\n").filter { it.isNotBlank() }
    }

    /** Append a new story URI to the persisted list. Does not replace existing stories. */
    suspend fun publishStory(uri: String) {
        dataStore.edit { prefs ->
            val existing = prefs[KEY_OWN_STORY_URIS] ?: ""
            prefs[KEY_OWN_STORY_URIS] = if (existing.isBlank()) uri else "$existing\n$uri"
        }
    }

    /** Remove one specific story URI from the persisted list. */
    suspend fun deleteStory(uri: String) {
        dataStore.edit { prefs ->
            val updated = (prefs[KEY_OWN_STORY_URIS] ?: "")
                .split("\n")
                .filter { it.isNotBlank() && it != uri }
                .joinToString("\n")
            if (updated.isBlank()) prefs.remove(KEY_OWN_STORY_URIS)
            else prefs[KEY_OWN_STORY_URIS] = updated
        }
    }

    /** Remove all own story URIs (e.g. on sign-out or full wipe). */
    suspend fun clearAllStories() {
        dataStore.edit { it.remove(KEY_OWN_STORY_URIS) }
    }

    // ── Seen story IDs ───────────────────────────────────────────────────────

    /**
     * In-memory mirror of seen story IDs.
     *
     * Updated synchronously (before the DataStore write) in [markSeenStoryId].
     * Because [OwnStorySessionStore] is a @Singleton, this value is shared across
     * every ViewModel instance within the same process — including new [HomeViewModel]
     * instances created when the back stack produces a fresh NavBackStackEntry (e.g.
     * Profile bottom-nav → Home forward-navigate).
     *
     * This eliminates the DataStore async-read race: a new ViewModel can read the
     * correct seen state immediately via [liveSeenStoryIds], without waiting for
     * DataStore to commit and replay.
     */
    private val _seenIdsLive = MutableStateFlow<Set<String>>(emptySet())

    /**
     * Zero-latency snapshot of seen IDs for the current process session.
     * Starts empty; populated the first time [markSeenStoryId] is called or when
     * [HomeViewModel.init] merges it with the DataStore-persisted set.
     */
    val liveSeenStoryIds: StateFlow<Set<String>> = _seenIdsLive.asStateFlow()

    /**
     * Persisted set of story IDs the user has already viewed.
     * Survives process death and ViewModel recreation (back-nav / bottom-tab re-navigate).
     */
    val seenStoryIds: Flow<Set<String>> = dataStore.data.map { prefs ->
        val raw = prefs[KEY_SEEN_STORY_IDS] ?: ""
        if (raw.isBlank()) emptySet()
        else raw.split(",").filter { it.isNotBlank() }.toSet()
    }

    /**
     * Persist a newly viewed story ID. Idempotent — safe to call multiple times.
     *
     * Updates [liveSeenStoryIds] synchronously first so that any new ViewModel
     * created immediately after this call (same-session ViewModel recreation) already
     * sees the correct seen state without waiting for the DataStore write to complete.
     */
    suspend fun markSeenStoryId(id: String) {
        _seenIdsLive.update { it + id }          // synchronous in-memory update first
        dataStore.edit { prefs ->
            val existing = prefs[KEY_SEEN_STORY_IDS] ?: ""
            val ids = if (existing.isBlank()) setOf(id)
                      else existing.split(",").filter { it.isNotBlank() }.toSet() + id
            prefs[KEY_SEEN_STORY_IDS] = ids.joinToString(",")
        }
    }

    private companion object {
        val KEY_OWN_STORY_URIS  = stringPreferencesKey("own_story_uris")
        val KEY_SEEN_STORY_IDS  = stringPreferencesKey("seen_story_ids")
    }
}
