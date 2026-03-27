package com.photogram.feature.story

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.photogram.core.designsystem.LocalLanguageCode
import com.photogram.core.designsystem.PhotogramTheme

// ── Palette ───────────────────────────────────────────────────────────────────

private val Terracotta  = Color(0xFFC5663E)
private val GlassDark   = Color.Black.copy(alpha = 0.42f)
private val GlassBorder = Color.White.copy(alpha = 0.26f)

// ── Quick reactions — reference order: fire · sparkle · heart-eyes · hands · wave ─
private val QuickReactions = listOf("🔥", "✨", "😍", "🙌", "🌊")

// ---------------------------------------------------------------------------
// Public entry point
// ---------------------------------------------------------------------------

@Composable
fun StoryViewerScreen(
    onClose: () -> Unit,
    viewModel: StoryViewerViewModel = hiltViewModel(),
) {
    val state        by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager  = LocalFocusManager.current
    val strings       = StoryStrings.forCode(LocalLanguageCode.current)

    LaunchedEffect(Unit) {
        viewModel.closeEvent.collect { onClose() }
    }

    PhotogramTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .imePadding(),
        ) {

            // ── 1. Full-screen media gradient ─────────────────────────────────
            StoryMediaBackground(
                colorArgb = state.currentStory?.mediaColorArgb ?: 0xFF0A1820L,
                modifier  = Modifier.fillMaxSize(),
            )

            // ── 2. Prev / next tap zones — transparent gesture areas ──────────
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.4f)
                        .pointerInput(Unit) {
                            detectTapGestures { viewModel.onAction(StoryViewerUiAction.TapPrev) }
                        },
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.6f)
                        .pointerInput(Unit) {
                            detectTapGestures { viewModel.onAction(StoryViewerUiAction.TapNext) }
                        },
                )
            }

            // ── 3. Chevron hints — display-only, gesture-transparent ──────────
            Text(
                text       = "<",
                modifier   = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 14.dp),
                color      = Color.White.copy(alpha = 0.26f),
                fontSize   = 34.sp,
                fontWeight = FontWeight.Light,
            )
            Text(
                text       = ">",
                modifier   = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 14.dp),
                color      = Color.White.copy(alpha = 0.26f),
                fontSize   = 34.sp,
                fontWeight = FontWeight.Light,
            )

            // ── 4. Center reaction emoji — shown when a reaction is selected ──
            state.reactionSent?.let { emoji ->
                Box(
                    modifier         = Modifier.align(Alignment.Center),
                    contentAlignment = Alignment.Center,
                ) {
                    // Translucent backing circle (matches reference)
                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.10f)),
                    )
                    Text(
                        text     = emoji,
                        fontSize = 74.sp,
                    )
                }
            }

            // ── 5. Top chrome: progress bars + author pill + close ────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                StoryProgressRow(
                    count        = state.stories.size,
                    currentIndex = state.currentIndex,
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StoryAuthorPill(
                        authorName = state.currentStory?.authorName ?: "",
                        timeAgo    = state.currentStory?.timeAgo ?: "",
                    )
                    Spacer(Modifier.weight(1f))
                    StoryCloseButton(
                        onClick = { viewModel.onAction(StoryViewerUiAction.Close) },
                        desc    = strings.closeStory,
                    )
                }
            }

            // ── 6. Bottom chrome: caption + date + reply + reactions ──────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.00f to Color.Transparent,
                                0.25f to Color(0x66000000),
                                0.55f to Color(0xBB000000),
                                1.00f to Color(0xF0000000),
                            ),
                        )
                    )
                    .navigationBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 100.dp, bottom = 16.dp),
            ) {
                // Caption — large italic serif
                state.currentStory?.caption?.let { caption ->
                    Text(
                        text  = caption,
                        style = TextStyle(
                            fontSize   = 30.sp,
                            fontStyle  = FontStyle.Italic,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.Serif,
                            color      = Color.White,
                            lineHeight = 38.sp,
                        ),
                    )
                    Spacer(Modifier.height(6.dp))
                }

                // Metadata — small uppercase tracked
                state.currentStory?.dateLabel?.let { date ->
                    Text(
                        text  = date,
                        style = TextStyle(
                            fontSize      = 11.sp,
                            fontWeight    = FontWeight.Medium,
                            color         = Color.White.copy(alpha = 0.70f),
                            letterSpacing = 1.4.sp,
                        ),
                    )
                }

                Spacer(Modifier.height(18.dp))

                // Reply row: [message input] [❤] [▶]
                StoryReplyInput(
                    text         = state.replyText,
                    replySent    = state.replySent,
                    strings      = strings,
                    onTextChange = { viewModel.onAction(StoryViewerUiAction.ReplyTextChanged(it)) },
                    onHeartClick = { viewModel.onAction(StoryViewerUiAction.ReactionTapped("❤️")) },
                    onSend       = {
                        viewModel.onAction(StoryViewerUiAction.SendReply)
                        focusManager.clearFocus()
                    },
                )

                Spacer(Modifier.height(14.dp))

                // Quick reactions row
                StoryReactionRow(
                    selectedReaction = state.reactionSent,
                    onReaction       = { emoji ->
                        viewModel.onAction(StoryViewerUiAction.ReactionTapped(emoji))
                    },
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Media background
// ---------------------------------------------------------------------------

@Composable
private fun StoryMediaBackground(colorArgb: Long, modifier: Modifier = Modifier) {
    // Multi-stop sunset gradient — simulates golden-hour shore photo.
    // The per-story color tint is blended in at the golden-yellow peak stop.
    val tint = Color(colorArgb)
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                0.00f to Color(0xFF8C3808),
                0.10f to Color(0xFFB85018),
                0.22f to Color(0xFFD07020),
                0.32f to Color(
                    red   = 0xD8 / 255f * 0.65f + tint.red   * 0.35f,
                    green = 0x98 / 255f * 0.65f + tint.green * 0.35f,
                    blue  = 0x10 / 255f * 0.65f + tint.blue  * 0.35f,
                ),
                0.41f to Color(0xFFD07028),
                0.50f to Color(0xFFC05030),
                0.57f to Color(0xFFA04028),
                0.64f to Color(0xFF987060),
                0.72f to Color(0xFF7A8070),
                0.81f to Color(0xFF4A6870),
                0.90f to Color(0xFF283848),
                1.00f to Color(0xFF0A1820),
            ),
        ),
    )
}

// ---------------------------------------------------------------------------
// Progress bars
// ---------------------------------------------------------------------------

@Composable
private fun StoryProgressRow(count: Int, currentIndex: Int) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        repeat(count) { i ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(
                        if (i <= currentIndex) Color.White
                        else Color.White.copy(alpha = 0.30f),
                    ),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Author pill
// ---------------------------------------------------------------------------

@Composable
private fun StoryAuthorPill(authorName: String, timeAgo: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(GlassDark)
            .padding(start = 5.dp, end = 14.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Avatar — warm circular placeholder simulating a face photo
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFD4A880), Color(0xFF8A5A38)),
                    )
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.18f), Color.Transparent)
                        )
                    ),
            )
        }

        // Display name
        Text(
            text  = authorName,
            style = TextStyle(
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Color.White,
            ),
        )

        // Time ago — muted
        Text(
            text  = timeAgo,
            style = TextStyle(
                fontSize   = 13.sp,
                fontWeight = FontWeight.Normal,
                color      = Color.White.copy(alpha = 0.55f),
            ),
        )
    }
}

// ---------------------------------------------------------------------------
// Close button
// ---------------------------------------------------------------------------

@Composable
private fun StoryCloseButton(onClick: () -> Unit, desc: String) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(GlassDark)
            .border(0.5.dp, GlassBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector        = Icons.Default.Close,
            contentDescription = desc,
            tint               = Color.White,
            modifier           = Modifier.size(18.dp),
        )
    }
}

// ---------------------------------------------------------------------------
// Quick reactions row
// ---------------------------------------------------------------------------

@Composable
private fun StoryReactionRow(
    selectedReaction: String?,
    onReaction:       (String) -> Unit,
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        QuickReactions.forEach { emoji ->
            val isSelected = emoji == selectedReaction
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(GlassDark)
                    .border(
                        width = if (isSelected) 2.dp else 0.5.dp,
                        color = if (isSelected) Terracotta else GlassBorder,
                        shape = CircleShape,
                    )
                    .clickable { onReaction(emoji) },
                contentAlignment = Alignment.Center,
            ) {
                Text(text = emoji, fontSize = 24.sp)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Reply input row
// ---------------------------------------------------------------------------

@Composable
private fun StoryReplyInput(
    text:         String,
    replySent:    Boolean,
    strings:      StoryStrings,
    onTextChange: (String) -> Unit,
    onHeartClick: () -> Unit,
    onSend:       () -> Unit,
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Message input field
        BasicTextField(
            value         = text,
            onValueChange = onTextChange,
            modifier      = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(50.dp))
                .background(GlassDark)
                .border(0.5.dp, GlassBorder, RoundedCornerShape(50.dp))
                .padding(horizontal = 18.dp, vertical = 13.dp),
            textStyle         = TextStyle(color = Color.White, fontSize = 14.sp),
            singleLine        = true,
            keyboardOptions   = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions   = KeyboardActions(onSend = { onSend() }),
            decorationBox     = { innerTextField ->
                if (text.isEmpty()) {
                    Text(
                        text  = if (replySent) strings.replySentPlaceholder
                                else strings.replyDefaultPlaceholder,
                        style = TextStyle(
                            color    = Color.White.copy(alpha = 0.50f),
                            fontSize = 14.sp,
                        ),
                    )
                }
                innerTextField()
            },
        )

        // Heart quick-reaction button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(GlassDark)
                .border(0.5.dp, GlassBorder, CircleShape)
                .clickable(onClick = onHeartClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Default.Favorite,
                contentDescription = strings.heartReactDesc,
                tint               = Color.White,
                modifier           = Modifier.size(20.dp),
            )
        }

        // Send / forward button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(GlassDark)
                .border(0.5.dp, GlassBorder, CircleShape)
                .clickable(enabled = text.isNotBlank(), onClick = onSend),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = strings.sendReplyDesc,
                tint               = if (text.isNotBlank()) Color.White
                                     else Color.White.copy(alpha = 0.40f),
                modifier           = Modifier.size(20.dp),
            )
        }
    }
}
