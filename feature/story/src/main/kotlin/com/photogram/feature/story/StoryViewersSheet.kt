package com.photogram.feature.story

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StoryViewersSheet(
    state: StoryViewerUiState,
    strings: StoryStrings,
    onAction: (StoryViewerUiAction) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onAction(StoryViewerUiAction.CloseViewers) },
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
            // Header row: label + view count
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text  = strings.seenBy,
                    style = TextStyle(
                        fontSize      = 11.sp,
                        fontWeight    = FontWeight.SemiBold,
                        color         = Color.White.copy(alpha = 0.45f),
                        letterSpacing = 1.4.sp,
                    ),
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text  = "${state.viewCount}",
                    style = TextStyle(
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color.White.copy(alpha = 0.80f),
                    ),
                )
            }

            if (state.viewers.isEmpty()) {
                Text(
                    text     = strings.noViewsYet,
                    style    = TextStyle(
                        fontSize = 14.sp,
                        color    = Color.White.copy(alpha = 0.50f),
                    ),
                    modifier = Modifier.padding(bottom = 32.dp),
                )
            } else {
                LazyColumn {
                    items(state.viewers, key = { it.username }) { viewer ->
                        ViewerEntryRow(viewer = viewer)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ViewerEntryRow(viewer: StoryViewerEntry) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.10f)),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = viewer.displayName,
                style = TextStyle(
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color.White,
                ),
            )
            Text(
                text  = "@${viewer.username}",
                style = TextStyle(
                    fontSize = 12.sp,
                    color    = Color.White.copy(alpha = 0.50f),
                ),
            )
        }
        Text(
            text  = viewer.timeAgo,
            style = TextStyle(
                fontSize = 12.sp,
                color    = Color.White.copy(alpha = 0.40f),
            ),
        )
    }
}
