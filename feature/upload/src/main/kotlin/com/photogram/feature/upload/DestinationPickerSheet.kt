package com.photogram.feature.upload

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Sheet-local palette (mirrors Home palette) ────────────────────────────────
private val SheetTerracotta = Color(0xFFC5663E)
private val SheetBlue       = Color(0xFFC9A96E)
private val SheetGold       = Color(0xFFC9A96E)

// ── Sheet ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DestinationPickerSheet(
    preSelected: CameraDestination,
    onDismiss: () -> Unit,
    onSelect: (CameraDestination) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor   = Color(0xFF0B0B0D),
        dragHandle       = { DestHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text          = "ADD TO",
                color         = Color.White.copy(alpha = 0.30f),
                fontSize      = 10.sp,
                fontWeight    = FontWeight.SemiBold,
                letterSpacing = 3.sp,
                modifier      = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 2.dp),
            )
            DestCard(
                icon        = Icons.Default.AutoAwesome,
                label       = "Story",
                subtitle    = "Share with your group now",
                gradient    = Brush.verticalGradient(listOf(Color(0xFF1E1108), Color(0xFF110B05))),
                accentColor = SheetTerracotta,
                selected    = preSelected == CameraDestination.STORY,
                onClick     = { onSelect(CameraDestination.STORY) },
            )
            DestCard(
                icon        = Icons.Default.PhotoLibrary,
                label       = "Gallery",
                subtitle    = "Save to your photo gallery",
                gradient    = Brush.verticalGradient(listOf(Color(0xFF0C1425), Color(0xFF08101E))),
                accentColor = SheetBlue,
                selected    = preSelected == CameraDestination.GALLERY,
                onClick     = { onSelect(CameraDestination.GALLERY) },
            )
            DestCard(
                icon        = Icons.Default.PhotoAlbum,
                label       = "Album",
                subtitle    = "Add to a shared album",
                gradient    = Brush.verticalGradient(listOf(Color(0xFF181410), Color(0xFF100E0B))),
                accentColor = SheetGold,
                selected    = preSelected == CameraDestination.ALBUM,
                onClick     = { onSelect(CameraDestination.ALBUM) },
            )
        }
    }
}

@Composable
private fun DestHandle() {
    Box(
        modifier         = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 4.dp)
                .clip(RoundedCornerShape(50))
                .background(SheetTerracotta.copy(alpha = 0.45f)),
        )
    }
}

@Composable
private fun DestCard(
    icon: ImageVector,
    label: String,
    subtitle: String,
    gradient: Brush,
    accentColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) accentColor.copy(alpha = 0.55f) else accentColor.copy(alpha = 0.18f),
                shape = RoundedCornerShape(20.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = accentColor,
                    modifier           = Modifier.size(22.dp),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text          = label,
                    color         = Color.White,
                    fontSize      = 16.sp,
                    fontWeight    = FontWeight.SemiBold,
                    letterSpacing = 0.2.sp,
                )
                Text(
                    text       = subtitle,
                    color      = Color.White.copy(alpha = 0.42f),
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}
