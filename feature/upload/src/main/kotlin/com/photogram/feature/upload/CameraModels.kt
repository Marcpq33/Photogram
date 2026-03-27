package com.photogram.feature.upload

enum class CameraMode { STORY, GALLERY, ALBUM }

enum class CameraDestination { STORY, GALLERY, ALBUM }

internal fun CameraMode.toDestination(): CameraDestination = when (this) {
    CameraMode.STORY   -> CameraDestination.STORY
    CameraMode.GALLERY -> CameraDestination.GALLERY
    CameraMode.ALBUM   -> CameraDestination.ALBUM
}
