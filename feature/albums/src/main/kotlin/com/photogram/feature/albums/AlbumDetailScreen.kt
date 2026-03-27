package com.photogram.feature.albums

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.photogram.core.designsystem.LocalLanguageCode
import com.photogram.core.designsystem.PhotogramBottomNav
import com.photogram.core.designsystem.PhotogramNavDestination

// ── Palette ───────────────────────────────────────────────────────────────────

private val AlbumBg      = Color(0xFF050505)
private val Terracotta   = Color(0xFFC5663E)
private val WhiteAlpha80 = Color.White.copy(alpha = 0.80f)
private val WhiteAlpha40 = Color.White.copy(alpha = 0.40f)
private val CalDayText   = Color(0xFFB0B0B8)

// ── Tab descriptors ───────────────────────────────────────────────────────────

private data class AlbumTabItem(val tab: AlbumTab, val icon: ImageVector, val label: String)

private val kAlbumTabs = listOf(
    AlbumTabItem(AlbumTab.Photos,   Icons.Default.Collections,  "ALBUM"),
    AlbumTabItem(AlbumTab.Calendar, Icons.Default.DateRange,    "CALENDARIO"),
    AlbumTabItem(AlbumTab.Recaps,   Icons.Default.VideoLibrary, "RECAPS"),
    AlbumTabItem(AlbumTab.Members,  Icons.Default.Event,        "EVENTOS"),
)

// ── Spanish weekday initials (Mon-first, matching reference) ──────────────────

private val kWeekdays = listOf("L", "M", "X", "J", "V", "S", "D")

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun AlbumDetailScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit = {},
    viewModel: AlbumDetailViewModel = hiltViewModel(),
) {
    val uiState           by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState  = remember { SnackbarHostState() }
    val strings            by rememberUpdatedState(AlbumStrings.forCode(LocalLanguageCode.current))

    LaunchedEffect("nav") {
        viewModel.navEvent.collect { route ->
            if (route == "back") onBack() else onNavigate(route)
        }
    }
    LaunchedEffect("snackbar") {
        viewModel.snackbarEvent.collect { key ->
            snackbarHostState.showSnackbar(
                when (key) {
                    AlbumSnackbarKey.ShareComingSoon       -> strings.snackbarShareComingSoon
                    AlbumSnackbarKey.UploadComingSoon      -> strings.snackbarUploadComingSoon
                    AlbumSnackbarKey.PhotoComingSoon       -> strings.snackbarPhotoComingSoon
                    AlbumSnackbarKey.DownloadComingSoon    -> strings.snackbarDownloadComingSoon
                    AlbumSnackbarKey.CreateAlbumComingSoon -> strings.snackbarCreateAlbumComingSoon
                }
            )
        }
    }

    Box(Modifier.fillMaxSize().background(AlbumBg)) {

        // ── Scrollable body ───────────────────────────────────────────────────
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            HeroSection(
                albumTitle    = uiState.albumTitle,
                albumSubtitle = uiState.albumSubtitle,
                onBack        = { viewModel.onAction(AlbumDetailUiAction.BackClicked) },
                onDownload    = { viewModel.onAction(AlbumDetailUiAction.DownloadClicked) },
                onCreateAlbum = { viewModel.onAction(AlbumDetailUiAction.CreateAlbumClicked) },
                strings       = strings,
            )

            AlbumTabRow(
                selected = uiState.selectedTab,
                onSelect = { viewModel.onAction(AlbumDetailUiAction.TabSelected(it)) },
            )

            // 1 dp hairline separator
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF181818)),
            )

            // ── Tab content ───────────────────────────────────────────────────
            when (uiState.selectedTab) {
                AlbumTab.Calendar ->
                    if (uiState.showYearView) {
                        YearView(
                            allMonths = uiState.allMonthsData,
                            onClose   = { viewModel.onAction(AlbumDetailUiAction.YearViewToggled) },
                        )
                    } else {
                        CalendarSection(
                            allMonths      = uiState.allMonthsData,
                            initialPage    = uiState.calendarPage,
                            onPageChanged  = { viewModel.onAction(AlbumDetailUiAction.CalendarPageChanged(it)) },
                            onYearViewOpen = { viewModel.onAction(AlbumDetailUiAction.YearViewToggled) },
                        )
                    }
                else ->
                    PhotoGrid(
                        photos  = uiState.photos,
                        onPhoto = { viewModel.onAction(AlbumDetailUiAction.PhotoTapped(it)) },
                    )
            }

            // Space so bottom nav never overlaps last content row
            Spacer(Modifier.height(140.dp))
        }

        // ── Bottom nav bar ────────────────────────────────────────────────────
        PhotogramBottomNav(
            modifier          = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            activeDestination = PhotogramNavDestination.Gallery,
            onHome    = { viewModel.onAction(AlbumDetailUiAction.HomeNavClicked) },
            onGallery = { viewModel.onAction(AlbumDetailUiAction.GalleryNavClicked) },
            onCreate  = { viewModel.onAction(AlbumDetailUiAction.CreateNavClicked) },
            onChat    = { viewModel.onAction(AlbumDetailUiAction.ChatNavClicked) },
            onProfile = { viewModel.onAction(AlbumDetailUiAction.ProfileNavClicked) },
        )

        // ── Snackbar ──────────────────────────────────────────────────────────
        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 72.dp),
        )
    }
}

// ── Hero ──────────────────────────────────────────────────────────────────────

@Composable
private fun HeroSection(
    albumTitle:    String,
    albumSubtitle: String,
    onBack:        () -> Unit,
    onDownload:    () -> Unit,
    onCreateAlbum: () -> Unit,
    strings:       AlbumStrings,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp),
    ) {
        // ── Base: green wheat field at golden hour ────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.00f to Color(0xFF2A4820),
                        0.16f to Color(0xFF3D6030),
                        0.30f to Color(0xFF789050),
                        0.42f to Color(0xFFC4A040),
                        0.52f to Color(0xFFD4B858),
                        0.63f to Color(0xFF8A6028),
                        0.76f to Color(0xFF3A1E0A),
                        0.88f to Color(0xFF140804),
                        1.00f to Color(0xFF050505),
                    )
                ),
        )

        // ── Sunburst radial glow — upper-center ───────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFEEB0).copy(alpha = 0.68f),
                                Color.Transparent,
                            ),
                            center = Offset(size.width * 0.50f, size.height * 0.36f),
                            radius = size.height * 0.42f,
                        ),
                    )
                },
        )

        // ── Dark vignette — ensures title readability ─────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, AlbumBg),
                    )
                ),
        )

        // ── Back button — gray glass circle, top-left ─────────────────────────
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp, top = 14.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(onClick = onBack)
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = strings.back,
                tint               = Color.White,
                modifier           = Modifier.size(20.dp),
            )
        }

        // ── Top-right actions: download + create album ────────────────────────
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(end = 16.dp, top = 14.dp)
                .align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(onClick = onDownload),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Default.FileDownload,
                    contentDescription = "Download",
                    tint               = Color.White,
                    modifier           = Modifier.size(20.dp),
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(onClick = onCreateAlbum),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Create album",
                    tint               = Color.White,
                    modifier           = Modifier.size(20.dp),
                )
            }
        }

        // ── Title + date — centered at lower portion of hero ──────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text       = albumTitle,
                color      = Color.White,
                fontSize   = 46.sp,
                fontStyle  = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text          = albumSubtitle,
                color         = Color.White.copy(alpha = 0.78f),
                fontSize      = 12.sp,
                fontWeight    = FontWeight.Medium,
                letterSpacing = 3.sp,
            )
        }
    }
}

// ── Tab row ───────────────────────────────────────────────────────────────────

@Composable
private fun AlbumTabRow(selected: AlbumTab, onSelect: (AlbumTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AlbumBg),
    ) {
        kAlbumTabs.forEach { item ->
            val active = item.tab == selected
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(item.tab) }
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector        = item.icon,
                    contentDescription = item.label,
                    tint               = if (active) Color.White else WhiteAlpha40,
                    modifier           = Modifier.size(18.dp),
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text          = item.label,
                    color         = if (active) Color.White else WhiteAlpha40,
                    fontSize      = 9.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(if (active) Color.White else Color.Transparent),
                )
            }
        }
    }
}

// ── Calendar section (swipeable monthly carousel) ─────────────────────────────

@Composable
private fun CalendarSection(
    allMonths:      List<MonthCalendarData>,
    initialPage:    Int,
    onPageChanged:  (Int) -> Unit,
    onYearViewOpen: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { allMonths.size })

    LaunchedEffect(pagerState.settledPage) {
        onPageChanged(pagerState.settledPage)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 22.dp, bottom = 20.dp),
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint               = WhiteAlpha40,
                modifier           = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(2.dp))
            Text(
                text       = "${allMonths[pagerState.currentPage].title} 2025",
                modifier   = Modifier.weight(1f),
                color      = Color.White,
                fontSize   = 22.sp,
                fontStyle  = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Light,
            )
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint               = WhiteAlpha40,
                modifier           = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
            Box(
                modifier         = Modifier
                    .size(36.dp)
                    .clickable(onClick = onYearViewOpen),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Default.GridView,
                    contentDescription = null,
                    tint               = WhiteAlpha40,
                    modifier           = Modifier.size(22.dp),
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        // ── Weekday initials (fixed — same for all months) ────────────────────
        Row(modifier = Modifier.fillMaxWidth()) {
            kWeekdays.forEach { wd ->
                Box(
                    modifier         = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text       = wd,
                        color      = WhiteAlpha40,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign  = TextAlign.Center,
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // ── Swipeable month grid ──────────────────────────────────────────────
        HorizontalPager(
            state    = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            MonthDayGrid(month = allMonths[page])
        }
    }
}

@Composable
private fun MonthDayGrid(month: MonthCalendarData) {
    val totalRows = (month.startOffset + month.days.size + 6) / 7
    val cells: List<CalendarDay?> = buildList {
        repeat(month.startOffset) { add(null) }
        addAll(month.days)
        repeat(totalRows * 7 - month.startOffset - month.days.size) { add(null) }
    }
    Column {
        cells.chunked(7).forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
            ) {
                row.forEach { day ->
                    CalendarDayCell(
                        day      = day,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

// ── Full-year view ─────────────────────────────────────────────────────────────

@Composable
private fun YearView(
    allMonths: List<MonthCalendarData>,
    onClose:   () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 20.dp, bottom = 20.dp),
    ) {
        // Header
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text       = "2025",
                modifier   = Modifier.weight(1f),
                color      = Color.White,
                fontSize   = 26.sp,
                fontStyle  = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Light,
            )
            Box(
                modifier         = Modifier
                    .size(36.dp)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Default.GridView,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(22.dp),
                )
            }
        }

        // 2 months per row
        allMonths.chunked(2).forEachIndexed { rowIdx, pair ->
            if (rowIdx > 0) Spacer(Modifier.height(28.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                pair.forEach { month ->
                    MiniMonth(
                        month    = month,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (pair.size < 2) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MiniMonth(month: MonthCalendarData, modifier: Modifier = Modifier) {
    val totalRows = (month.startOffset + month.days.size + 6) / 7
    val cells: List<CalendarDay?> = buildList {
        repeat(month.startOffset) { add(null) }
        addAll(month.days)
        repeat(totalRows * 7 - month.startOffset - month.days.size) { add(null) }
    }
    Column(modifier = modifier) {
        Text(
            text          = month.title,
            color         = Color.White.copy(alpha = 0.85f),
            fontSize      = 11.sp,
            fontWeight    = FontWeight.Medium,
            letterSpacing = 0.5.sp,
            modifier      = Modifier.padding(bottom = 6.dp),
        )
        // Mini weekday header
        Row(modifier = Modifier.fillMaxWidth()) {
            kWeekdays.forEach { wd ->
                Box(
                    modifier         = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text      = wd,
                        color     = WhiteAlpha40,
                        fontSize  = 7.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        // Mini day grid
        cells.chunked(7).forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp),
            ) {
                row.forEach { day ->
                    MiniCalendarDayCell(day = day, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MiniCalendarDayCell(day: CalendarDay?, modifier: Modifier = Modifier) {
    Box(
        modifier         = modifier.height(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (day == null) return@Box
        if (day.thumbnailColorArgb != null) {
            val outerSize = if (day.hasRing) 19.dp else 17.dp
            val innerSize = if (day.hasRing) 15.dp else 17.dp
            Box(
                modifier         = Modifier
                    .size(outerSize)
                    .clip(CircleShape)
                    .background(if (day.hasRing) Terracotta else Color.Transparent),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier         = Modifier
                        .size(innerSize)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(day.thumbnailColorArgb).copy(alpha = 0.95f),
                                    Color(day.thumbnailColorArgb).copy(alpha = 0.65f),
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text  = day.day.toString(),
                        style = TextStyle(
                            color         = Color.White,
                            fontSize      = 7.sp,
                            lineHeight    = 7.sp,
                            fontWeight    = FontWeight.SemiBold,
                            textAlign     = TextAlign.Center,
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                        ),
                    )
                }
            }
        } else {
            Text(
                text  = day.day.toString(),
                style = TextStyle(
                    color         = CalDayText.copy(alpha = 0.55f),
                    fontSize      = 8.sp,
                    lineHeight    = 8.sp,
                    textAlign     = TextAlign.Center,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
            )
        }
    }
}

// ── Calendar day cell ─────────────────────────────────────────────────────────

@Composable
private fun CalendarDayCell(day: CalendarDay?, modifier: Modifier = Modifier) {
    Box(
        modifier         = modifier.height(52.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (day == null) return@Box

        if (day.thumbnailColorArgb != null) {
            // Circular photo thumbnail — outer ring (terracotta) if hasRing, else no ring
            val outerSize = if (day.hasRing) 46.dp else 42.dp
            val innerSize = if (day.hasRing) 40.dp else 42.dp
            val outerBg   = if (day.hasRing) Terracotta else Color.Transparent

            Box(
                modifier         = Modifier
                    .size(outerSize)
                    .clip(CircleShape)
                    .background(outerBg),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier         = Modifier
                        .size(innerSize)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(day.thumbnailColorArgb).copy(alpha = 0.95f),
                                    Color(day.thumbnailColorArgb).copy(alpha = 0.65f),
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text       = day.day.toString(),
                        color      = Color.White,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        } else {
            // Plain day number
            Text(
                text       = day.day.toString(),
                color      = CalDayText,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Normal,
                textAlign  = TextAlign.Center,
            )
        }
    }
}

// ── Photo grid (used for non-Memories tabs) ───────────────────────────────────

@Composable
private fun PhotoGrid(
    photos:  List<AlbumPhoto>,
    onPhoto: (String) -> Unit,
) {
    if (photos.isEmpty()) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(vertical = 64.dp, horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector        = Icons.Default.AddPhotoAlternate,
                contentDescription = null,
                tint               = Color.White.copy(alpha = 0.10f),
                modifier           = Modifier.size(52.dp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = "Nothing here yet",
                fontSize   = 20.sp,
                fontStyle  = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                color      = Color.White.copy(alpha = 0.38f),
            )
            Text(
                text      = "Add photos to bring this album to life.",
                fontSize  = 13.sp,
                color     = Color.White.copy(alpha = 0.20f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
        return
    }
    val rows = photos.chunked(2)
    Column(
        modifier            = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        rows.forEach { row ->
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                row.forEach { photo ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(3f / 4f)
                            .background(Color(photo.placeholderColor))
                            .clickable { onPhoto(photo.id) },
                    ) {
                        if (photo.hasReaction) {
                            Icon(
                                imageVector        = Icons.Default.Favorite,
                                contentDescription = null,
                                tint               = Terracotta,
                                modifier           = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(6.dp)
                                    .size(16.dp),
                            )
                        }
                    }
                }
                repeat(2 - row.size) {
                    Spacer(Modifier.weight(1f).aspectRatio(3f / 4f))
                }
            }
        }
    }
}

