package com.photogram.core.model

data class Recap(
    val id: String,
    val albumId: String,
    val title: String,
    val videoUrl: String,
    val thumbnailUrl: String?,
    val month: Int,
    val year: Int,
    val durationSeconds: Int,
    val createdAt: Long,
)
