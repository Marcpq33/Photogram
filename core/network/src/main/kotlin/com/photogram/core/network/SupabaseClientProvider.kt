package com.photogram.core.network

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient

// Assumption: supabase-kt 3.x API. ktor-client-android is on the classpath and
// auto-detected as the HTTP engine by Ktor's ServiceLoader mechanism on Android.
// Only Auth is installed for Milestone 3A — other plugins (Postgrest, Realtime,
// Storage) are added in later milestones as each feature layer is introduced.
internal fun createPhotogramSupabaseClient(
    url: String,
    key: String,
): SupabaseClient = createSupabaseClient(
    supabaseUrl = url,
    supabaseKey = key,
) {
    install(Auth) {
        // Redirect URL registered in the Supabase dashboard and in AndroidManifest.xml:
        //   io.photogram://callback
        // supabase-kt uses scheme + host to match incoming deep-link intents and to build
        // the redirectUrl parameter automatically when generating magic-link emails.
        scheme = "io.photogram"
        host = "callback"
    }
}
