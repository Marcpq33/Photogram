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
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.photogram.core.designsystem.PhotogramTheme

// ── Palette ───────────────────────────────────────────────────────────────────
private val LpDarkBg       = Color(0xFF050505)
private val LpCardBg       = Color(0xFF121214)
private val LpTerracotta   = Color(0xFFC5663E)
private val LpTextSec      = Color(0xFF8A8A8E)
private val LpSectionLabel = Color(0xFF6E6E73)
private val LpDivider      = Color(0x14FFFFFF)

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun LanguagePickerScreen(
    onBack: () -> Unit = {},
    viewModel: LanguagePickerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PhotogramTheme(darkTheme = true) {
        LanguagePickerContent(
            uiState  = uiState,
            onAction = viewModel::onAction,
            onBack   = onBack,
        )
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun LanguagePickerContent(
    uiState: LanguagePickerUiState,
    onAction: (LanguagePickerUiAction) -> Unit,
    onBack: () -> Unit,
) {
    val strings = SettingsStrings.forCode(uiState.selectedCode)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LpDarkBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 48.dp, bottom = 32.dp),
        ) {
            LpTopBar(onBack = onBack, title = strings.languageScreenTitle)
            Spacer(Modifier.height(8.dp))

            Text(
                text     = strings.languageSectionLabel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
                style    = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    fontSize      = 11.sp,
                ),
                color = LpSectionLabel,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LpCardBg),
            ) {
                uiState.languages.forEachIndexed { index, lang ->
                    val isSelected = lang.code == uiState.selectedCode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAction(LanguagePickerUiAction.LanguageSelected(lang.code)) }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text     = lang.flag,
                            fontSize = 26.sp,
                        )
                        Spacer(Modifier.width(14.dp))
                        Text(
                            text       = lang.name,
                            color      = Color.White,
                            fontSize   = 16.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            modifier   = Modifier.weight(1f),
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(LpTerracotta),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.Check,
                                    contentDescription = null,
                                    tint               = Color.White,
                                    modifier           = Modifier.size(16.dp),
                                )
                            }
                        }
                    }
                    if (index < uiState.languages.lastIndex) {
                        HorizontalDivider(
                            modifier  = Modifier.padding(start = 56.dp),
                            color     = LpDivider,
                            thickness = 0.5.dp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text     = strings.languageFooter,
                modifier = Modifier.padding(horizontal = 20.dp),
                color    = LpTextSec,
                fontSize = 12.sp,
            )
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun LpTopBar(onBack: () -> Unit, title: String) {
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
            text  = title,
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
