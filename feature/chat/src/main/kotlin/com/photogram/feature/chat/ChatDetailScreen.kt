package com.photogram.feature.chat

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageAndVideo
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.photogram.core.designsystem.LocalLanguageCode
import com.photogram.core.designsystem.PhotogramTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── Local palette ─────────────────────────────────────────────────────────────

private val ChatBg      = Color(0xFF050505)
private val Terracotta  = Color(0xFFC5663E)
private val BubbleOther = Color(0xFF1C1C24)
private val ComposerBg  = Color(0xFF141418)
private val ComposerBtn = Color(0xFF1C1C26)
private val TextPrimary = Color.White
private val TextGray    = Color(0xFF888890)
private val AudioBg     = Color(0xFF191920)

// ── Composable-owned MediaRecorder handle ─────────────────────────────────────

private data class ActiveRecording(
    val recorder: MediaRecorder,
    val fileUri: Uri,
    val startMs: Long,
)

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun ChatDetailScreen(
    onBack: () -> Unit,
    viewModel: ChatDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val activeRecording = remember { mutableStateOf<ActiveRecording?>(null) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val strings = ChatStrings.forCode(LocalLanguageCode.current)

    var audioPermGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED
        )
    }

    val pickerLauncher = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            val mimeType = context.contentResolver.getType(uri) ?: ""
            viewModel.onAction(ChatDetailUiAction.MediaPicked(uri, isVideo = mimeType.startsWith("video/")))
        }
    }

    val audioPermLauncher = rememberLauncherForActivityResult(RequestPermission()) { granted ->
        audioPermGranted = granted
        if (granted) {
            try {
                val rec = startAudioRecording(context)
                activeRecording.value = rec
                viewModel.onAction(ChatDetailUiAction.RecordingStarted)
            } catch (e: Exception) {
                scope.launch {
                    snackbarHostState.showSnackbar("Mic unavailable — ${e.message ?: "check device/emulator settings"}")
                }
            }
        } else {
            scope.launch { snackbarHostState.showSnackbar(strings.micPermissionRequired) }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            activeRecording.value?.let {
                try { it.recorder.stop(); it.recorder.release() } catch (_: Exception) {}
            }
        }
    }

    LaunchedEffect(uiState.isRecordingAudio) {
        if (uiState.isRecordingAudio) {
            elapsedSeconds = 0
            while (true) { delay(1_000L); elapsedSeconds++ }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.navBack.collect { onBack() }
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }

    PhotogramTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ChatBg)
                    .statusBarsPadding()
                    .imePadding(),
            ) {
                DetailTopBar(
                    albumName = uiState.albumName,
                    strings   = strings,
                    onBackTap = { viewModel.onAction(ChatDetailUiAction.BackTapped) },
                )

                LazyColumn(
                    state               = listState,
                    modifier            = Modifier.weight(1f),
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(uiState.messages, key = { it.id }) { msg ->
                        MessageBubble(msg)
                    }
                }

                if (uiState.isRecordingAudio) {
                    RecordingBar(
                        elapsedSeconds = elapsedSeconds,
                        strings        = strings,
                        onStop = {
                            activeRecording.value?.let { rec ->
                                val (uri, durationMs) = stopAudioRecording(rec)
                                activeRecording.value = null
                                viewModel.onAction(ChatDetailUiAction.AudioRecordingFinished(uri, durationMs))
                            }
                        },
                        onCancel = {
                            activeRecording.value?.let { rec ->
                                cancelAudioRecording(rec)
                                activeRecording.value = null
                            }
                            viewModel.onAction(ChatDetailUiAction.RecordingCancelled)
                        },
                    )
                } else {
                    MessageComposer(
                        text     = uiState.inputText,
                        strings  = strings,
                        onChange = { viewModel.onAction(ChatDetailUiAction.InputChanged(it)) },
                        onSend   = { viewModel.onAction(ChatDetailUiAction.SendTapped) },
                        onAttach = { pickerLauncher.launch(PickVisualMediaRequest(ImageAndVideo)) },
                        onMicTap = {
                            if (audioPermGranted) {
                                try {
                                    val rec = startAudioRecording(context)
                                    activeRecording.value = rec
                                    viewModel.onAction(ChatDetailUiAction.RecordingStarted)
                                } catch (e: Exception) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Mic unavailable — ${e.message ?: "check device/emulator settings"}")
                                    }
                                }
                            } else {
                                audioPermLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier  = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 80.dp),
            )
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun DetailTopBar(albumName: String, strings: ChatStrings, onBackTap: () -> Unit) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Back
        IconButton(onClick = onBackTap) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = strings.back,
                tint               = TextPrimary,
            )
        }

        // Italic serif title
        Text(
            text       = albumName,
            modifier   = Modifier
                .weight(1f)
                .padding(start = 2.dp),
            color      = TextPrimary,
            fontSize   = 22.sp,
            fontWeight = FontWeight.Normal,
            fontStyle  = FontStyle.Italic,
            fontFamily = FontFamily.Serif,
        )

        // Avatar circle — warm portrait gradient placeholder
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFA07868), Color(0xFF5A3020))
                    )
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.18f), Color.Transparent)
                        )
                    )
            )
        }

        Spacer(Modifier.width(12.dp))
    }
}

// ── Message dispatcher ────────────────────────────────────────────────────────

@Composable
private fun MessageBubble(message: ChatDetailMessage) {
    if (message.isDateSeparator) {
        DateSeparator(message.dateSeparatorLabel)
        return
    }
    if (message.isMine) {
        OutgoingMessage(message)
    } else {
        IncomingMessage(message)
    }
}

// ── Date separator ────────────────────────────────────────────────────────────

@Composable
private fun DateSeparator(label: String) {
    Box(
        modifier         = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text          = label,
            color         = TextGray,
            fontSize      = 11.sp,
            fontWeight    = FontWeight.Medium,
            letterSpacing = 1.4.sp,
        )
    }
}

// ── Incoming message ──────────────────────────────────────────────────────────

@Composable
private fun IncomingMessage(message: ChatDetailMessage) {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        message.attachment?.let { att ->
            when (att) {
                is MessageAttachment.MockPhoto -> MockPhotoCard()
                is MessageAttachment.Audio     -> AudioBubble(att)
                is MessageAttachment.Image     -> IncomingImageCard(att)
                is MessageAttachment.Video     -> VideoCard(att)
            }
        }

        // Caption / text body (outside the attachment card — plain text)
        if (message.text.isNotEmpty()) {
            if (message.attachment != null) Spacer(Modifier.height(6.dp))
            Text(
                text       = message.text,
                color      = TextPrimary,
                fontSize   = 15.sp,
                lineHeight = 22.sp,
                modifier   = Modifier.widthIn(max = 300.dp),
            )
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text     = message.timestamp,
            color    = TextGray,
            fontSize = 11.sp,
        )
    }
}

// ── Outgoing message ──────────────────────────────────────────────────────────

@Composable
private fun OutgoingMessage(message: ChatDetailMessage) {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = 80.dp, max = 290.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Terracotta)
                .padding(horizontal = 18.dp, vertical = 14.dp),
        ) {
            Text(
                text       = message.text,
                color      = Color.White,
                fontSize   = 15.sp,
                lineHeight = 22.sp,
            )
        }

        Spacer(Modifier.height(5.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text     = message.timestamp,
                color    = TextGray,
                fontSize = 11.sp,
            )
            message.deliveryLabel?.let { label ->
                Text(
                    text     = " · $label",
                    color    = TextGray,
                    fontSize = 11.sp,
                )
            }
        }
    }
}

// ── Mock photo card (mountain gradient placeholder) ───────────────────────────

@Composable
private fun MockPhotoCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    0.00f to Color(0xFF0D0A1C),
                    0.12f to Color(0xFF141228),
                    0.24f to Color(0xFF1E1630),
                    0.36f to Color(0xFF281A38),
                    0.46f to Color(0xFF4A3020),  // warm amber glow — peak highlight
                    0.54f to Color(0xFF3C2418),
                    0.64f to Color(0xFF1C1430),
                    0.76f to Color(0xFF141228),
                    0.88f to Color(0xFF0E0C20),
                    1.00f to Color(0xFF08081A),
                )
            ),
    ) {
        // RAW badge — bottom-left overlay
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black.copy(alpha = 0.62f))
                .padding(horizontal = 6.dp, vertical = 3.dp),
        ) {
            Text(
                text          = "RAW",
                color         = Color.White,
                fontSize      = 10.sp,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 0.8.sp,
            )
        }
    }
}

// ── Incoming image card (real URI) ────────────────────────────────────────────

@Composable
private fun IncomingImageCard(attachment: MessageAttachment.Image) {
    AsyncImage(
        model              = attachment.uri,
        contentDescription = null,
        contentScale       = ContentScale.Crop,
        modifier           = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp)),
    )
}

// ── Video card ────────────────────────────────────────────────────────────────

@Composable
private fun VideoCard(attachment: MessageAttachment.Video) {
    val strings = ChatStrings.forCode(LocalLanguageCode.current)
    Box(
        modifier         = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A2030)),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model              = attachment.uri,
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize(),
        )
        Box(
            modifier         = Modifier
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.55f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Filled.PlayArrow,
                contentDescription = strings.playVideo,
                tint               = Color.White,
                modifier           = Modifier.size(30.dp),
            )
        }
    }
}

// ── Audio bubble ──────────────────────────────────────────────────────────────

@Composable
private fun AudioBubble(attachment: MessageAttachment.Audio) {
    Row(
        modifier              = Modifier
            .widthIn(min = 200.dp, max = 260.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(AudioBg)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Play circle
        Box(
            modifier         = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF2A2A34)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint               = TextPrimary,
                modifier           = Modifier.size(22.dp),
            )
        }

        // Waveform
        AudioWaveform(modifier = Modifier.weight(1f))

        // Duration
        Text(
            text       = formatDurationMs(attachment.durationMs),
            color      = TextGray,
            fontSize   = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun AudioWaveform(modifier: Modifier = Modifier) {
    val heights = listOf(
        0.30f, 0.55f, 0.80f, 0.50f, 1.00f, 0.65f, 0.90f,
        0.45f, 0.75f, 0.40f, 0.70f, 0.55f, 0.35f, 0.85f, 0.50f,
    )
    Row(
        modifier              = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        heights.forEach { h ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height((5f + 18f * h).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Terracotta.copy(alpha = 0.75f)),
            )
        }
    }
}

// ── Composer ──────────────────────────────────────────────────────────────────

@Composable
private fun MessageComposer(
    text: String,
    strings: ChatStrings,
    onChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttach: () -> Unit,
    onMicTap: () -> Unit,
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // + circle button
        Box(
            modifier         = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(ComposerBtn)
                .clickable(onClick = onAttach),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Default.Add,
                contentDescription = strings.attachMedia,
                tint               = TextGray,
                modifier           = Modifier.size(20.dp),
            )
        }

        // Text input field
        BasicTextField(
            value         = text,
            onValueChange = onChange,
            modifier      = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(26.dp))
                .background(ComposerBg)
                .padding(horizontal = 18.dp, vertical = 13.dp),
            textStyle     = TextStyle(color = TextPrimary, fontSize = 15.sp),
            cursorBrush   = SolidColor(Terracotta),
            singleLine    = true,
            decorationBox = { inner ->
                Box {
                    if (text.isEmpty()) {
                        Text(
                            text  = strings.messagePlaceholder,
                            color = TextGray.copy(alpha = 0.70f),
                            fontSize = 15.sp,
                        )
                    }
                    inner()
                }
            },
        )

        // Mic icon
        IconButton(
            onClick  = onMicTap,
            modifier = Modifier.size(36.dp),
        ) {
            Icon(
                imageVector        = Icons.Default.Mic,
                contentDescription = strings.recordAudio,
                tint               = TextGray,
                modifier           = Modifier.size(22.dp),
            )
        }

        // Send — large blue circle (always visible)
        Box(
            modifier         = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFC9A96E))
                .clickable(onClick = onSend),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = strings.send,
                tint               = Color.White,
                modifier           = Modifier.size(22.dp),
            )
        }
    }
}

// ── Recording bar ─────────────────────────────────────────────────────────────

@Composable
private fun RecordingBar(
    elapsedSeconds: Int,
    strings: ChatStrings,
    onStop: () -> Unit,
    onCancel: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rec")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 0.2f,
        animationSpec = infiniteRepeatable(
            animation  = tween(500),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dot",
    )

    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onCancel) {
            Icon(Icons.Default.Close, contentDescription = strings.cancelRecording, tint = TextGray)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.Red.copy(alpha = dotAlpha), CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text     = "${strings.recording}  ${formatElapsed(elapsedSeconds)}",
                color    = TextPrimary,
                fontSize = 15.sp,
            )
        }

        IconButton(onClick = onStop) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Terracotta, RoundedCornerShape(4.dp)),
            )
        }
    }
}

// ── MediaRecorder helpers ─────────────────────────────────────────────────────

private fun startAudioRecording(context: android.content.Context): ActiveRecording {
    val file = java.io.File(context.cacheDir, "chat_audio_${System.currentTimeMillis()}.m4a")
    val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        @Suppress("DEPRECATION")
        MediaRecorder()
    }
    recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
    recorder.setOutputFile(file.absolutePath)
    recorder.prepare()
    recorder.start()
    return ActiveRecording(recorder, Uri.fromFile(file), System.currentTimeMillis())
}

private fun stopAudioRecording(active: ActiveRecording): Pair<Uri, Long> {
    val durationMs = System.currentTimeMillis() - active.startMs
    try { active.recorder.stop(); active.recorder.release() } catch (_: Exception) {}
    return Pair(active.fileUri, durationMs)
}

private fun cancelAudioRecording(active: ActiveRecording) {
    try { active.recorder.stop(); active.recorder.release() } catch (_: Exception) {}
    try { active.fileUri.path?.let { java.io.File(it).delete() } } catch (_: Exception) {}
}

// ── Formatting helpers ────────────────────────────────────────────────────────

private fun formatElapsed(seconds: Int): String =
    "%d:%02d".format(seconds / 60, seconds % 60)

private fun formatDurationMs(ms: Long): String {
    val totalSec = (ms / 1000).toInt().coerceAtLeast(1)
    return "%d:%02d".format(totalSec / 60, totalSec % 60)
}
