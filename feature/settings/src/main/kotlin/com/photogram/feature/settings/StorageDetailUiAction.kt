package com.photogram.feature.settings

sealed interface StorageDetailUiAction {
    data object FreeSpaceClicked  : StorageDetailUiAction
    data object ClearCacheClicked : StorageDetailUiAction
}
