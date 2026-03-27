package com.photogram.feature.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.photogram.core.navigation.PhotogramDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// ── UiState ───────────────────────────────────────────────────────────────────

data class MediaViewerUiState(
    val mediaId: String = "",
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

@HiltViewModel
class MediaViewerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val mediaId: String =
        checkNotNull(savedStateHandle[PhotogramDestination.MediaViewer.ARG_MEDIA_ID])

    private val _uiState = MutableStateFlow(MediaViewerUiState(mediaId = mediaId))
    val uiState: StateFlow<MediaViewerUiState> = _uiState.asStateFlow()
}
