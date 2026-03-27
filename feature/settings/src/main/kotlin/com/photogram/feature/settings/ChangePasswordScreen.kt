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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.photogram.core.designsystem.LocalLanguageCode
import com.photogram.core.designsystem.PhotogramTheme

// ── Palette ───────────────────────────────────────────────────────────────────
private val CpDarkBg       = Color(0xFF050505)
private val CpCardBg       = Color(0xFF121214)
private val CpTerracotta   = Color(0xFFC5663E)
private val CpTextSec      = Color(0xFF8A8A8E)
private val CpSectionLabel = Color(0xFF6E6E73)
private val CpDivider      = Color(0x14FFFFFF)

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit = {},
    viewModel: ChangePasswordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PhotogramTheme(darkTheme = true) {
        ChangePasswordContent(
            uiState  = uiState,
            onAction = viewModel::onAction,
            onBack   = onBack,
        )
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun ChangePasswordContent(
    uiState: ChangePasswordUiState,
    onAction: (ChangePasswordUiAction) -> Unit,
    onBack: () -> Unit,
) {
    val strings = ChangePasswordStrings.forCode(LocalLanguageCode.current)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CpDarkBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 48.dp, bottom = 40.dp),
        ) {
            CpTopBar(onBack = onBack, strings = strings)
            Spacer(Modifier.height(8.dp))

            Text(
                text     = strings.sectionSecurity,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
                style    = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    fontSize      = 11.sp,
                ),
                color = CpSectionLabel,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CpCardBg),
            ) {
                CpPasswordRow(
                    label       = strings.currentPassword,
                    value       = uiState.currentPassword,
                    show        = uiState.showCurrentPassword,
                    onToggle    = { onAction(ChangePasswordUiAction.ToggleCurrentVisibility) },
                    onValueChange = { onAction(ChangePasswordUiAction.CurrentPasswordChanged(it)) },
                )
                HorizontalDivider(
                    modifier  = Modifier.padding(start = 16.dp),
                    color     = CpDivider,
                    thickness = 0.5.dp,
                )
                CpPasswordRow(
                    label       = strings.newPassword,
                    value       = uiState.newPassword,
                    show        = uiState.showNewPassword,
                    onToggle    = { onAction(ChangePasswordUiAction.ToggleNewVisibility) },
                    onValueChange = { onAction(ChangePasswordUiAction.NewPasswordChanged(it)) },
                )
                HorizontalDivider(
                    modifier  = Modifier.padding(start = 16.dp),
                    color     = CpDivider,
                    thickness = 0.5.dp,
                )
                CpPasswordRow(
                    label         = strings.confirmPassword,
                    value         = uiState.confirmPassword,
                    show          = uiState.showConfirmPassword,
                    showDivider   = false,
                    onToggle      = { onAction(ChangePasswordUiAction.ToggleConfirmVisibility) },
                    onValueChange = { onAction(ChangePasswordUiAction.ConfirmPasswordChanged(it)) },
                )
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text     = strings.passwordHint,
                modifier = Modifier.padding(horizontal = 20.dp),
                color    = CpTextSec,
                fontSize = 12.sp,
            )

            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CpTerracotta)
                    .clickable { onAction(ChangePasswordUiAction.SaveClicked) }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = strings.changePasswordButton,
                    color      = Color.White,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    style      = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.5.sp),
                )
            }
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun CpTopBar(onBack: () -> Unit, strings: ChangePasswordStrings) {
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

// ── Password row ──────────────────────────────────────────────────────────────

@Composable
private fun CpPasswordRow(
    label: String,
    value: String,
    show: Boolean,
    showDivider: Boolean = true,
    onToggle: () -> Unit,
    onValueChange: (String) -> Unit,
) {
    val strings = ChangePasswordStrings.forCode(LocalLanguageCode.current)
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, color = CpTextSec, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                BasicTextField(
                    value                = value,
                    onValueChange        = onValueChange,
                    textStyle            = TextStyle(color = Color.White, fontSize = 15.sp),
                    cursorBrush          = SolidColor(CpTerracotta),
                    singleLine           = true,
                    visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
                    decorationBox        = { inner ->
                        if (value.isEmpty()) {
                            Text(text = "••••••••", color = CpTextSec.copy(alpha = 0.4f), fontSize = 15.sp)
                        }
                        inner()
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector        = if (show) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (show) strings.hide else strings.show,
                    tint               = CpTextSec,
                    modifier           = Modifier.size(18.dp),
                )
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier  = Modifier.padding(start = 16.dp),
                color     = CpDivider,
                thickness = 0.5.dp,
            )
        }
    }
}
