package com.photogram.feature.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageAndVideo
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

// ── Palette (scoped to this file) ─────────────────────────────────────────────
private val NpBg     = Color(0xFF050505)
private val NpCard   = Color(0xFF121214)
private val NpField  = Color(0xFF121214)
private val NpTerra  = Color(0xFFC9A96E)
private val NpMuted  = Color(0x66FFFFFF)
private val NpChipBg = Color(0xFF0F1014)

private val GridColors = listOf(
    Color(0xFF1A1D26), Color(0xFF2A2520), Color(0xFF1E2A20),
    Color(0xFF251E1E), Color(0xFF1E1E2A), Color(0xFF2A251E),
)

private val AlbumFilters = listOf("RECENTS", "FAVORITES", "TRAVEL", "SUMMER", "FAMILY")

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
internal fun NewPostContent(
    mediaUri: Uri?,
    onClose: () -> Unit,
    onCameraClicked: () -> Unit,
    onMediaSelected: (Uri) -> Unit,
) {
    var caption by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("RECENTS") }

    // System photo+video picker (no permission needed on API 33+; system handles it on API 32-)
    val pickerLauncher = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        uri?.let { onMediaSelected(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NpBg)
            .statusBarsPadding()
            // imePadding() pushes content up when the soft keyboard opens;
            // placed before verticalScroll so the scroll adapts to remaining height
            .imePadding()
            .verticalScroll(rememberScrollState()),
    ) {

        // ── Top bar: back arrow + "New Post" title ────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint               = Color.White,
                    modifier           = Modifier.size(22.dp),
                )
            }
            Text(
                text  = "New Post",
                style = TextStyle(
                    fontFamily  = FontFamily.Serif,
                    fontStyle   = FontStyle.Italic,
                    fontSize    = 24.sp,
                    fontWeight  = FontWeight.Normal,
                    color       = Color.White,
                    textAlign   = TextAlign.Center,
                ),
            )
        }

        // ── Media preview card ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(240.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(NpCard),
            contentAlignment = Alignment.Center,
        ) {
            if (mediaUri != null) {
                // Real: Coil loads the URI selected from the gallery picker
                AsyncImage(
                    model              = mediaUri,
                    contentDescription = "Selected media",
                    modifier           = Modifier.fillMaxSize(),
                    contentScale       = ContentScale.Crop,
                )
                // Subtle dim overlay so the card still looks composed
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.12f)),
                )
            } else {
                // Placeholder: gradient background + upload progress ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF3A3A3E), NpCard),
                                radius = 700f,
                            ),
                        ),
                )
                Canvas(modifier = Modifier.size(100.dp)) {
                    val sw    = 6.dp.toPx()
                    val inset = sw / 2f
                    val arc   = Size(size.width - sw, size.height - sw)
                    // Track
                    drawArc(
                        color      = Color.White.copy(alpha = 0.15f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter  = false,
                        topLeft    = Offset(inset, inset),
                        size       = arc,
                        style      = Stroke(width = sw, cap = StrokeCap.Round),
                    )
                    // Progress (67% placeholder)
                    drawArc(
                        color      = NpTerra,
                        startAngle = -90f,
                        sweepAngle = 360f * 0.67f,
                        useCenter  = false,
                        topLeft    = Offset(inset, inset),
                        size       = arc,
                        style      = Stroke(width = sw, cap = StrokeCap.Round),
                    )
                }
                Text(
                    text  = "67%",
                    style = TextStyle(
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White,
                    ),
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Drag handle
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 36.dp, height = 3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0x44FFFFFF)),
        )

        Spacer(Modifier.height(20.dp))

        // ── Action buttons + caption ──────────────────────────────────────────
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // "TAKE PHOTO NOW" — navigates to the validated CameraScreen
            // (permissions + CameraX are already handled there, same as the Home flow)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(50))
                    .background(NpTerra)
                    .clickable { onCameraClicked() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text  = "TAKE PHOTO NOW",
                    style = TextStyle(
                        fontSize      = 13.sp,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color         = Color.White,
                    ),
                )
            }

            // "CHOOSE FROM GALLERY" — system PickVisualMedia, no runtime permission needed
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(50))
                    .clickable {
                        pickerLauncher.launch(PickVisualMediaRequest(ImageAndVideo))
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text  = "CHOOSE FROM GALLERY",
                    style = TextStyle(
                        fontSize      = 13.sp,
                        fontWeight    = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp,
                        color         = Color.White,
                    ),
                )
            }

            // Caption field
            // windowSoftInputMode is handled by imePadding() + verticalScroll above;
            // no additional IME config needed at the composable level.
            BasicTextField(
                value         = caption,
                onValueChange = { caption = it },
                modifier      = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NpField)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                textStyle     = TextStyle(color = Color.White, fontSize = 14.sp),
                decorationBox = { innerTextField ->
                    if (caption.isEmpty()) {
                        Text(
                            text  = "Write a caption...",
                            style = TextStyle(color = NpMuted, fontSize = 14.sp),
                        )
                    }
                    innerTextField()
                },
            )
        }

        Spacer(Modifier.height(16.dp))

        // ── Album filter chips ────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AlbumFilters.forEach { filter ->
                val active = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (active) NpTerra else NpChipBg)
                        .then(
                            if (!active) Modifier.border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(50))
                            else Modifier,
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text  = filter,
                        style = TextStyle(
                            fontSize      = 11.sp,
                            fontWeight    = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color         = Color.White,
                        ),
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Photo grid (placeholder — wired to real picker in gallery milestone) ─
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
        ) {
            GridColors.chunked(3).forEach { rowColors ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    rowColors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(color),
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
            }
        }

        // Bottom breathing room — navigation bar + extra spacing
        Spacer(
            modifier = Modifier
                .navigationBarsPadding()
                .height(16.dp),
        )
    }
}
