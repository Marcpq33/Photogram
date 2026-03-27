package com.photogram.core.model

data class UserProfile(
    val userId: String,
    val displayName: String,
    val avatarUrl: String?,
    val bio: String?,
    val isFollowedByMe: Boolean,
)
