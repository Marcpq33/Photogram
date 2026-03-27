package com.photogram.feature.upload

import android.net.Uri

internal sealed interface CameraUiAction {
    data object Close : CameraUiAction
    data class ModeSelected(val mode: CameraMode) : CameraUiAction
    data object ToggleLens : CameraUiAction
    data object ToggleFlash : CameraUiAction
    data class PermissionsResult(val granted: Boolean) : CameraUiAction
    data class PhotoCaptured(val uri: Uri) : CameraUiAction
    data class MediaPicked(val uri: Uri) : CameraUiAction
    data object RecordingStarted : CameraUiAction
    data class RecordingStopped(val uri: Uri?) : CameraUiAction
    data object DestinationPickerDismissed : CameraUiAction
    data class DestinationSelected(val destination: CameraDestination) : CameraUiAction
}
