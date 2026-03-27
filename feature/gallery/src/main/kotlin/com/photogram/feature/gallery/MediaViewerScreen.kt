package com.photogram.feature.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.absoluteValue

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun MediaViewerScreen(
    onBack: () -> Unit = {},
    viewModel: MediaViewerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MediaViewerContent(uiState = uiState, onBack = onBack)
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun MediaViewerContent(
    uiState: MediaViewerUiState,
    onBack: () -> Unit,
) {
    // Derive a stable color from the mediaId so each item has a unique tone.
    // Same palette used by GalleryScreen — visual continuity across the tap transition.
    val colorIndex = uiState.mediaId.hashCode().absoluteValue % mockPhotoColors.size
    val photoColor = mockPhotoColors[colorIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505)),
    ) {
        // ── Full-screen framed photo (white frame + warm mat + color fill) ────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 80.dp)
                .background(Color.White),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
                    .background(Color(0xFFF2EBE0)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                        .background(photoColor),
                )
            }
        }

        // ── Back button — top-left, 44dp touch target ─────────────────────────
        Box(
            modifier = Modifier
                .padding(top = 12.dp, start = 8.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint               = Color.White,
                modifier           = Modifier.size(22.dp),
            )
        }

        // ── Media ID label — bottom-left (mock caption until real data is wired)
        Text(
            text       = uiState.mediaId,
            modifier   = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 36.dp),
            color      = Color.White.copy(alpha = 0.55f),
            fontSize   = 12.sp,
            fontWeight = FontWeight.Light,
            fontFamily = FontFamily.Serif,
            fontStyle  = FontStyle.Italic,
        )
    }
}
