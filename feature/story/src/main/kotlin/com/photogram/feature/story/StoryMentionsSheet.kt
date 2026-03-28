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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val MentionAccent       = Color(0xFFC5663E)
private val MentionGlassBorder  = Color.White.copy(alpha = 0.20f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StoryMentionsSheet(
    state: StoryViewerUiState,
    strings: StoryStrings,
    onAction: (StoryViewerUiAction) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onAction(StoryViewerUiAction.CloseMentions) },
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
            // Header row: label + done button
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text     = strings.tagPeople,
                    style    = TextStyle(
                        fontSize      = 11.sp,
                        fontWeight    = FontWeight.SemiBold,
                        color         = Color.White.copy(alpha = 0.45f),
                        letterSpacing = 1.4.sp,
                    ),
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(MentionAccent)
                        .clickable { onAction(StoryViewerUiAction.ConfirmMentions) }
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                ) {
                    Text(
                        text  = strings.done,
                        style = TextStyle(
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color.White,
                        ),
                    )
                }
            }

            // Search field
            BasicTextField(
                value         = state.mentionQuery,
                onValueChange = { onAction(StoryViewerUiAction.MentionQueryChanged(it)) },
                modifier      = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                textStyle     = TextStyle(color = Color.White, fontSize = 14.sp),
                singleLine    = true,
                cursorBrush   = SolidColor(MentionAccent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                decorationBox = { innerTextField ->
                    if (state.mentionQuery.isEmpty()) {
                        Text(
                            text  = strings.searchPeople,
                            style = TextStyle(
                                color    = Color.White.copy(alpha = 0.40f),
                                fontSize = 14.sp,
                            ),
                        )
                    }
                    innerTextField()
                },
            )

            Spacer(Modifier.height(12.dp))

            // Candidate list — max height to keep sheet manageable
            LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {
                items(state.mentionCandidates, key = { it.id }) { user ->
                    MentionCandidateRow(
                        user     = user,
                        onToggle = { onAction(StoryViewerUiAction.ToggleMentionUser(user.id)) },
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun MentionCandidateRow(
    user: StoryMentionUser,
    onToggle: () -> Unit,
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.10f)),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = user.displayName,
                style = TextStyle(
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color.White,
                ),
            )
            Text(
                text  = "@${user.username}",
                style = TextStyle(
                    fontSize = 12.sp,
                    color    = Color.White.copy(alpha = 0.50f),
                ),
            )
        }
        // Selection indicator circle
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(if (user.selected) MentionAccent else Color.Transparent)
                .border(
                    width = 1.5.dp,
                    color = if (user.selected) MentionAccent else MentionGlassBorder,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (user.selected) {
                Icon(
                    imageVector        = Icons.Default.Check,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(14.dp),
                )
            }
        }
    }
}
