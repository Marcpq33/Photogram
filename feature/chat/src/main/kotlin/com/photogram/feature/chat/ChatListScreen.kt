package com.photogram.feature.chat

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.photogram.core.designsystem.LocalLanguageCode
import com.photogram.core.designsystem.PhotogramTheme

// ── Local palette ─────────────────────────────────────────────────────────────

private val ChatBg      = Color(0xFF050505)
private val Terracotta  = Color(0xFFC5663E)
private val SearchBg    = Color(0xFF0B0B0D)
private val DividerCol  = Color(0x14FFFFFF)
private val TextPrimary = Color.White
private val TextGray    = Color(0xFF888890)
private val OnlineDot   = Color(0xFF4CAF50)
private val OfflineDot  = Color(0xFF555560)

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun ChatListScreen(
    onNavigate: (String) -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: ChatListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.navEvent.collect { route -> onNavigate(route) }
    }
    val currentChatStrings by rememberUpdatedState(ChatStrings.forCode(LocalLanguageCode.current))
    LaunchedEffect(viewModel.snackbarEvent) {
        viewModel.snackbarEvent.collect { key ->
            val message = when (key) {
                ChatSnackbarKey.NewConversation -> currentChatStrings.snackbarNewConversation
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    PhotogramTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ChatBg)
                .statusBarsPadding(),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                ChatTopBar(onBack = onBack)
                ChatSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.onAction(ChatListUiAction.SearchQueryChanged(it)) },
                )
                FilterChipsRow(
                    filters  = uiState.filters,
                    selected = uiState.selectedFilter,
                    onFilterSelected = { viewModel.onAction(ChatListUiAction.FilterSelected(it)) },
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 88.dp),
                ) {
                    if (uiState.displayed.isEmpty()) {
                        item { ChatEmptyState() }
                    } else {
                        items(uiState.displayed, key = { it.id }) { item ->
                            ChatItemRow(
                                item  = item,
                                onTap = { viewModel.onAction(ChatListUiAction.ChatItemTapped(item.id)) },
                            )
                            HorizontalDivider(color = DividerCol, thickness = 0.5.dp)
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick        = { viewModel.onAction(ChatListUiAction.FabTapped) },
                containerColor = Color(0xFFC9A96E),
                contentColor   = Color.White,
                shape          = CircleShape,
                modifier       = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = 20.dp, bottom = 20.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier  = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun ChatTopBar(onBack: () -> Unit) {
    val strings = ChatStrings.forCode(LocalLanguageCode.current)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
    ) {
        IconButton(
            onClick  = onBack,
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Icon(
                imageVector        = Icons.Default.ArrowBack,
                contentDescription = strings.back,
                tint               = TextPrimary,
                modifier           = Modifier.size(22.dp),
            )
        }
        Text(
            text       = "Chat",
            modifier   = Modifier.align(Alignment.Center),
            color      = TextPrimary,
            fontSize   = 26.sp,
            fontStyle  = FontStyle.Italic,
            fontFamily = FontFamily.Serif,
        )
    }
}

// ── Search bar ────────────────────────────────────────────────────────────────

@Composable
private fun ChatSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(SearchBg, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector        = Icons.Default.Search,
            contentDescription = null,
            tint               = TextGray,
            modifier           = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        BasicTextField(
            value         = query,
            onValueChange = onQueryChange,
            modifier      = Modifier.fillMaxWidth(),
            singleLine    = true,
            textStyle     = TextStyle(color = TextPrimary, fontSize = 15.sp),
            cursorBrush   = SolidColor(Terracotta),
            decorationBox = { innerTextField ->
                Box {
                    val strings = ChatStrings.forCode(LocalLanguageCode.current)
                if (query.isEmpty()) {
                        Text(strings.searchChats, color = TextGray, fontSize = 15.sp)
                    }
                    innerTextField()
                }
            },
        )
    }
}

// ── Filter chips ──────────────────────────────────────────────────────────────

@Composable
private fun FilterChipsRow(
    filters: List<ChatFilter>,
    selected: ChatFilter,
    onFilterSelected: (ChatFilter) -> Unit,
) {
    LazyRow(
        contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(filters, key = { it.name }) { filter ->
            val isSelected = filter == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(if (isSelected) Color.White else Color.Transparent)
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = if (isSelected) Color.Transparent else Color(0xFF3A3A46),
                        shape = RoundedCornerShape(50.dp),
                    )
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = filter.label,
                    color      = if (isSelected) Color(0xFF0A0A0A) else TextGray,
                    fontSize   = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun ChatEmptyState() {
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
            color    = TextPrimary.copy(alpha = 0.10f),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text       = "No conversations yet",
            fontSize   = 22.sp,
            fontStyle  = FontStyle.Italic,
            fontFamily = FontFamily.Serif,
            color      = TextPrimary.copy(alpha = 0.38f),
        )
        Text(
            text     = "Messages from your album groups\nwill appear here.",
            fontSize = 13.sp,
            color    = TextPrimary.copy(alpha = 0.20f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

// ── Chat item row ─────────────────────────────────────────────────────────────

@Composable
private fun ChatItemRow(item: ChatItem, onTap: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTap)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChatAvatarBox(item = item)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text       = item.albumName,
                    modifier   = Modifier.weight(1f),
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text     = item.timestamp,
                    color    = if (item.timestamp.contains(":")) Terracotta else TextGray,
                    fontSize = 12.sp,
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ChatPreviewContent(item = item)
                }
                if (item.unreadCount > 0) {
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier         = Modifier
                            .size(22.dp)
                            .background(Color(0xFFE53935), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text       = item.unreadCount.toString(),
                            color      = Color.White,
                            fontSize   = 11.sp,
                            lineHeight = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign  = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

// ── Avatar with online dot ────────────────────────────────────────────────────

@Composable
private fun ChatAvatarBox(item: ChatItem) {
    Box(modifier = Modifier.size(72.dp)) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(item.coverColorArgb))
                .align(Alignment.TopStart),
        )
        if (item.onlineStatus != OnlineStatus.NONE) {
            val dotColor = if (item.onlineStatus == OnlineStatus.ONLINE) OnlineDot else OfflineDot
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.BottomStart)
                    .background(ChatBg, CircleShape)
                    .padding(3.dp)
                    .background(dotColor, CircleShape),
            )
        }
    }
}

// ── Preview variants ──────────────────────────────────────────────────────────

@Composable
private fun ChatPreviewContent(item: ChatItem) {
    when (item.previewType) {
        PreviewType.TEXT -> {
            Text(
                text     = item.previewText,
                color    = TextGray,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        PreviewType.PHOTO -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF4A7A68)),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text     = item.previewText,
                    color    = TextGray,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        PreviewType.VOICE -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Default.Mic,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(15.dp),
                )
                Spacer(Modifier.width(4.dp))
                VoiceWaveform()
                Spacer(Modifier.width(6.dp))
                Text(
                    text     = item.voiceDuration,
                    color    = Color.White.copy(alpha = 0.70f),
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun VoiceWaveform() {
    val heights = listOf(0.4f, 0.7f, 0.5f, 1.0f, 0.8f, 0.6f, 0.9f, 0.7f, 0.5f, 0.8f, 0.6f, 0.4f)
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        heights.forEach { h ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height((4 + 16 * h).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.80f)),
            )
        }
    }
}

