package com.photogram.core.model

data class MediaAsset(
    val id: String,
    val albumId: String,
    val uploadedBy: String,
    val url: String,
    val thumbnailUrl: String?,
    val takenAt: Long,
    val uploadedAt: Long,
    val caption: String?,
    val width: Int,
    val height: Int,
    val sizeBytes: Long,
    val isVideo: Boolean,
    val syncState: SyncState,
)

enum class SyncState {
    PENDING_UPLOAD,
    UPLOADING,
    SYNCED,
    FAILED,
}
