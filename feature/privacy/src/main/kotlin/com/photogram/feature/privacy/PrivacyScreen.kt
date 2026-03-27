package com.photogram.feature.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.photogram.core.designsystem.LocalLanguageCode
import com.photogram.core.designsystem.PhotogramTheme

// ── Palette ───────────────────────────────────────────────────────────────────
private val PrivacyDarkBg     = Color(0xFF050505)
private val CardBg            = Color(0xFF121214)
private val Terracotta        = Color(0xFFC5663E)
private val TextSecondary     = Color(0xFF8A8A8E)
private val SectionLabelColor = Color(0xFF6E6E73)
private val DividerColor      = Color(0x14FFFFFF)
private val ToggleOffBg       = Color(0xFF252528)
private val StatusGradStart   = Color(0xFF2A1A0C)
private val StatusGradEnd     = Color(0xFF0A0805)
private val WarnBg            = Color(0xFF1A150A)
private val WarnText          = Color(0xFFD4A017)
private val StepCircleBg      = Color(0xFF2A1508)

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun PrivacyScreen(
    onBack: () -> Unit = {},
    viewModel: PrivacyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PhotogramTheme(darkTheme = true) {
        PrivacyContent(uiState = uiState, onAction = viewModel::onAction, onBack = onBack)
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun PrivacyContent(
    uiState: PrivacyUiState,
    onAction: (PrivacyUiAction) -> Unit,
    onBack: () -> Unit,
) {
    val strings = PrivacyStrings.forCode(LocalLanguageCode.current)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrivacyDarkBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 48.dp, bottom = 88.dp),
        ) {
            PrivacyTopBar(onBack = onBack, strings = strings)
            Spacer(Modifier.height(16.dp))

            StatusCard(uiState = uiState, strings = strings)
            Spacer(Modifier.height(24.dp))

            HierarchyStepper(strings = strings)
            Spacer(Modifier.height(4.dp))

            SectionLabel(strings.sectionProfileMode)
            ProfileModeCard(
                selected    = uiState.profileMode == ProfileMode.SOLO_YO,
                title       = strings.soloTitle,
                description = strings.soloDesc,
                showBadge   = true,
                strings     = strings,
                onClick     = { onAction(PrivacyUiAction.ProfileModeSelected(ProfileMode.SOLO_YO)) },
            )
            Spacer(Modifier.height(8.dp))
            ProfileModeCard(
                selected    = uiState.profileMode == ProfileMode.MIEMBROS,
                title       = strings.membersTitle,
                description = strings.membersDesc,
                showBadge   = false,
                strings     = strings,
                onClick     = { onAction(PrivacyUiAction.ProfileModeSelected(ProfileMode.MIEMBROS)) },
            )
            Spacer(Modifier.height(8.dp))

            WarningBanner(strings = strings)

            SectionLabel(strings.sectionWhatCanSee)
            WhatCanSeeGroup(uiState = uiState, onAction = onAction, strings = strings)

            SectionLabel(strings.sectionAlbums)
            AlbumsGroup(uiState = uiState, onAction = onAction, strings = strings)

            SectionLabel(strings.sectionAlbumSummary)
            AlbumsSummaryCard(uiState = uiState, strings = strings)
            Spacer(Modifier.height(8.dp))
            GestionarLink(strings = strings)
            Spacer(Modifier.height(16.dp))
        }

        SaveButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            strings  = strings,
            onClick  = { onAction(PrivacyUiAction.SaveChangesClicked) },
        )
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun PrivacyTopBar(onBack: () -> Unit, strings: PrivacyStrings) {
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

// ── Status card ───────────────────────────────────────────────────────────────

@Composable
private fun StatusCard(uiState: PrivacyUiState, strings: PrivacyStrings) {
    val modeLabel = when (uiState.profileMode) {
        ProfileMode.SOLO_YO  -> strings.onlyYouMode
        ProfileMode.MIEMBROS -> strings.membersMode
    }
    val photosFormatted = if (uiState.totalPhotos >= 1000) {
        val k = uiState.totalPhotos / 1000
        val r = uiState.totalPhotos % 1000
        "$k,${r.toString().padStart(3, '0')}"
    } else {
        "${uiState.totalPhotos}"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(StatusGradStart, StatusGradEnd),
                ),
            )
            .padding(20.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Default.Lock,
                    contentDescription = null,
                    tint               = Terracotta,
                    modifier           = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text      = strings.currentStatus,
                    color     = Terracotta,
                    fontSize  = 11.sp,
                    fontWeight = FontWeight.Bold,
                    style     = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                    ),
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text       = strings.privateProfile,
                color      = Color.White,
                fontSize   = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontStyle  = FontStyle.Italic,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(text = "${uiState.publicAlbums} ${strings.publicAlbumsSuffix}")
                StatusChip(text = "$photosFormatted ${strings.privatePhotosSuffix}")
                StatusChip(text = modeLabel)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text     = strings.levelDescription,
                color    = Color.White.copy(alpha = 0.55f),
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun StatusChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .border(0.5.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Text(
            text     = text,
            color    = Color.White.copy(alpha = 0.75f),
            fontSize = 11.sp,
        )
    }
}

// ── Hierarchy stepper ─────────────────────────────────────────────────────────

private data class StepItem(val icon: ImageVector, val label: String)

@Composable
private fun HierarchyStepper(strings: PrivacyStrings) {
    val steps = listOf(
        StepItem(Icons.Default.Person,      strings.stepProfile),
        StepItem(Icons.Default.Collections, strings.stepAlbums),
        StepItem(Icons.Default.Photo,       strings.stepPhotos),
    )

    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            steps.forEachIndexed { index, step ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(StepCircleBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector        = step.icon,
                        contentDescription = step.label,
                        tint               = Terracotta,
                        modifier           = Modifier.size(22.dp),
                    )
                }
                if (index < steps.lastIndex) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(DividerColor),
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            steps.forEachIndexed { index, step ->
                Text(
                    text      = step.label,
                    color     = Color.White,
                    fontSize  = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = when (index) {
                        0               -> TextAlign.Start
                        steps.lastIndex -> TextAlign.End
                        else            -> TextAlign.Center
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text      = strings.hierarchyHint,
            color     = TextSecondary,
            fontSize  = 12.sp,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Section label ─────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text     = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        style    = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.5.sp,
            fontSize      = 11.sp,
        ),
        color = SectionLabelColor,
    )
}

// ── Profile mode cards ────────────────────────────────────────────────────────

@Composable
private fun ProfileModeCard(
    selected: Boolean,
    title: String,
    description: String,
    showBadge: Boolean,
    strings: PrivacyStrings,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) Terracotta else Color.Transparent
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(CardBg)
            .border(1.5.dp, borderColor, RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        RadioDot(selected = selected)
        Spacer(Modifier.width(12.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text       = title,
                    color      = Color.White,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                if (showBadge) {
                    Spacer(Modifier.width(8.dp))
                    DefaultBadge(strings = strings)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text     = description,
                color    = TextSecondary,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun RadioDot(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .border(1.5.dp, if (selected) Terracotta else TextSecondary, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Terracotta),
            )
        }
    }
}

@Composable
private fun DefaultBadge(strings: PrivacyStrings) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Terracotta)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text      = strings.defaultBadge,
            color     = Color.White,
            fontSize  = 9.sp,
            fontWeight = FontWeight.Bold,
            style     = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
        )
    }
}

// ── Warning banner ────────────────────────────────────────────────────────────

@Composable
private fun WarningBanner(strings: PrivacyStrings) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(WarnBg)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector        = Icons.Default.Warning,
            contentDescription = null,
            tint               = WarnText,
            modifier           = Modifier.size(15.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text     = strings.warningText,
            color    = WarnText,
            fontSize = 12.sp,
        )
    }
}

// ── QUÉ PUEDEN VER group ──────────────────────────────────────────────────────

@Composable
private fun WhatCanSeeGroup(
    uiState: PrivacyUiState,
    onAction: (PrivacyUiAction) -> Unit,
    strings: PrivacyStrings,
) {
    PrivacyGroup {
        // Static row — always visible
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PrivacyIconBox(icon = Icons.Default.Person)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = strings.photoAndName, color = Color.White, fontSize = 16.sp)
                Text(
                    text     = strings.alwaysVisibleFor,
                    color    = TextSecondary,
                    fontSize = 12.sp,
                )
            }
            Text(
                text      = strings.always,
                color     = TextSecondary,
                fontSize  = 11.sp,
                fontWeight = FontWeight.SemiBold,
                style     = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
            )
        }
        ItemDivider()

        // Public photos toggle
        PrivacyToggleRow(
            icon        = Icons.Default.Photo,
            label       = strings.publicPhotos,
            checked     = uiState.showPublicPhotos,
            showDivider = true,
            onToggle    = { onAction(PrivacyUiAction.ShowPublicPhotosToggled(it)) },
        )

        // Stats toggle
        PrivacyToggleRow(
            icon        = Icons.Default.BarChart,
            label       = strings.stats,
            checked     = uiState.showStats,
            showDivider = false,
            onToggle    = { onAction(PrivacyUiAction.ShowStatsToggled(it)) },
        )
    }
}

// ── ÁLBUMES group ─────────────────────────────────────────────────────────────

@Composable
private fun AlbumsGroup(
    uiState: PrivacyUiState,
    onAction: (PrivacyUiAction) -> Unit,
    strings: PrivacyStrings,
) {
    PrivacyGroup {
        // Who can invite — nav row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PrivacyIconBox(icon = Icons.Default.GroupAdd)
            Spacer(Modifier.width(12.dp))
            Text(
                text     = strings.whoCanInvite,
                color    = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text     = uiState.whoCanInvite,
                    color    = TextSecondary,
                    fontSize = 13.sp,
                )
                Spacer(Modifier.width(2.dp))
                Icon(
                    imageVector        = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint               = TextSecondary,
                    modifier           = Modifier.size(16.dp),
                )
            }
        }
        ItemDivider()

        // Approve before joining — toggle with subtitle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAction(PrivacyUiAction.RequireApprovalToggled(!uiState.requireApproval)) }
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PrivacyIconBox(icon = Icons.Default.HowToReg)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = strings.approveBeforeJoining, color = Color.White, fontSize = 16.sp)
                Text(
                    text     = strings.approveDesc,
                    color    = TextSecondary,
                    fontSize = 12.sp,
                )
            }
            Spacer(Modifier.width(8.dp))
            PrivacySwitch(
                checked  = uiState.requireApproval,
                onToggle = { onAction(PrivacyUiAction.RequireApprovalToggled(it)) },
            )
        }
    }
}

// ── Albums summary card ───────────────────────────────────────────────────────

@Composable
private fun AlbumsSummaryCard(uiState: PrivacyUiState, strings: PrivacyStrings) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(CardBg)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AlbumBulletRow(
            label  = strings.albumPrivate,
            count  = uiState.albumsPrivate,
            unit   = strings.albumUnit,
            plural = strings.albumUnitPlural,
            active = true,
        )
        AlbumBulletRow(
            label  = strings.albumWithLink,
            count  = uiState.albumsWithLink,
            unit   = strings.albumUnit,
            plural = strings.albumUnitPlural,
            active = true,
        )
        AlbumBulletRow(
            label  = strings.albumPublic,
            count  = uiState.albumsPublic,
            unit   = strings.albumUnit,
            plural = strings.albumUnitPlural,
            active = false,
        )
    }
}

@Composable
private fun AlbumBulletRow(label: String, count: Int, unit: String, plural: String, active: Boolean) {
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (active) Terracotta else ToggleOffBg),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text     = label,
            color    = Color.White,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f),
        )
        val countLabel = if (count == 1) "1 $unit" else "$count $plural"
        Text(
            text       = countLabel,
            color      = Color.White,
            fontSize   = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// ── Gestionar link ────────────────────────────────────────────────────────────

@Composable
private fun GestionarLink(strings: PrivacyStrings) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text      = strings.managePrivacy,
            color     = Terracotta,
            fontSize  = 14.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.width(2.dp))
        Icon(
            imageVector        = Icons.Default.ChevronRight,
            contentDescription = null,
            tint               = Terracotta,
            modifier           = Modifier.size(16.dp),
        )
    }
}

// ── Shared helpers ────────────────────────────────────────────────────────────

@Composable
private fun PrivacyGroup(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(CardBg),
    ) {
        content()
    }
}

@Composable
private fun PrivacyIconBox(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Terracotta),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = Color.White,
            modifier           = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun PrivacyToggleRow(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    showDivider: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle(!checked) }
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PrivacyIconBox(icon = icon)
            Spacer(Modifier.width(12.dp))
            Text(
                text     = label,
                color    = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
            PrivacySwitch(checked = checked, onToggle = onToggle)
        }
        if (showDivider) ItemDivider()
    }
}

@Composable
private fun ItemDivider() {
    HorizontalDivider(
        modifier  = Modifier.padding(start = 68.dp),
        color     = DividerColor,
        thickness = 0.5.dp,
    )
}

// ── Custom thumb switch ───────────────────────────────────────────────────────

@Composable
private fun PrivacySwitch(checked: Boolean, onToggle: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 50.dp, height = 28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (checked) Terracotta else ToggleOffBg)
            .clickable { onToggle(!checked) },
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 3.dp)
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
}

// ── Save button ───────────────────────────────────────────────────────────────

@Composable
private fun SaveButton(modifier: Modifier, strings: PrivacyStrings, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(PrivacyDarkBg)
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Terracotta)
                .clickable(onClick = onClick)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text      = strings.saveChanges,
                color     = Color.White,
                fontSize  = 15.sp,
                fontWeight = FontWeight.Bold,
                style     = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.5.sp),
            )
        }
    }
}
