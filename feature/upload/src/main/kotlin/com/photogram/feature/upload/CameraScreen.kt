package com.photogram.feature.upload

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageAndVideo
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.photogram.core.designsystem.LocalLanguageCode
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

// ── Camera-local palette ──────────────────────────────────────────────────────
private val CameraTerracotta = Color(0xFFC9A96E)

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun CameraScreen(
    onClose: () -> Unit,
    onUploadPlaceholder: (String) -> Unit,
    viewModel: CameraViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // One-shot nav events
    LaunchedEffect(viewModel.navEvent) {
        viewModel.navEvent.collect { event ->
            when (event) {
                CameraNavEvent.NavigateUp ->
                    onClose()
                is CameraNavEvent.UploadPlaceholder ->
                    onUploadPlaceholder(event.destination.name.lowercase() + " — upload coming soon")
            }
        }
    }

    // Permission launcher (camera + audio + storage)
    val permLauncher = rememberLauncherForActivityResult(RequestMultiplePermissions()) { results ->
        viewModel.onAction(CameraUiAction.PermissionsResult(results[Manifest.permission.CAMERA] == true))
    }

    // System photo/video picker — no storage permission required on API 33+
    val pickerLauncher = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        uri?.let { viewModel.onAction(CameraUiAction.MediaPicked(it)) }
    }

    // Request permissions once on first composition
    LaunchedEffect(uiState.permissionsChecked) {
        if (!uiState.permissionsChecked) {
            permLauncher.launch(cameraPermissions())
        }
    }

    // CameraX use-case refs — live in the composable, not the ViewModel
    val imageCaptureRef   = remember { mutableStateOf<ImageCapture?>(null) }
    val videoCaptureRef   = remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    val activeRecordingRef = remember { mutableStateOf<Recording?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        // Live preview (only when permissions granted)
        if (uiState.permissionsGranted) {
            CameraPreviewSurface(
                modifier             = Modifier.fillMaxSize(),
                lensFacing           = uiState.lensFacing,
                flashMode            = uiState.flashMode,
                onImageCaptureReady  = { imageCaptureRef.value = it },
                onVideoCaptureReady  = { videoCaptureRef.value = it },
            )
        }

        // Permission denied fallback
        if (uiState.permissionsChecked && !uiState.permissionsGranted) {
            PermissionDeniedOverlay(onClose = { viewModel.onAction(CameraUiAction.Close) })
        }

        // Top controls overlay
        TopBar(
            onClose       = { viewModel.onAction(CameraUiAction.Close) },
            flashMode     = uiState.flashMode,
            onToggleFlash = { viewModel.onAction(CameraUiAction.ToggleFlash) },
            currentMode   = uiState.mode,
        )

        // Bottom controls overlay (capture + mode tabs)
        BottomControls(
            modifier = Modifier.align(Alignment.BottomCenter),
            mode     = uiState.mode,
            isRecording = uiState.isRecording,
            onCapture = {
                val ic = imageCaptureRef.value
                if (ic != null) {
                    capturePhoto(ic, context) { uri ->
                        viewModel.onAction(CameraUiAction.PhotoCaptured(uri))
                    }
                }
            },
            onStartRecording = {
                val vc = videoCaptureRef.value
                if (vc != null) {
                    beginRecording(
                        videoCapture  = vc,
                        context       = context,
                        onRecordingRef = { rec -> activeRecordingRef.value = rec },
                        onStarted     = { viewModel.onAction(CameraUiAction.RecordingStarted) },
                        onFinished    = { uri ->
                            activeRecordingRef.value = null
                            viewModel.onAction(CameraUiAction.RecordingStopped(uri))
                        },
                    )
                }
            },
            onStopRecording = { activeRecordingRef.value?.stop() },
            onFlipLens      = { viewModel.onAction(CameraUiAction.ToggleLens) },
            onOpenPicker    = { pickerLauncher.launch(PickVisualMediaRequest(ImageAndVideo)) },
            onModeSelected  = { viewModel.onAction(CameraUiAction.ModeSelected(it)) },
        )

        // Post-capture destination picker
        if (uiState.showDestinationPicker) {
            DestinationPickerSheet(
                preSelected = uiState.mode.toDestination(),
                onDismiss   = { viewModel.onAction(CameraUiAction.DestinationPickerDismissed) },
                onSelect    = { viewModel.onAction(CameraUiAction.DestinationSelected(it)) },
            )
        }
    }
}

// ── Camera preview surface ─────────────────────────────────────────────────────

@Composable
private fun CameraPreviewSurface(
    modifier: Modifier,
    lensFacing: Int,
    flashMode: Int,
    onImageCaptureReady: (ImageCapture) -> Unit,
    onVideoCaptureReady: (VideoCapture<Recorder>) -> Unit,
) {
    val context       = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView   = remember { PreviewView(context) }
    val providerRef   = remember { mutableStateOf<ProcessCameraProvider?>(null) }

    // Rebind camera whenever lens or flash changes
    LaunchedEffect(lensFacing, flashMode) {
        val provider: ProcessCameraProvider = suspendCancellableCoroutine { cont ->
            val future = ProcessCameraProvider.getInstance(context)
            future.addListener(
                { cont.resume(future.get()) },
                ContextCompat.getMainExecutor(context),
            )
            cont.invokeOnCancellation { future.cancel(false) }
        }
        providerRef.value = provider

        val preview      = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val imageCapture = ImageCapture.Builder().setFlashMode(flashMode).build()
        val videoCapture = VideoCapture.withOutput(
            Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build(),
        )
        val selector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        try {
            provider.unbindAll()
            provider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture, videoCapture)
            onImageCaptureReady(imageCapture)
            onVideoCaptureReady(videoCapture)
        } catch (_: Exception) {
            // Camera unavailable (e.g., no front camera on emulator) — degrade gracefully
        }
    }

    // Release camera when composable leaves composition
    DisposableEffect(Unit) {
        onDispose { providerRef.value?.unbindAll() }
    }

    AndroidView(factory = { previewView }, modifier = modifier)
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun TopBar(
    onClose: () -> Unit,
    flashMode: Int,
    onToggleFlash: () -> Unit,
    currentMode: CameraMode,
) {
    val strings = CameraStrings.forCode(LocalLanguageCode.current)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.65f), Color.Transparent),
                ),
            )
            .statusBarsPadding()
            .padding(horizontal = 4.dp, vertical = 4.dp),
    ) {
        IconButton(
            onClick  = onClose,
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Icon(
                imageVector        = Icons.Default.Close,
                contentDescription = strings.closeCamera,
                tint               = Color.White,
            )
        }

        Text(
            text          = currentMode.name,
            color         = Color.White,
            fontWeight    = FontWeight.SemiBold,
            letterSpacing = 1.5.sp,
            fontSize      = 12.sp,
            modifier      = Modifier.align(Alignment.Center),
        )

        IconButton(
            onClick  = onToggleFlash,
            modifier = Modifier.align(Alignment.CenterEnd),
        ) {
            Icon(
                imageVector = when (flashMode) {
                    ImageCapture.FLASH_MODE_ON   -> Icons.Default.FlashOn
                    ImageCapture.FLASH_MODE_AUTO -> Icons.Default.FlashAuto
                    else                         -> Icons.Default.FlashOff
                },
                contentDescription = strings.toggleFlash,
                tint = when (flashMode) {
                    ImageCapture.FLASH_MODE_OFF -> Color.White.copy(alpha = 0.45f)
                    else                        -> Color(0xFFFFD76E)
                },
            )
        }
    }
}

// ── Bottom controls ───────────────────────────────────────────────────────────

@Composable
private fun BottomControls(
    modifier: Modifier,
    mode: CameraMode,
    isRecording: Boolean,
    onCapture: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onFlipLens: () -> Unit,
    onOpenPicker: () -> Unit,
    onModeSelected: (CameraMode) -> Unit,
) {
    val strings = CameraStrings.forCode(LocalLanguageCode.current)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)),
                ),
            )
            .padding(horizontal = 32.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(28.dp))

        // Capture row: gallery | shutter | flip
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onOpenPicker, modifier = Modifier.size(52.dp)) {
                Icon(
                    imageVector        = Icons.Default.PhotoLibrary,
                    contentDescription = strings.openGallery,
                    tint               = Color.White,
                    modifier           = Modifier.size(28.dp),
                )
            }

            CaptureButton(
                isRecording      = isRecording,
                onCapture        = onCapture,
                onStartRecording = onStartRecording,
                onStopRecording  = onStopRecording,
            )

            IconButton(onClick = onFlipLens, modifier = Modifier.size(52.dp)) {
                Icon(
                    imageVector        = Icons.Default.Cameraswitch,
                    contentDescription = strings.flipCamera,
                    tint               = Color.White,
                    modifier           = Modifier.size(28.dp),
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Mode tabs: STORY | GALLERY | ALBUM
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            CameraMode.entries.forEach { cameraMode ->
                ModeTab(
                    label    = cameraMode.name,
                    selected = cameraMode == mode,
                    onClick  = { onModeSelected(cameraMode) },
                )
            }
        }

        Spacer(Modifier.height(28.dp))
    }
}

// ── Capture button ─────────────────────────────────────────────────────────────

@Composable
private fun CaptureButton(
    isRecording: Boolean,
    onCapture: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(74.dp)
            .clip(CircleShape)
            .border(
                width = if (isRecording) 3.dp else 4.dp,
                color = if (isRecording) Color.Red else Color.White,
                shape = CircleShape,
            )
            .background(
                if (isRecording) Color.Red.copy(alpha = 0.18f)
                else Color.White.copy(alpha = 0.12f),
            )
            .pointerInput(isRecording) {
                detectTapGestures(
                    onTap       = { if (isRecording) onStopRecording() else onCapture() },
                    onLongPress = { if (!isRecording) onStartRecording() },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        if (isRecording) {
            // Stop indicator: red square
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.Red),
            )
        } else {
            // Capture indicator: white circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White),
            )
        }
    }
}

// ── Mode tab ──────────────────────────────────────────────────────────────────

@Composable
private fun ModeTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text      = label,
        modifier  = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) CameraTerracotta.copy(alpha = 0.22f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 7.dp),
        color         = if (selected) CameraTerracotta else Color.White.copy(alpha = 0.55f),
        fontSize      = 11.sp,
        fontWeight    = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        letterSpacing = 1.2.sp,
    )
}

// ── Permission denied overlay ─────────────────────────────────────────────────

@Composable
private fun PermissionDeniedOverlay(onClose: () -> Unit) {
    val strings = CameraStrings.forCode(LocalLanguageCode.current)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector        = Icons.Default.CameraAlt,
                contentDescription = null,
                tint               = Color.White.copy(alpha = 0.35f),
                modifier           = Modifier.size(52.dp),
            )
            Text(
                text       = strings.cameraAccessNeeded,
                color      = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize   = 16.sp,
            )
            Text(
                text      = strings.enableCameraPermission,
                color     = Color.White.copy(alpha = 0.45f),
                fontSize  = 13.sp,
                textAlign = TextAlign.Center,
                modifier  = Modifier.padding(horizontal = 44.dp),
            )
        }

        IconButton(
            onClick  = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(4.dp),
        ) {
            Icon(Icons.Default.Close, contentDescription = strings.closeCamera, tint = Color.White)
        }
    }
}

// ── Capture helpers ───────────────────────────────────────────────────────────

private fun cameraPermissions(): Array<String> = buildList {
    add(Manifest.permission.CAMERA)
    add(Manifest.permission.RECORD_AUDIO)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        add(Manifest.permission.READ_MEDIA_IMAGES)
        add(Manifest.permission.READ_MEDIA_VIDEO)
    } else {
        add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}.toTypedArray()

private fun capturePhoto(
    imageCapture: ImageCapture,
    context: Context,
    onCaptured: (Uri) -> Unit,
) {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "photogram_${System.currentTimeMillis()}")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Photogram")
        }
    }
    imageCapture.takePicture(
        ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues,
        ).build(),
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                output.savedUri?.let(onCaptured)
            }
            override fun onError(e: ImageCaptureException) { /* capture failed — silent */ }
        },
    )
}

private fun beginRecording(
    videoCapture: VideoCapture<Recorder>,
    context: Context,
    onRecordingRef: (Recording) -> Unit,
    onStarted: () -> Unit,
    onFinished: (Uri?) -> Unit,
) {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "photogram_video_${System.currentTimeMillis()}")
        put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Photogram")
        }
    }
    val recording = videoCapture.output
        .prepareRecording(
            context,
            MediaStoreOutputOptions.Builder(
                context.contentResolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            ).setContentValues(contentValues).build(),
        )
        .withAudioEnabled()
        .start(ContextCompat.getMainExecutor(context)) { event ->
            when (event) {
                is VideoRecordEvent.Start    -> onStarted()
                is VideoRecordEvent.Finalize ->
                    onFinished(if (!event.hasError()) event.outputResults.outputUri else null)
                else                         -> Unit
            }
        }
    onRecordingRef(recording)
}
