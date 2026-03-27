package com.photogram.feature.gallery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.photogram.core.designsystem.LocalLanguageCode

// ── Gallery-local palette ─────────────────────────────────────────────────────

private val GalleryBg     = Color(0xFF050505)
private val GalleryAccent = Color(0xFFC5663E)
private val NavPill       = Color(0xFF0B0B0D)
private val Terracotta    = Color(0xFFC5663E)   // AccentWarm
private val FrameWhite    = Color(0xFFFFFFFF)
private val FrameMat      = Color(0xFFF2EBE0)

internal val mockPhotoColors = listOf(
    Color(0xFF8FA87A),
    Color(0xFF7A9FB5),
    Color(0xFFB5A07A),
    Color(0xFFA0887A),
    Color(0xFFB5C5C0),
    Color(0xFF9AB0A0),
    Color(0xFFC0B090),
    Color(0xFFBAAA8A),
    Color(0xFFA0B0A8),
    Color(0xFFD0C8B8),
)

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun GalleryScreen(
    onNavigate: (String) -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: GalleryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.navEvent) {
        viewModel.navEvent.collect { route -> onNavigate(route) }
    }

    GalleryContent(
        uiState  = uiState,
        onAction = viewModel::onAction,
        onBack   = onBack,
    )
}

// ── Content shell — sticky header + sticky month bar + scrollable body ────────
//
// Fix: header and MonthSelector are OUTSIDE the verticalScroll Column.
// Previously GalleryHeader was inside the scroll and the MonthSelector was an
// overlay at padding(top=68dp) — this caused the app bar to scroll away and
// the AnimatedVisibility search bar to fight with the overlay position.

@Composable
private fun GalleryContent(
    uiState:  GalleryUiState,
    onAction: (GalleryUiAction) -> Unit,
    onBack:   () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GalleryBg),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── STICKY: app bar (never scrolls) ──────────────────────────
            GalleryHeader(
                isSearchActive = uiState.isSearchActive,
                searchQuery    = uiState.searchQuery,
                onSearchToggle = { onAction(GalleryUiAction.SearchToggled) },
                onQueryChange  = { onAction(GalleryUiAction.SearchQueryChanged(it)) },
                onBack         = onBack,
            )

            // ── STICKY: month selector (never scrolls) ────────────────────
            MonthSelector(
                months        = uiState.months,
                selectedIndex = uiState.selectedMonthIndex,
                onMonthClick  = { onAction(GalleryUiAction.MonthSelected(it)) },
            )

            // ── SCROLLABLE body ───────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 108.dp),
            ) {
                when {
                    uiState.sections.isEmpty() && uiState.searchQuery.isNotEmpty() ->
                        GalleryEmptySearch(query = uiState.searchQuery)
                    uiState.sections.isEmpty() ->
                        GalleryEmptyState()
                    else -> {
                        uiState.sections.forEach { section ->
                            DaySection(
                                section    = section,
                                onImageTap = { id -> onAction(GalleryUiAction.ImageTapped(id)) },
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // ── OVERLAY: bottom nav pill (always above scroll) ─────────────────
        GalleryBottomNav(
            modifier  = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            onHome    = { onAction(GalleryUiAction.HomeNavClicked) },
            onAlbum   = { onAction(GalleryUiAction.AlbumNavClicked) },
            onRecaps  = { onAction(GalleryUiAction.RecapsNavClicked) },
            onProfile = { onAction(GalleryUiAction.ProfileNavClicked) },
        )
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun GalleryHeader(
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchToggle: () -> Unit,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
) {
    val strings = GalleryStrings.forCode(LocalLanguageCode.current)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GalleryBg),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 4.dp),
        ) {
            // Back button — left, 44dp touch target
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = strings.back,
                    tint               = Color.White,
                    modifier           = Modifier.size(22.dp),
                )
            }

            // Title — centered in the bar
            Text(
                text       = strings.title,
                modifier   = Modifier.align(Alignment.Center),
                color      = Color.White,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Light,
                fontStyle  = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
            )

            // Search icon — right, 44dp touch target
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onSearchToggle),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = if (isSearchActive) strings.closeSearch else strings.openSearch,
                    tint               = Color.White,
                    modifier           = Modifier.size(20.dp),
                )
            }
        }

        // Search bar slides in below the app bar — AnimatedVisibility works correctly
        // here because we are NOT inside a verticalScroll; the sticky column
        // simply grows to accommodate it.
        // Real search bar — BasicTextField with inline placeholder overlay
        AnimatedVisibility(visible = isSearchActive) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF121214))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                BasicTextField(
                    value         = searchQuery,
                    onValueChange = onQueryChange,
                    singleLine    = true,
                    textStyle     = TextStyle(color = Color.White, fontSize = 14.sp),
                    cursorBrush   = SolidColor(Color.White),
                    modifier      = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector        = Icons.Default.Search,
                                contentDescription = null,
                                tint               = Color.White.copy(alpha = 0.40f),
                                modifier           = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text     = strings.searchPlaceholder,
                                        color    = Color.White.copy(alpha = 0.40f),
                                        fontSize = 14.sp,
                                    )
                                }
                                innerTextField()
                            }
                        }
                    },
                )
            }
        }
    }
}

// ── Month selector ────────────────────────────────────────────────────────────

@Composable
private fun MonthSelector(
    months: List<GalleryMonthTab>,
    selectedIndex: Int,
    onMonthClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(GalleryBg)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        months.forEachIndexed { index, tab ->
            MonthTab(
                tab        = tab,
                isSelected = index == selectedIndex,
                onClick    = { onMonthClick(index) },
            )
        }
    }
}

@Composable
private fun MonthTab(
    tab: GalleryMonthTab,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val textAlpha = if (isSelected) 1f else 0.40f
    Column(
        modifier            = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        Text(
            text          = tab.year.toString(),
            color         = Color.White.copy(alpha = textAlpha),
            fontSize      = 10.sp,
            fontWeight    = FontWeight.Normal,
            letterSpacing = 0.3.sp,
        )
        Text(
            text       = tab.month,
            color      = Color.White.copy(alpha = textAlpha),
            fontSize   = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontFamily = if (isSelected) FontFamily.Serif else FontFamily.SansSerif,
        )
        // Orange underline for selected tab — always 2dp tall for layout stability
        Box(
            modifier = Modifier
                .width(26.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(if (isSelected) GalleryAccent else Color.Transparent),
        )
    }
}

// ── Day section ───────────────────────────────────────────────────────────────

@Composable
private fun DaySection(
    section: GalleryDaySection,
    onImageTap: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        DayHeader(monthName = section.monthName, day = section.day)

        val featured   = section.items.firstOrNull() ?: return@Column
        val thumbnails = section.items.drop(1)

        // Full-width featured photo
        FramedPhoto(
            color    = mockPhotoColors.getOrElse(featured.colorIndex) { Color.DarkGray },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clickable { onImageTap(featured.id) },
            framePadding = 5.dp,
            matPadding   = 22.dp,
        )

        // 3-column thumbnail grid
        if (thumbnails.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            thumbnails.chunked(3).forEach { row ->
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    row.forEach { item ->
                        FramedPhoto(
                            color    = mockPhotoColors.getOrElse(item.colorIndex) { Color.DarkGray },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clickable { onImageTap(item.id) },
                            framePadding = 3.dp,
                            matPadding   = 10.dp,
                        )
                    }
                    // Pad incomplete last row
                    repeat(3 - row.size) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f).background(GalleryBg))
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@Composable
private fun DayHeader(monthName: String, day: Int) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 22.dp, bottom = 14.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Orange accent bar
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(48.dp)
                .background(GalleryAccent),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontStyle  = FontStyle.Italic,
                        fontFamily = FontFamily.Serif,
                        fontSize   = 26.sp,
                        fontWeight = FontWeight.Light,
                        color      = Color.White,
                    ),
                ) { append("$monthName ") }
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize   = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White,
                    ),
                ) { append(day.toString()) }
            },
        )
    }
}

// ── Framed photo ──────────────────────────────────────────────────────────────

@Composable
private fun FramedPhoto(
    color: Color,
    modifier: Modifier = Modifier,
    framePadding: Dp = 4.dp,
    matPadding: Dp = 16.dp,
) {
    Box(modifier = modifier.background(FrameWhite)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(framePadding)
                .background(FrameMat),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(matPadding)
                    .background(color),
            )
        }
    }
}

// ── Empty state (no photos at all) ────────────────────────────────────────────

@Composable
private fun GalleryEmptyState() {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(top = 72.dp, bottom = 48.dp, start = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector        = Icons.Default.PhotoLibrary,
            contentDescription = null,
            tint               = Color.White.copy(alpha = 0.10f),
            modifier           = Modifier.size(52.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text       = "Your gallery awaits",
            fontSize   = 20.sp,
            fontStyle  = FontStyle.Italic,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Light,
            color      = Color.White.copy(alpha = 0.40f),
        )
        Text(
            text     = "Photos you upload to your albums\nwill appear here, organized by date.",
            fontSize = 13.sp,
            color    = Color.White.copy(alpha = 0.22f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

// ── Empty search state ────────────────────────────────────────────────────────

@Composable
private fun GalleryEmptySearch(query: String) {
    val strings = GalleryStrings.forCode(LocalLanguageCode.current)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector        = Icons.Default.Search,
            contentDescription = null,
            tint               = Color.White.copy(alpha = 0.20f),
            modifier           = Modifier.size(48.dp),
        )
        Text(
            text       = "${strings.noResultsFor} «$query»",
            color      = Color.White.copy(alpha = 0.50f),
            fontSize   = 14.sp,
            fontWeight = FontWeight.Light,
            fontFamily = FontFamily.Serif,
            fontStyle  = FontStyle.Italic,
        )
        Text(
            text       = strings.trySearchHint,
            color      = Color.White.copy(alpha = 0.30f),
            fontSize   = 12.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.SansSerif,
        )
    }
}

// ── Bottom nav ────────────────────────────────────────────────────────────────

@Composable
private fun GalleryBottomNav(
    modifier:  Modifier = Modifier,
    onHome:    () -> Unit,
    onAlbum:   () -> Unit,
    onRecaps:  () -> Unit,
    onProfile: () -> Unit,
) {
    val strings = GalleryStrings.forCode(LocalLanguageCode.current)
    Row(
        modifier              = modifier
            .background(NavPill)
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        GalleryNavItem(icon = Icons.Default.Home,       label = strings.navHome,   onClick = onHome)
        GalleryNavItem(icon = Icons.Default.CameraRoll, label = strings.navAlbums, onClick = onAlbum)
        GalleryNavItem(icon = Icons.Default.Videocam,   label = strings.navRecaps, onClick = onRecaps)
        // Profile — círculo borde fino + Person + label
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier            = Modifier
                .size(width = 64.dp, height = 52.dp)
                .clickable(onClick = onProfile),
        ) {
            Box(
                modifier         = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color.White.copy(alpha = 0.75f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Default.Person,
                    contentDescription = strings.navProfile,
                    tint               = Color.White.copy(alpha = 0.75f),
                    modifier           = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text     = strings.navProfile,
                color    = Color.White.copy(alpha = 0.75f),
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
private fun GalleryNavItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .size(width = 64.dp, height = 52.dp)
            .clickable(onClick = onClick),
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = Color.White.copy(alpha = 0.75f),
            modifier           = Modifier.size(24.dp),
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text     = label,
            color    = Color.White.copy(alpha = 0.75f),
            fontSize = 11.sp,
        )
    }
}
