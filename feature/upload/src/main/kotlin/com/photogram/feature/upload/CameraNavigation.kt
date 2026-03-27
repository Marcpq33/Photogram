package com.photogram.feature.upload

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.photogram.core.navigation.PhotogramDestination

fun NavGraphBuilder.cameraScreen(
    onClose: () -> Unit,
    onUploadPlaceholder: (String) -> Unit,
) {
    composable(route = PhotogramDestination.Camera.route) {
        CameraScreen(
            onClose             = onClose,
            onUploadPlaceholder = onUploadPlaceholder,
        )
    }
}
