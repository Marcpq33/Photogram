package com.photogram.feature.recaps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.photogram.core.designsystem.LocalLanguageCode
import com.photogram.core.designsystem.PhotogramBottomNav
import com.photogram.core.designsystem.PhotogramNavDestination

// ── Local palette ─────────────────────────────────────────────────────────────

private val RecapsBg    = Color(0xFF050505)
private val CtaBlue     = Color(0xFFC9A96E)
private val TextPrimary = Color(0xFFFFFFFF)

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun RecapsScreen(
    onNavigate: (String) -> Unit = {},
    viewModel: RecapsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { route -> onNavigate(route) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RecapsBg),
    ) {
        // ── Scrollable content ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(bottom = 112.dp),
        ) {
            RecapsTopBar(
                onBack      = { viewModel.onAction(RecapsUiAction.BackClicked) },
                onBell      = { viewModel.onAction(RecapsUiAction.SettingsClicked) },
            )
            Spacer(Modifier.height(12.dp))
            if (uiState.featured.isEmpty()) {
                RecapsEmptyState(
                    onCreateClick = { viewModel.onAction(RecapsUiAction.CreateRecapClicked) },
                )
            } else {
                HeroCarousel(
                    items       = uiState.featured,
                    onItemClick = { id -> viewModel.onAction(RecapsUiAction.RecapClicked(id)) },
                )
                Spacer(Modifier.height(28.dp))
                PersonalRecapsSection(
                    items = uiState.personal,
                    onTap = { id -> viewModel.onAction(RecapsUiAction.RecapClicked(id)) },
                )
                Spacer(Modifier.height(20.dp))
                CreateRecapButton(
                    onClick  = { viewModel.onAction(RecapsUiAction.CreateRecapClicked) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        // ── Bottom nav (fixed overlay) ────────────────────────────────────────
        PhotogramBottomNav(
            modifier          = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            activeDestination = PhotogramNavDestination.None,
            onHome    = { viewModel.onAction(RecapsUiAction.HomeNavClicked) },
            onGallery = { viewModel.onAction(RecapsUiAction.GalleryNavClicked) },
            onCreate  = { viewModel.onAction(RecapsUiAction.CreateNavClicked) },
            onChat    = { viewModel.onAction(RecapsUiAction.ChatNavClicked) },
            onProfile = { viewModel.onAction(RecapsUiAction.ProfileNavClicked) },
        )
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun RecapsTopBar(onBack: () -> Unit, onBell: () -> Unit, hasUnread: Boolean = false) {
    val strings = RecapsStrings.forCode(LocalLanguageCode.current)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
    ) {
        IconButton(
            onClick  = onBack,
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = strings.backDesc,
                tint               = TextPrimary,
                modifier           = Modifier.size(22.dp),
            )
        }
        Text(
            text       = strings.title,
            color      = TextPrimary,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Medium,
            fontStyle  = FontStyle.Italic,
            fontFamily = FontFamily.Serif,
            modifier   = Modifier.align(Alignment.Center),
        )
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier         = Modifier.align(Alignment.CenterEnd),
        ) {
            IconButton(onClick = onBell) {
                Icon(
                    imageVector        = Icons.Default.Favorite,
                    contentDescription = strings.settingsDesc,
                    tint               = TextPrimary,
                    modifier           = Modifier.size(22.dp),
                )
            }
            if (hasUnread) {
                Box(
                    modifier = Modifier
                        .padding(top = 9.dp, end = 9.dp)
                        .size(7.dp)
                        .background(Color(0xFFE53935), CircleShape),
                )
            }
        }
    }
}

// ── Hero carousel ─────────────────────────────────────────────────────────────

@Composable
private fun HeroCarousel(
    items: List<RecapItem>,
    onItemClick: (String) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { items.size })

    Column {
        HorizontalPager(
            state          = pagerState,
            contentPadding = PaddingValues(horizontal = 44.dp),
            pageSpacing    = 14.dp,
            modifier       = Modifier.fillMaxWidth(),
        ) { page ->
            val rawOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val pageOffset = rawOffset.absoluteValue.coerceIn(0f, 1f)
            val scale = lerp(start = 0.86f, stop = 1f, fraction = 1f - pageOffset)
            val alpha = lerp(start = 0.50f, stop = 1f, fraction = 1f - pageOffset)

            HeroRecapCard(
                item    = items[page],
                onClick = { onItemClick(items[page].id) },
                modifier = Modifier.graphicsLayer {
                    scaleX        = scale
                    scaleY        = scale
                    this.alpha    = alpha
                    // subtle Z-depth: center card draws on top
                    translationY  = 16.dp.toPx() * pageOffset
                },
            )
        }

        // Page indicator dots
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            repeat(items.size) { index ->
                val active = index == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (active) 8.dp else 5.dp)
                        .clip(CircleShape)
                        .background(
                            if (active) TextPrimary
                            else TextPrimary.copy(alpha = 0.28f)
                        ),
                )
            }
        }
    }
}

// ── Hero card ─────────────────────────────────────────────────────────────────

@Composable
private fun HeroRecapCard(item: RecapItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val strings = RecapsStrings.forCode(LocalLanguageCode.current)
    Box(
        modifier = modifier
            .height(420.dp)
            .clip(RoundedCornerShape(28.dp))
            .clickable(onClick = onClick),
    ) {
        // ── Layer 1: fallback color ───────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(item.thumbnailColor)),
        )
        // ── Layer 2: cinematic image ──────────────────────────────────────────
        if (item.imageUrl != null) {
            AsyncImage(
                model              = item.imageUrl,
                contentDescription = item.title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
            )
        }
        // ── Layer 3: bottom gradient scrim ────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00f to Color.Transparent,
                            0.35f to Color(0x66000000),
                            1.00f to Color(0xF0000000),
                        ),
                    ),
                ),
        )
        // ── Play button centered ──────────────────────────────────────────────
        Box(
            modifier         = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.20f))
                .align(Alignment.Center),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Default.PlayArrow,
                contentDescription = strings.playDesc,
                tint               = TextPrimary,
                modifier           = Modifier.size(40.dp),
            )
        }
        // ── Season label + title — bottom left ────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 28.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (item.seasonLabel.isNotEmpty()) {
                Text(
                    text          = item.seasonLabel,
                    color         = TextPrimary.copy(alpha = 0.75f),
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.SemiBold,
                    letterSpacing = 2.8.sp,
                    fontFamily    = FontFamily.SansSerif,
                )
            }
            Text(
                text       = item.title,
                color      = TextPrimary,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp,
                fontFamily = FontFamily.SansSerif,
            )
        }
    }
}

// ── Personal recaps section ───────────────────────────────────────────────────

@Composable
private fun PersonalRecapsSection(items: List<RecapItem>, onTap: (String) -> Unit) {
    val strings = RecapsStrings.forCode(LocalLanguageCode.current)
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text       = strings.yourPersonalRecaps,
            color      = TextPrimary,
            fontSize   = 20.sp,
            fontStyle  = FontStyle.Italic,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(14.dp))
        val rows = items.chunked(2)
        rows.forEachIndexed { index, row ->
            if (index > 0) Spacer(Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier              = Modifier.fillMaxWidth(),
            ) {
                row.forEach { item ->
                    PersonalRecapCard(
                        item     = item,
                        onClick  = { onTap(item.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size < 2) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PersonalRecapCard(
    item: RecapItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(0.82f)
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
    ) {
        // ── Fallback color ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(item.thumbnailColor)),
        )
        // ── Real image ────────────────────────────────────────────────────────
        if (item.imageUrl != null) {
            AsyncImage(
                model              = item.imageUrl,
                contentDescription = item.title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
            )
        }
        // ── Bottom gradient ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xCC000000)),
                    ),
                ),
        )
        // ── Title + photo count ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 10.dp, bottom = 10.dp, end = 28.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text       = item.title,
                color      = TextPrimary,
                fontSize   = 13.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif,
            )
            if (item.photoCount > 0) {
                Text(
                    text          = "${item.photoCount} PHOTOS",
                    color         = TextPrimary.copy(alpha = 0.58f),
                    fontSize      = 10.sp,
                    letterSpacing = 1.0.sp,
                    fontFamily    = FontFamily.SansSerif,
                )
            }
        }
        // ── Sparkle badge — top right ─────────────────────────────────────────
        Text(
            text     = "✦",
            color    = Color.White.copy(alpha = 0.72f),
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(9.dp),
        )
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun RecapsEmptyState(onCreateClick: () -> Unit) {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 52.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier         = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Default.PlayArrow,
                contentDescription = null,
                tint               = Color.White.copy(alpha = 0.25f),
                modifier           = Modifier.size(36.dp),
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text       = "Your story, not yet told",
            color      = TextPrimary.copy(alpha = 0.45f),
            fontSize   = 22.sp,
            fontStyle  = FontStyle.Italic,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text      = "Recaps are compiled automatically\nfrom your album memories.",
            color     = TextPrimary.copy(alpha = 0.25f),
            fontSize  = 13.sp,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        CreateRecapButton(
            onClick  = onCreateClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ── Create recap CTA ──────────────────────────────────────────────────────────

@Composable
private fun CreateRecapButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val strings = RecapsStrings.forCode(LocalLanguageCode.current)
    Box(
        modifier         = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .background(CtaBlue)
            .clickable(onClick = onClick)
            .padding(vertical = 17.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text          = strings.createNewRecap,
            color         = TextPrimary,
            fontSize      = 16.sp,
            fontWeight    = FontWeight.SemiBold,
            fontFamily    = FontFamily.SansSerif,
            letterSpacing = 0.2.sp,
        )
    }
}

