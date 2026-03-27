package com.photogram.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoDelete
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.photogram.core.designsystem.LocalLanguageCode
import com.photogram.core.designsystem.PhotogramTheme

// ── Palette ───────────────────────────────────────────────────────────────────
private val SdDarkBg       = Color(0xFF050505)
private val SdCardBg       = Color(0xFF121214)
private val SdTerracotta   = Color(0xFFC5663E)
private val SdTextSec      = Color(0xFF8A8A8E)
private val SdSectionLabel = Color(0xFF6E6E73)
private val SdDivider      = Color(0x14FFFFFF)
private val SdToggleOff    = Color(0xFF252528)

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun StorageDetailScreen(
    onBack: () -> Unit = {},
    viewModel: StorageDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PhotogramTheme(darkTheme = true) {
        StorageDetailContent(
            uiState  = uiState,
            onAction = viewModel::onAction,
            onBack   = onBack,
        )
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun StorageDetailContent(
    uiState: StorageDetailUiState,
    onAction: (StorageDetailUiAction) -> Unit,
    onBack: () -> Unit,
) {
    val strings = StorageDetailStrings.forCode(LocalLanguageCode.current)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SdDarkBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 48.dp, bottom = 40.dp),
        ) {
            SdTopBar(onBack = onBack, strings = strings)
            Spacer(Modifier.height(16.dp))

            // Hero storage card
            StorageHeroCard(uiState = uiState, strings = strings)
            Spacer(Modifier.height(4.dp))

            SdSectionLabel(strings.sectionBreakdown)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SdCardBg),
            ) {
                StorageTypeItem(
                    icon     = Icons.Default.Photo,
                    label    = strings.photos,
                    usedGb   = uiState.photosGb,
                    totalGb  = uiState.totalGb,
                )
                HorizontalDivider(
                    modifier  = Modifier.padding(start = 68.dp),
                    color     = SdDivider,
                    thickness = 0.5.dp,
                )
                StorageTypeItem(
                    icon     = Icons.Default.VideoLibrary,
                    label    = strings.videos,
                    usedGb   = uiState.videosGb,
                    totalGb  = uiState.totalGb,
                )
                HorizontalDivider(
                    modifier  = Modifier.padding(start = 68.dp),
                    color     = SdDivider,
                    thickness = 0.5.dp,
                )
                StorageTypeItem(
                    icon        = Icons.Default.Message,
                    label       = strings.messages,
                    usedGb      = uiState.messagesGb,
                    totalGb     = uiState.totalGb,
                    showDivider = false,
                )
            }

            SdSectionLabel(strings.sectionManagement)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SdCardBg),
            ) {
                SdActionItem(
                    icon     = Icons.Default.AutoDelete,
                    label    = strings.freeSpace,
                    subtitle = strings.freeSpaceSubtitle,
                    onClick  = { onAction(StorageDetailUiAction.FreeSpaceClicked) },
                )
                HorizontalDivider(
                    modifier  = Modifier.padding(start = 68.dp),
                    color     = SdDivider,
                    thickness = 0.5.dp,
                )
                SdActionItem(
                    icon        = Icons.Default.CleaningServices,
                    label       = strings.clearCache,
                    subtitle    = "%.0f MB ${strings.ofTempData}".format(uiState.cacheGb * 1024),
                    showDivider = false,
                    onClick     = { onAction(StorageDetailUiAction.ClearCacheClicked) },
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun SdTopBar(onBack: () -> Unit, strings: StorageDetailStrings) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
    ) {
        IconButton(
            onClick  = onBack,
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Icon(
                imageVector        = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint               = Color.White,
                modifier           = Modifier.size(22.dp),
            )
        }
        Text(
            text  = strings.title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontStyle  = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                fontSize   = 22.sp,
            ),
            color    = Color.White,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

// ── Section label ─────────────────────────────────────────────────────────────

@Composable
private fun SdSectionLabel(text: String) {
    Text(
        text     = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        style    = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.5.sp,
            fontSize      = 11.sp,
        ),
        color = SdSectionLabel,
    )
}

// ── Hero storage card ─────────────────────────────────────────────────────────

@Composable
private fun StorageHeroCard(uiState: StorageDetailUiState, strings: StorageDetailStrings) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(SdCardBg)
            .padding(20.dp),
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 32.sp)) {
                        append("%.1f".format(uiState.usedGb))
                    }
                    withStyle(SpanStyle(color = SdTextSec, fontSize = 16.sp)) {
                        append(" GB")
                    }
                },
            )
            Spacer(Modifier.weight(1f))
            Text(
                text     = "${strings.ofGb} ${uiState.totalGb.toInt()} GB",
                color    = SdTextSec,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(SdToggleOff),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(uiState.usedGb / uiState.totalGb)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(SdTerracotta),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text     = "%.0f%% ${strings.usedPercent}".format(uiState.usedGb / uiState.totalGb * 100),
            color    = SdTextSec,
            fontSize = 12.sp,
        )
    }
}

// ── Storage type item ─────────────────────────────────────────────────────────

@Composable
private fun StorageTypeItem(
    icon: ImageVector,
    label: String,
    usedGb: Float,
    totalGb: Float,
    showDivider: Boolean = true,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SdTerracotta),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text     = label,
                        color    = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text       = "%.1f GB".format(usedGb),
                        color      = SdTextSec,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(SdToggleOff),
                ) {
                    val ratio = (usedGb / totalGb).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(ratio)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(SdTerracotta),
                    )
                }
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier  = Modifier.padding(start = 68.dp),
                color     = SdDivider,
                thickness = 0.5.dp,
            )
        }
    }
}

// ── Action item ───────────────────────────────────────────────────────────────

@Composable
private fun SdActionItem(
    icon: ImageVector,
    label: String,
    subtitle: String,
    showDivider: Boolean = true,
    onClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SdTerracotta),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(text = label, color = Color.White, fontSize = 15.sp)
                Text(text = subtitle, color = SdTextSec, fontSize = 12.sp)
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier  = Modifier.padding(start = 68.dp),
                color     = SdDivider,
                thickness = 0.5.dp,
            )
        }
    }
}
