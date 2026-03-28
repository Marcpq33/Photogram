package com.photogram.feature.story

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ShareAccent      = Color(0xFFC5663E)
private val ShareGlassBorder = Color.White.copy(alpha = 0.18f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StoryShareSheet(
    albumId: String,
    strings: StoryStrings,
    onAction: (StoryViewerUiAction) -> Unit,
) {
    val sheetState       = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val clipboardManager = LocalClipboardManager.current
    var linkCopied by remember { mutableStateOf(false) }
    val shareUrl = "https://photogram.app/stories/$albumId"

    ModalBottomSheet(
        onDismissRequest = { onAction(StoryViewerUiAction.CloseShare) },
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
                text     = strings.shareHeader,
                style    = TextStyle(
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.SemiBold,
                    color         = Color.White.copy(alpha = 0.45f),
                    letterSpacing = 1.4.sp,
                ),
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
            )

            // Copy link row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.07f))
                    .border(
                        width = 0.5.dp,
                        color = if (linkCopied) ShareAccent else ShareGlassBorder,
                        shape = RoundedCornerShape(14.dp),
                    )
                    .clickable {
                        clipboardManager.setText(AnnotatedString(shareUrl))
                        linkCopied = true
                    }
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector        = Icons.Default.ContentCopy,
                    contentDescription = strings.copyLink,
                    tint               = if (linkCopied) ShareAccent else Color.White,
                    modifier           = Modifier.size(20.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = if (linkCopied) strings.linkCopied else strings.copyLink,
                        style = TextStyle(
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = if (linkCopied) ShareAccent else Color.White,
                        ),
                    )
                    Text(
                        text  = shareUrl,
                        style = TextStyle(
                            fontSize = 11.sp,
                            color    = Color.White.copy(alpha = 0.40f),
                        ),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
