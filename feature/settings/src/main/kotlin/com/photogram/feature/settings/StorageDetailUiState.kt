package com.photogram.feature.settings

data class StorageDetailUiState(
    val usedGb: Float = 2.4f,
    val totalGb: Float = 5f,
    val photosGb: Float = 1.8f,
    val videosGb: Float = 0.5f,
    val messagesGb: Float = 0.1f,
    val cacheGb: Float = 0.05f,
)
