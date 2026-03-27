package com.photogram.feature.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.photogram.core.designsystem.LocalLanguageCode
import com.photogram.core.designsystem.PhotogramTheme
import com.photogram.core.navigation.PhotogramDestination

// ── Palette ───────────────────────────────────────────────────────────────────

private val NotifDarkBg       = Color(0xFF050505)
private val NotifCardBg       = Color(0xFF121214)
private val Terracotta        = Color(0xFFC5663E)
private val TextSec           = Color(0xFF8A8A8E)
private val SectionLabelColor = Color(0xFF6E6E73)
private val ChipUnselectedBg  = Color(0xFF1A1A1E)
private val ItemDividerColor  = Color(0x14FFFFFF)
private val FollowBtnBg       = Color(0xFF1A1A1E)

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun NotificationsScreen(
    onNavigate: (String) -> Unit = {},
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.navEvent) {
        viewModel.navEvent.collect { route -> onNavigate(route) }
    }

    PhotogramTheme(darkTheme = true) {
        NotifContent(
            uiState  = uiState,
            onAction = viewModel::onAction,
            onBack   = { onNavigate(PhotogramDestination.Home.route) },
        )
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun NotifContent(
    uiState:  NotificationsUiState,
    onAction: (NotificationsUiAction) -> Unit,
    onBack:   () -> Unit,
) {
    val strings = NotificationsStrings.forCode(LocalLanguageCode.current)
    val groupedItems = notifGroupOrder.mapNotNull { group ->
        val items = uiState.displayed.filter { it.group == group }
        if (items.isEmpty()) null else group to items
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NotifDarkBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            Spacer(Modifier.height(8.dp))

            NotifTopBar(
                onBack  = onBack,
                strings = strings,
            )

            FilterChipsRow(
                selected = uiState.selectedFilter,
                onSelect = { onAction(NotificationsUiAction.FilterSelected(it)) },
            )

            LazyColumn(
                modifier       = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                if (groupedItems.isEmpty()) {
                    item { NotifEmptyState() }
                } else {
                    groupedItems.forEach { (group, items) ->
                        item(key = "header_${group.name}") {
                            SectionHeader(group = group, strings = strings)
                        }
                        items(items = items, key = { it.id }) { notif ->
                            NotifRow(
                                item  = notif,
                                onTap = { onAction(NotificationsUiAction.NotifTapped(notif.id)) },
                            )
                        }
                        item(key = "gap_${group.name}") {
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // Filter bottom-sheet placeholder
        if (uiState.showFilterSheet) {
            FilterSheetDialog(
                strings   = strings,
                onDismiss = { onAction(NotificationsUiAction.FilterSheetDismissed) },
            )
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun NotifTopBar(
    onBack:  () -> Unit,
    strings: NotificationsStrings,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 4.dp),
    ) {
        IconButton(
            onClick  = onBack,
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = strings.back,
                tint               = Color.White,
                modifier           = Modifier.size(22.dp),
            )
        }

        Text(
            text       = strings.title,
            color      = Color.White,
            fontSize   = 24.sp,
            fontStyle  = FontStyle.Italic,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Light,
            modifier   = Modifier.align(Alignment.Center),
        )
    }
}

// ── Filter chips ──────────────────────────────────────────────────────────────

@Composable
private fun FilterChipsRow(
    selected: NotifFilter,
    onSelect: (NotifFilter) -> Unit,
) {
    LazyRow(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding        = PaddingValues(horizontal = 16.dp),
    ) {
        items(items = NotifFilter.values().toList(), key = { it.name }) { filter ->
            val isSelected = filter == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(if (isSelected) Color.White else ChipUnselectedBg)
                    .border(
                        width = if (isSelected) 0.dp else 0.5.dp,
                        color = Color.White.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(50.dp),
                    )
                    .clickable { onSelect(filter) }
                    .padding(horizontal = 18.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = filter.label,
                    color      = if (isSelected) Color(0xFF0A0A0A) else Color.White.copy(alpha = 0.70f),
                    fontSize   = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}

// ── Section header ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(group: NotifGroup, strings: NotificationsStrings) {
    when (group) {
        NotifGroup.TODAY, NotifGroup.YESTERDAY -> {
            val label = if (group == NotifGroup.TODAY) strings.today else strings.yesterday
            Column {
                Text(
                    text       = label,
                    modifier   = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 10.dp),
                    fontSize   = 22.sp,
                    fontStyle  = FontStyle.Italic,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light,
                    color      = Color.White,
                )
                HorizontalDivider(
                    modifier  = Modifier.padding(horizontal = 16.dp),
                    color     = Color.White.copy(alpha = 0.08f),
                    thickness = 0.5.dp,
                )
            }
        }
        NotifGroup.THIS_WEEK -> {
            Text(
                text          = strings.thisWeek,
                modifier      = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 6.dp),
                fontSize      = 11.sp,
                letterSpacing = 2.sp,
                fontWeight    = FontWeight.Bold,
                color         = SectionLabelColor,
            )
        }
    }
}

// ── Notification row ──────────────────────────────────────────────────────────

@Composable
private fun NotifRow(item: NotifItem, onTap: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTap)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Avatar with optional unread dot
        NotifAvatar(item = item)

        Spacer(Modifier.width(12.dp))

        // Text block
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 2.dp),
        ) {
            Text(
                text       = buildNotifAnnotatedString(item),
                fontSize   = 14.sp,
                lineHeight = 20.sp,
                maxLines   = 5,
                overflow   = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text          = item.timestampLabel,
                color         = TextSec,
                fontSize      = 11.sp,
                fontWeight    = FontWeight.Medium,
                letterSpacing = 0.6.sp,
            )
        }

        Spacer(Modifier.width(12.dp))

        // Right-side element (thumb / stacked / follow / nothing)
        NotifRightElement(item = item)
    }

    HorizontalDivider(
        modifier  = Modifier.padding(start = 80.dp, end = 16.dp),
        color     = ItemDividerColor,
        thickness = 0.5.dp,
    )
}

// ── Annotated body builder (pure function — no @Composable) ──────────────────

private fun buildNotifAnnotatedString(item: NotifItem): AnnotatedString = buildAnnotatedString {
    // Bold display name
    if (item.displayName.isNotEmpty()) {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
            append(item.displayName)
        }
    }

    val body         = item.body
    val italicPhrase = item.bodyItalicPhrase

    if (italicPhrase != null && body.contains(italicPhrase)) {
        val idx = body.indexOf(italicPhrase)
        // Normal text before italic phrase
        withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = Color.White)) {
            append(body.substring(0, idx))
        }
        // Italic serif phrase (e.g. album name)
        withStyle(
            SpanStyle(
                fontStyle  = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                color      = Color.White,
            )
        ) {
            append(italicPhrase)
        }
        // Remaining normal text
        withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = Color.White)) {
            append(body.substring(idx + italicPhrase.length))
        }
    } else {
        withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = Color.White)) {
            append(body)
        }
    }
}

// ── Avatar ────────────────────────────────────────────────────────────────────

@Composable
private fun NotifAvatar(item: NotifItem) {
    Box(modifier = Modifier.size(52.dp)) {
        if (item.isSystemNotif) {
            // Rounded-square system icon with sparkle glyph
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF282830), Color(0xFF1A1A22))
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "✦", fontSize = 22.sp, color = Color.White.copy(alpha = 0.88f))
            }
        } else {
            // Circular user avatar — warm gradient simulating face photo
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(item.avatarColorArgb)),
            ) {
                // Subtle top-to-bottom highlight to suggest photo depth
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.14f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.20f),
                                )
                            )
                        ),
                )
            }
        }

        // Unread indicator dot (terracotta, top-right corner)
        if (item.isUnread) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Terracotta)
                    .border(1.5.dp, NotifDarkBg, CircleShape)
                    .align(Alignment.TopEnd)
                    .offset(x = 1.dp, y = (-1).dp),
            )
        }
    }
}

// ── Right-side element ────────────────────────────────────────────────────────

@Composable
private fun NotifRightElement(item: NotifItem) {
    when (item.thumbStyle) {
        ThumbStyle.NONE -> {
            // No right element — nothing rendered
        }

        ThumbStyle.ROUND -> {
            // Single circular thumbnail
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(item.thumbColorArgb)),
            ) {
                // Subtle overlay suggesting photo content (horizontal gradient)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.White.copy(0.06f), Color.Transparent)
                            )
                        )
                )
            }
        }

        ThumbStyle.STACKED_WITH_BADGE -> {
            // Main circular thumb + overlapping badge circle
            Box(modifier = Modifier.size(width = 72.dp, height = 52.dp)) {
                // Main thumb (left-aligned)
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(item.thumbColorArgb))
                        .align(Alignment.CenterStart),
                )
                // Badge circle (right, overlapping)
                if (item.thumbExtraCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1C2230))
                            .border(1.5.dp, NotifDarkBg, CircleShape)
                            .align(Alignment.CenterEnd),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text       = "+${item.thumbExtraCount}",
                            color      = Color.White,
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        ThumbStyle.FOLLOW_BUTTON -> {
            // Outlined white pill button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(FollowBtnBg)
                    .border(1.dp, Color.White.copy(alpha = 0.50f), RoundedCornerShape(50.dp))
                    .padding(horizontal = 18.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text          = "FOLLOW",
                    color         = Color.White,
                    fontSize      = 12.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun NotifEmptyState() {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(top = 72.dp, bottom = 48.dp, start = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text     = "✦",
            fontSize = 36.sp,
            color    = Color.White.copy(alpha = 0.12f),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text       = "All quiet here",
            fontSize   = 22.sp,
            fontStyle  = FontStyle.Italic,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Light,
            color      = Color.White.copy(alpha = 0.40f),
        )
        Text(
            text      = "Reactions, comments, and activity\nfrom your albums will appear here.",
            fontSize  = 13.sp,
            lineHeight = 19.sp,
            color     = Color.White.copy(alpha = 0.22f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

// ── Filter sheet dialog ───────────────────────────────────────────────────────

@Composable
private fun FilterSheetDialog(strings: NotificationsStrings, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(NotifCardBg)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text       = strings.filters,
                color      = Color.White,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text     = strings.advancedFilters,
                color    = TextSec,
                fontSize = 14.sp,
            )
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Terracotta)
                    .clickable(onClick = onDismiss)
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = strings.close,
                    color      = Color.White,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
