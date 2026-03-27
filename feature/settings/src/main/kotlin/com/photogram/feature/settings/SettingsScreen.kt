package com.photogram.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.photogram.core.designsystem.PhotogramTheme

// ── Palette ───────────────────────────────────────────────────────────────────
private val SettingsDarkBg    = Color(0xFF050505)
private val CardBg            = Color(0xFF121214)
private val Terracotta        = Color(0xFFC9A96E)
private val AvatarBg          = Color(0xFF3A2518)
private val TextSecondary     = Color(0xFF8A8A8E)
private val SectionLabelColor = Color(0xFF6E6E73)
private val DividerColor      = Color(0x14FFFFFF)
private val ToggleOffBg       = Color(0xFF252528)
private val DestructiveRed    = Color(0xFFE85A4A)

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onNavigateToPrivacy: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToPassword: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToLanguage: () -> Unit = {},
    onNavigateToStorage: () -> Unit = {},
    onLogOut: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PhotogramTheme(darkTheme = true) {
        SettingsContent(
            uiState                   = uiState,
            onAction                  = viewModel::onAction,
            onBack                    = onBack,
            onNavigateToPrivacy       = onNavigateToPrivacy,
            onNavigateToEditProfile   = onNavigateToEditProfile,
            onNavigateToPassword      = onNavigateToPassword,
            onNavigateToNotifications = onNavigateToNotifications,
            onNavigateToLanguage      = onNavigateToLanguage,
            onNavigateToStorage       = onNavigateToStorage,
            onLogOut                  = onLogOut,
        )
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onAction: (SettingsUiAction) -> Unit,
    onBack: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToPassword: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onNavigateToStorage: () -> Unit,
    onLogOut: () -> Unit,
) {
    val strings = SettingsStrings.forCode(uiState.selectedLanguageCode)
    val currentLang = LanguageDefaults.all.find { it.code == uiState.selectedLanguageCode }
    val langDisplay = if (currentLang != null) "${currentLang.name} ${currentLang.flag}" else uiState.selectedLanguageCode

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SettingsDarkBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 48.dp),
        ) {
            SettingsTopBar(onBack = onBack, title = strings.screenTitle)
            Spacer(Modifier.height(8.dp))

            UserCard(
                uiState                 = uiState,
                onAction                = onAction,
                onNavigateToEditProfile = onNavigateToEditProfile,
                editProfileLabel        = strings.buttonEditProfile,
            )

            SectionLabel(text = strings.sectionAccount)
            SettingsGroup {
                SettingsItem(
                    icon    = Icons.Default.Lock,
                    label   = strings.itemPassword,
                    onClick = onNavigateToPassword,
                )
                SettingsItem(
                    icon        = Icons.Default.Shield,
                    label       = strings.itemPrivacy,
                    showDivider = false,
                    onClick     = onNavigateToPrivacy,
                )
            }

            SectionLabel(text = strings.sectionPreferences)
            SettingsGroup {
                SettingsItem(
                    icon    = Icons.Default.Translate,
                    label   = strings.itemLanguage,
                    onClick = onNavigateToLanguage,
                    trailing = {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text     = langDisplay,
                                color    = TextSecondary,
                                fontSize = 13.sp,
                            )
                            Icon(
                                imageVector        = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint               = TextSecondary,
                                modifier           = Modifier.size(16.dp),
                            )
                        }
                    },
                )
                SettingsItem(
                    icon        = Icons.Default.Notifications,
                    label       = strings.itemNotifications,
                    showDivider = false,
                    onClick     = onNavigateToNotifications,
                )
            }

            SectionLabel(text = strings.sectionAlbums)
            SettingsGroup {
                StorageItem(
                    usedGb  = uiState.storageUsedGb,
                    totalGb = uiState.storageTotalGb,
                    strings = strings,
                    onClick = onNavigateToStorage,
                )
                HorizontalDivider(
                    modifier  = Modifier.padding(horizontal = 16.dp),
                    color     = DividerColor,
                    thickness = 0.5.dp,
                )
                SettingsItem(
                    icon        = Icons.Default.Download,
                    label       = strings.itemDownload,
                    showDivider = false,
                    onClick     = { onAction(SettingsUiAction.DownloadPhotosClicked) },
                )
            }

            Spacer(Modifier.height(32.dp))
            LogOutButton(
                label   = strings.buttonLogOut,
                onLogOut = { onAction(SettingsUiAction.LogOutClicked) },
            )
            Spacer(Modifier.height(16.dp))
            VersionText(version = uiState.appVersion)
            Spacer(Modifier.height(32.dp))
        }

        // ── Dialogs ───────────────────────────────────────────────────────────
        if (uiState.showLogOutDialog) {
            LogOutDialog(
                strings   = strings,
                onDismiss = { onAction(SettingsUiAction.LogOutDismissed) },
                onConfirm = {
                    onAction(SettingsUiAction.LogOutDismissed)
                    onLogOut()
                },
            )
        }
        if (uiState.showDownloadDialog) {
            DownloadDialog(
                strings   = strings,
                onDismiss = { onAction(SettingsUiAction.DownloadDismissed) },
                onConfirm = { onAction(SettingsUiAction.DownloadConfirmed) },
            )
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun SettingsTopBar(onBack: () -> Unit, title: String) {
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
            text     = title,
            style    = MaterialTheme.typography.titleLarge.copy(
                fontStyle  = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                fontSize   = 22.sp,
            ),
            color    = Color.White,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

// ── User card ─────────────────────────────────────────────────────────────────

@Composable
private fun UserCard(
    uiState: SettingsUiState,
    onAction: (SettingsUiAction) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    editProfileLabel: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(CardBg)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(AvatarBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Default.Person,
                contentDescription = null,
                tint               = Color.White.copy(alpha = 0.80f),
                modifier           = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = uiState.displayName,
                color      = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 16.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text     = uiState.email,
                color    = TextSecondary,
                fontSize = 13.sp,
            )
        }
        Spacer(Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Terracotta)
                .clickable { onNavigateToEditProfile() }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text       = editProfileLabel,
                color      = Color.White,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign  = TextAlign.Center,
                lineHeight = 16.sp,
            )
        }
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

// ── Settings group card ───────────────────────────────────────────────────────

@Composable
private fun SettingsGroup(content: @Composable () -> Unit) {
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

// ── Settings item row ─────────────────────────────────────────────────────────

@Composable
private fun SettingsItem(
    icon: ImageVector,
    label: String,
    showDivider: Boolean = true,
    onClick: () -> Unit = {},
    trailing: @Composable () -> Unit = {
        Icon(
            imageVector        = Icons.Default.ChevronRight,
            contentDescription = null,
            tint               = TextSecondary,
            modifier           = Modifier.size(18.dp),
        )
    },
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
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
            Spacer(Modifier.width(12.dp))
            Text(
                text     = label,
                color    = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
            trailing()
        }
        if (showDivider) {
            HorizontalDivider(
                modifier  = Modifier.padding(start = 68.dp),
                color     = DividerColor,
                thickness = 0.5.dp,
            )
        }
    }
}

// ── Storage item ──────────────────────────────────────────────────────────────

@Composable
private fun StorageItem(
    usedGb: Float,
    totalGb: Float,
    strings: SettingsStrings,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text     = strings.itemStorage,
                color    = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                        append("%.1f GB".format(usedGb))
                    }
                    withStyle(SpanStyle(color = TextSecondary)) {
                        append(" ${strings.storageOf} ${totalGb.toInt()} GB ${strings.storageUsed}")
                    }
                },
                fontSize = 13.sp,
            )
        }
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(ToggleOffBg),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(usedGb / totalGb)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(Terracotta),
            )
        }
    }
}

// ── Log out button ────────────────────────────────────────────────────────────

@Composable
private fun LogOutButton(label: String, onLogOut: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, Terracotta, RoundedCornerShape(22.dp))
            .clickable(onClick = onLogOut)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text       = label,
            color      = Terracotta,
            fontSize   = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

// ── Version text ──────────────────────────────────────────────────────────────

@Composable
private fun VersionText(version: String) {
    Text(
        text      = "PHOTOGRAM VERSION $version",
        modifier  = Modifier.fillMaxWidth(),
        style     = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.5.sp,
            fontSize      = 10.sp,
        ),
        color     = SectionLabelColor,
        textAlign = TextAlign.Center,
    )
}

// ── Log out dialog ────────────────────────────────────────────────────────────

@Composable
private fun LogOutDialog(
    strings: SettingsStrings,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(CardBg)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(DestructiveRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "👋", fontSize = 24.sp)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text       = strings.dialogLogOutTitle,
                color      = Color.White,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text      = strings.dialogLogOutBody,
                color     = TextSecondary,
                fontSize  = 14.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ToggleOffBg)
                        .clickable(onClick = onDismiss)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text       = strings.dialogLogOutCancel,
                        color      = Color.White,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DestructiveRed)
                        .clickable(onClick = onConfirm)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text       = strings.dialogLogOutConfirm,
                        color      = Color.White,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

// ── Download dialog ───────────────────────────────────────────────────────────

@Composable
private fun DownloadDialog(
    strings: SettingsStrings,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(CardBg)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Terracotta.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Default.Download,
                    contentDescription = null,
                    tint               = Terracotta,
                    modifier           = Modifier.size(28.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text       = strings.dialogDownloadTitle,
                color      = Color.White,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text      = strings.dialogDownloadBody,
                color     = TextSecondary,
                fontSize  = 14.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ToggleOffBg)
                        .clickable(onClick = onDismiss)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text       = strings.dialogDownloadCancel,
                        color      = Color.White,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Terracotta)
                        .clickable(onClick = onConfirm)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text       = strings.dialogDownloadConfirm,
                        color      = Color.White,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}
