package com.photogram.feature.story

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SettingsDanger = Color(0xFFE55A4E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StorySettingsSheet(
    strings: StoryStrings,
    repliesMuted: Boolean,
    onAction: (StoryViewerUiAction) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onAction(StoryViewerUiAction.CloseSettings) },
        sheetState       = sheetState,
        containerColor   = Color(0xFF1C1C1E),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(2.dp)),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
        ) {
            // Section header
            Text(
                text     = strings.manageStory,
                style    = TextStyle(
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.SemiBold,
                    color         = Color.White.copy(alpha = 0.45f),
                    letterSpacing = 1.4.sp,
                ),
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )

            SettingsDivider()
            // Share story — closes settings then opens share sheet
            SettingsItem(
                label   = strings.shareStory,
                color   = Color.White,
                onClick = {
                    onAction(StoryViewerUiAction.CloseSettings)
                    onAction(StoryViewerUiAction.OpenShare)
                },
            )
            SettingsDivider()
            // Mention — closes settings then opens mentions sheet
            SettingsItem(
                label   = strings.mentionAction,
                color   = Color.White,
                onClick = {
                    onAction(StoryViewerUiAction.CloseSettings)
                    onAction(StoryViewerUiAction.OpenMentions)
                },
            )
            SettingsDivider()
            SettingsItem(
                label   = if (repliesMuted) strings.unmuteReplies else strings.muteReplies,
                color   = Color.White,
                onClick = { onAction(StoryViewerUiAction.MuteReplies) },
            )
            SettingsDivider()
            SettingsItem(
                label   = strings.archiveStory,
                color   = Color.White,
                onClick = { onAction(StoryViewerUiAction.ArchiveStory) },
            )
            SettingsDivider()
            SettingsItem(
                label   = strings.deleteStory,
                color   = SettingsDanger,
                onClick = { onAction(StoryViewerUiAction.DeleteStory) },
            )
            SettingsDivider()
            SettingsItem(
                label   = strings.cancel,
                color   = Color.White.copy(alpha = 0.55f),
                onClick = { onAction(StoryViewerUiAction.CloseSettings) },
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SettingsItem(label: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier         = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text  = label,
            style = TextStyle(
                fontSize   = 16.sp,
                fontWeight = FontWeight.Normal,
                color      = color,
            ),
        )
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(Color.White.copy(alpha = 0.10f)),
    )
}
