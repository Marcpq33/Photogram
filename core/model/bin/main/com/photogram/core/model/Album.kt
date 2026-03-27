package com.photogram.core.model

data class Album(
    val id: String,
    val name: String,
    val coverUrl: String?,
    val type: AlbumType,
    val createdBy: String,
    val createdAt: Long,
    val memberCount: Int,
    val mediaCount: Int,
    val isPremium: Boolean,
)

enum class AlbumType {
    PERSONAL,
    COUPLE,
    FAMILY,
    EVENT,
}
