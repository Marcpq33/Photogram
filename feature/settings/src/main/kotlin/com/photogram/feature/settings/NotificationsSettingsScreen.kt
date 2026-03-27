package com.photogram.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NotificationsActive
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.photogram.core.designsystem.LocalLanguageCode
import com.photogram.core.designsystem.PhotogramTheme

// ── Palette ───────────────────────────────────────────────────────────────────
private val NsDarkBg       = Color(0xFF050505)
private val NsCardBg       = Color(0xFF121214)
private val NsTerracotta   = Color(0xFFC5663E)
private val NsTextSec      = Color(0xFF8A8A8E)
private val NsSectionLabel = Color(0xFF6E6E73)
private val NsDivider      = Color(0x14FFFFFF)
private val NsToggleOff    = Color(0xFF252528)

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun NotificationsSettingsScreen(
    onBack: () -> Unit = {},
    viewModel: NotificationsSettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PhotogramTheme(darkTheme = true) {
        NotificationsSettingsContent(
            uiState  = uiState,
            onAction = viewModel::onAction,
            onBack   = onBack,
        )
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun NotificationsSettingsContent(
    uiState: NotificationsSettingsUiState,
    onAction: (NotificationsSettingsUiAction) -> Unit,
    onBack: () -> Unit,
) {
    val strings = NotificationsSettingsStrings.forCode(LocalLanguageCode.current)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NsDarkBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 48.dp, bottom = 32.dp),
        ) {
            NsTopBar(onBack = onBack, strings = strings)
            Spacer(Modifier.height(8.dp))

            NsSectionLabel(strings.sectionGeneral)
            NsGroup {
                NsToggleItem(
                    icon     = Icons.Default.CameraAlt,
                    label    = strings.newPhotos,
                    subtitle = strings.newPhotosSubtitle,
                    checked  = uiState.newPhotos,
                    onToggle = { onAction(NotificationsSettingsUiAction.NewPhotosToggled(it)) },
                )
                NsToggleItem(
                    icon     = Icons.Default.Favorite,
                    label    = strings.reactions,
                    subtitle = strings.reactionsSubtitle,
                    checked  = uiState.reactions,
                    onToggle = { onAction(NotificationsSettingsUiAction.ReactionsToggled(it)) },
                )
                NsToggleItem(
                    icon        = Icons.Default.Message,
                    label       = strings.messages,
                    subtitle    = strings.messagesSubtitle,
                    checked     = uiState.messages,
                    showDivider = false,
                    onToggle    = { onAction(NotificationsSettingsUiAction.MessagesToggled(it)) },
                )
            }

            NsSectionLabel(strings.sectionAlbums)
            NsGroup {
                NsToggleItem(
                    icon     = Icons.Default.GroupAdd,
                    label    = strings.invitations,
                    subtitle = strings.invitationsSubtitle,
                    checked  = uiState.albumInvites,
                    onToggle = { onAction(NotificationsSettingsUiAction.AlbumInvitesToggled(it)) },
                )
                NsToggleItem(
                    icon        = Icons.Default.NotificationsActive,
                    label       = strings.updates,
                    subtitle    = strings.updatesSubtitle,
                    checked     = uiState.albumUpdates,
                    showDivider = false,
                    onToggle    = { onAction(NotificationsSettingsUiAction.AlbumUpdatesToggled(it)) },
                )
            }

            NsSectionLabel(strings.sectionEvents)
            NsGroup {
                NsToggleItem(
                    icon        = Icons.Default.Event,
                    label       = strings.reminders,
                    subtitle    = strings.remindersSubtitle,
                    checked     = uiState.eventReminders,
                    showDivider = false,
                    onToggle    = { onAction(NotificationsSettingsUiAction.EventRemindersToggled(it)) },
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun NsTopBar(onBack: () -> Unit, strings: NotificationsSettingsStrings) {
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
private fun NsSectionLabel(text: String) {
    Text(
        text     = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        style    = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.5.sp,
            fontSize      = 11.sp,
        ),
        color = NsSectionLabel,
    )
}

// ── Group card ────────────────────────────────────────────────────────────────

@Composable
private fun NsGroup(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(NsCardBg),
    ) {
        content()
    }
}

// ── Toggle item ───────────────────────────────────────────────────────────────

@Composable
private fun NsToggleItem(
    icon: ImageVector,
    label: String,
    subtitle: String,
    checked: Boolean,
    showDivider: Boolean = true,
    onToggle: (Boolean) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle(!checked) }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NsTerracotta),
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
                Text(text = label, color = Color.White, fontSize = 16.sp)
                Text(text = subtitle, color = NsTextSec, fontSize = 12.sp)
            }
            Spacer(Modifier.width(8.dp))
            NsSwitch(checked = checked, onToggle = onToggle)
        }
        if (showDivider) {
            HorizontalDivider(
                modifier  = Modifier.padding(start = 68.dp),
                color     = NsDivider,
                thickness = 0.5.dp,
            )
        }
    }
}

// ── Thumb switch ──────────────────────────────────────────────────────────────

@Composable
private fun NsSwitch(checked: Boolean, onToggle: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 50.dp, height = 28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (checked) NsTerracotta else NsToggleOff)
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
