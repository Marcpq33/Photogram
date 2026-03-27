package com.photogram.core.datastore

data class UserData(
    val userId: String?,
    val isOnboardingComplete: Boolean,
    val language: String = "EN",
    val isProtoMode: Boolean = false,
) {
    /**
     * True when proto/demo mode is explicitly active (set via dev bypass button).
     * Independent of userId so that Supabase session events cannot accidentally
     * flip it off while the prototype is being used.
     */
    val isDemoMode: Boolean get() = isProtoMode
}
