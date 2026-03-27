package com.photogram.feature.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class StorageDetailViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(StorageDetailUiState())
    val uiState: StateFlow<StorageDetailUiState> = _uiState.asStateFlow()

    fun onAction(action: StorageDetailUiAction) {
        when (action) {
            StorageDetailUiAction.FreeSpaceClicked  -> { /* placeholder */ }
            StorageDetailUiAction.ClearCacheClicked -> { /* placeholder */ }
        }
    }
}
