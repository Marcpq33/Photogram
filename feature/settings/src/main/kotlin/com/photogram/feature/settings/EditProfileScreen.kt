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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.photogram.core.designsystem.LocalLanguageCode
import com.photogram.core.designsystem.PhotogramTheme

// ── Palette ───────────────────────────────────────────────────────────────────
private val EpDarkBg       = Color(0xFF050505)
private val EpCardBg       = Color(0xFF121214)
private val EpTerracotta   = Color(0xFFC9A96E)
private val EpAvatarBg     = Color(0xFF3A2518)
private val EpTextSec      = Color(0xFF8A8A8E)
private val EpSectionLabel = Color(0xFF6E6E73)
private val EpDivider      = Color(0x14FFFFFF)

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun EditProfileScreen(
    onBack: () -> Unit = {},
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PhotogramTheme(darkTheme = true) {
        EditProfileContent(
            uiState  = uiState,
            onAction = viewModel::onAction,
            onBack   = onBack,
        )
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun EditProfileContent(
    uiState: EditProfileUiState,
    onAction: (EditProfileUiAction) -> Unit,
    onBack: () -> Unit,
) {
    val strings = EditProfileStrings.forCode(LocalLanguageCode.current)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri != null) {
            onAction(EditProfileUiAction.AvatarSelected(uri.toString()))
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EpDarkBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 48.dp)
                .navigationBarsPadding()
                .padding(bottom = 40.dp),
        ) {
            EpTopBar(onBack = onBack, strings = strings)
            Spacer(Modifier.height(24.dp))

            // Avatar with edit badge — tapping opens file picker
            Box(
                modifier         = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier         = Modifier.clickable { launcher.launch(ActivityResultContracts.PickVisualMedia.ImageOnly) },
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(EpAvatarBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (uiState.avatarUri.isNotBlank()) {
                            AsyncImage(
                                model              = uiState.avatarUri,
                                contentDescription = null,
                                contentScale       = ContentScale.Crop,
                                modifier           = Modifier
                                    .size(88.dp)
                                    .clip(CircleShape),
                            )
                        } else {
                            Icon(
                                imageVector        = Icons.Default.Person,
                                contentDescription = null,
                                tint               = Color.White.copy(alpha = 0.80f),
                                modifier           = Modifier.size(44.dp),
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(EpTerracotta),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Edit,
                            contentDescription = "Edit photo",
                            tint               = Color.White,
                            modifier           = Modifier.size(14.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text      = strings.changePhoto,
                color     = EpTerracotta,
                fontSize  = 13.sp,
                modifier  = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(24.dp))

            EpSectionLabel(strings.sectionInfo)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(EpCardBg),
            ) {
                EpFieldRow(
                    label       = strings.nameLabel,
                    value       = uiState.displayName,
                    placeholder = strings.namePlaceholder,
                    onValueChange = { onAction(EditProfileUiAction.DisplayNameChanged(it)) },
                )
                HorizontalDivider(
                    modifier  = Modifier.padding(start = 16.dp),
                    color     = EpDivider,
                    thickness = 0.5.dp,
                )
                EpFieldRow(
                    label       = strings.usernameLabel,
                    value       = uiState.username,
                    placeholder = strings.usernamePlaceholder,
                    onValueChange = { onAction(EditProfileUiAction.UsernameChanged(it)) },
                    showDivider = false,
                )
            }

            EpSectionLabel(strings.sectionBio)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(EpCardBg)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Text(text = strings.bioLabel, color = EpTextSec, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                BasicTextField(
                    value         = uiState.bio,
                    onValueChange = { onAction(EditProfileUiAction.BioChanged(it)) },
                    textStyle     = TextStyle(color = Color.White, fontSize = 15.sp),
                    cursorBrush   = SolidColor(EpTerracotta),
                    decorationBox = { inner ->
                        if (uiState.bio.isEmpty()) {
                            Text(text = strings.bioPlaceholder, color = EpTextSec, fontSize = 15.sp)
                        }
                        inner()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                )
            }

            EpSectionLabel(strings.sectionContact)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(EpCardBg),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text     = strings.emailLabel,
                        color    = EpTextSec,
                        fontSize = 15.sp,
                        modifier = Modifier.width(80.dp),
                    )
                    Text(
                        text     = uiState.email,
                        color    = Color.White.copy(alpha = 0.60f),
                        fontSize = 15.sp,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(EpTerracotta)
                    .clickable { onAction(EditProfileUiAction.SaveClicked) }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = strings.saveButton,
                    color      = Color.White,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    style      = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.5.sp),
                )
            }

            if (uiState.saveSuccess) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text      = strings.saveSuccessMessage,
                    color     = EpTerracotta,
                    fontSize  = 14.sp,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun EpTopBar(onBack: () -> Unit, strings: EditProfileStrings) {
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
private fun EpSectionLabel(text: String) {
    Text(
        text     = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        style    = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.5.sp,
            fontSize      = 11.sp,
        ),
        color = EpSectionLabel,
    )
}

// ── Field row ─────────────────────────────────────────────────────────────────

@Composable
private fun EpFieldRow(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    showDivider: Boolean = true,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text     = label,
                color    = EpTextSec,
                fontSize = 15.sp,
                modifier = Modifier.width(80.dp),
            )
            BasicTextField(
                value         = value,
                onValueChange = onValueChange,
                textStyle     = TextStyle(color = Color.White, fontSize = 15.sp),
                cursorBrush   = SolidColor(EpTerracotta),
                singleLine    = true,
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(text = placeholder, color = EpTextSec.copy(alpha = 0.6f), fontSize = 15.sp)
                    }
                    inner()
                },
                modifier = Modifier.weight(1f),
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier  = Modifier.padding(start = 16.dp),
                color     = EpDivider,
                thickness = 0.5.dp,
            )
        }
    }
}
