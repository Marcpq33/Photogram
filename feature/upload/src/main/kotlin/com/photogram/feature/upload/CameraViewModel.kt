package com.photogram.feature.upload

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photogram.core.datastore.OwnStorySessionStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Nav events ────────────────────────────────────────────────────────────────

internal sealed interface CameraNavEvent {
    data object NavigateUp : CameraNavEvent
    data class UploadPlaceholder(val destination: CameraDestination) : CameraNavEvent
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val ownStorySessionStore: OwnStorySessionStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    internal val uiState = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<CameraNavEvent>(extraBufferCapacity = 1)
    internal val navEvent: SharedFlow<CameraNavEvent> = _navEvent.asSharedFlow()

    internal fun onAction(action: CameraUiAction) {
        when (action) {
            CameraUiAction.Close ->
                emit(CameraNavEvent.NavigateUp)

            is CameraUiAction.ModeSelected ->
                _uiState.update { it.copy(mode = action.mode) }

            CameraUiAction.ToggleLens -> _uiState.update {
                it.copy(
                    lensFacing = if (it.lensFacing == CameraSelector.LENS_FACING_BACK)
                        CameraSelector.LENS_FACING_FRONT
                    else
                        CameraSelector.LENS_FACING_BACK,
                )
            }

            CameraUiAction.ToggleFlash -> _uiState.update {
                it.copy(
                    flashMode = when (it.flashMode) {
                        ImageCapture.FLASH_MODE_OFF  -> ImageCapture.FLASH_MODE_ON
                        ImageCapture.FLASH_MODE_ON   -> ImageCapture.FLASH_MODE_AUTO
                        else                         -> ImageCapture.FLASH_MODE_OFF
                    },
                )
            }

            is CameraUiAction.PermissionsResult ->
                _uiState.update { it.copy(permissionsGranted = action.granted, permissionsChecked = true) }

            is CameraUiAction.PhotoCaptured -> {
                if (_uiState.value.mode == CameraMode.STORY) {
                    // Story mode: show preview/confirm step before publishing.
                    _uiState.update { it.copy(capturedUri = action.uri, showStoryPreview = true) }
                } else {
                    _uiState.update { it.copy(capturedUri = action.uri, showDestinationPicker = true) }
                }
            }

            is CameraUiAction.MediaPicked -> {
                if (_uiState.value.mode == CameraMode.STORY) {
                    _uiState.update { it.copy(capturedUri = action.uri, showStoryPreview = true) }
                } else {
                    _uiState.update { it.copy(capturedUri = action.uri, showDestinationPicker = true) }
                }
            }

            CameraUiAction.RecordingStarted ->
                _uiState.update { it.copy(isRecording = true) }

            is CameraUiAction.RecordingStopped -> {
                _uiState.update { it.copy(isRecording = false) }
                if (action.uri != null) {
                    if (_uiState.value.mode == CameraMode.STORY) {
                        _uiState.update { it.copy(capturedUri = action.uri, showStoryPreview = true) }
                    } else {
                        _uiState.update { it.copy(capturedUri = action.uri, showDestinationPicker = true) }
                    }
                }
            }

            CameraUiAction.ConfirmStoryPublish -> {
                // Persist URI to DataStore before navigating back so the viewer and
                // Home can consume it even on subsequent cold starts.
                val uri = _uiState.value.capturedUri
                _uiState.update { it.copy(showStoryPreview = false, capturedUri = null) }
                if (uri != null) {
                    viewModelScope.launch { ownStorySessionStore.publishStory(uri.toString()) }
                }
                emit(CameraNavEvent.UploadPlaceholder(CameraDestination.STORY))
            }

            CameraUiAction.DiscardStoryCapture -> {
                // The captured URI is only in local state — it has never been published to
                // the store — so no store call is needed here.
                _uiState.update { it.copy(showStoryPreview = false, capturedUri = null) }
            }

            CameraUiAction.DestinationPickerDismissed ->
                _uiState.update { it.copy(showDestinationPicker = false, capturedUri = null) }

            is CameraUiAction.DestinationSelected -> {
                // Upload logic wired in the upload milestone — placeholder for now
                _uiState.update { it.copy(showDestinationPicker = false, capturedUri = null) }
                emit(CameraNavEvent.UploadPlaceholder(action.destination))
            }
        }
    }

    private fun emit(event: CameraNavEvent) {
        viewModelScope.launch { _navEvent.emit(event) }
    }
}
