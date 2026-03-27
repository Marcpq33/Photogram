package com.photogram.core.model

data class PrivacySettings(
    val userId: String,
    val profileVisibility: ProfileVisibility,
    val allowTagging: Boolean,
    val allowDirectMessages: Boolean,
    val activityVisible: Boolean,
)

enum class ProfileVisibility {
    PUBLIC,
    FOLLOWERS_ONLY,
    PRIVATE,
}
