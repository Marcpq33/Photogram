package com.photogram.feature.upload

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture

internal data class CameraUiState(
    val mode: CameraMode = CameraMode.STORY,
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    val flashMode: Int = ImageCapture.FLASH_MODE_OFF,
    val permissionsGranted: Boolean = false,
    val permissionsChecked: Boolean = false,
    val isRecording: Boolean = false,
    val capturedUri: Uri? = null,
    val showStoryPreview: Boolean = false,
    val showDestinationPicker: Boolean = false,
)
