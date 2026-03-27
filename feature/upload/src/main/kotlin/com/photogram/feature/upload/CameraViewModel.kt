package com.photogram.feature.upload

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class CameraViewModel @Inject constructor() : ViewModel() {

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

            is CameraUiAction.PhotoCaptured ->
                _uiState.update { it.copy(capturedUri = action.uri, showDestinationPicker = true) }

            is CameraUiAction.MediaPicked ->
                _uiState.update { it.copy(capturedUri = action.uri, showDestinationPicker = true) }

            CameraUiAction.RecordingStarted ->
                _uiState.update { it.copy(isRecording = true) }

            is CameraUiAction.RecordingStopped -> {
                _uiState.update { it.copy(isRecording = false) }
                if (action.uri != null) {
                    _uiState.update { it.copy(capturedUri = action.uri, showDestinationPicker = true) }
                }
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
