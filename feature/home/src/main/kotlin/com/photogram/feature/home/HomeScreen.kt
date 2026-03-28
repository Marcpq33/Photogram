package com.photogram.feature.home

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.photogram.core.designsystem.LocalLanguageCode
import com.photogram.core.designsystem.PhotogramBottomNav
import com.photogram.core.designsystem.PhotogramNavDestination
import com.photogram.core.designsystem.PhotogramTheme

// ── Home-local palette ──────────────────────────────────────────────────────────
private val HomeBg     = Color(0xFF050505)
private val Gold       = Color(0xFFC9A96E)
private val Terracotta = Color(0xFFC5663E)
private val AccentBlue = Color(0xFFC9A96E)

// ── Entry point ─────────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit = {},
    // Set to true by PhotogramApp when Camera confirms a story was published.
    // Triggers activateMyStory() exactly once; caller must clear the flag via onStoryPublishedConsumed.
    storyPublished: Boolean = false,
    onStoryPublishedConsumed: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(storyPublished) {
        if (storyPublished) {
            viewModel.onAction(HomeUiAction.StoryPublished)
            onStoryPublishedConsumed()
        }
    }

    LaunchedEffect(viewModel.navEvent) {
        viewModel.navEvent.collect { route -> onNavigate(route) }
    }
    val currentStrings by rememberUpdatedState(HomeStrings.forCode(LocalLanguageCode.current))
    LaunchedEffect(viewModel.snackbarEvent) {
        viewModel.snackbarEvent.collect { key ->
            val message = when (key) {
                HomeSnackbarKey.AlbumComingSoon    -> currentStrings.snackbarAlbumComingSoon
                HomeSnackbarKey.FeaturedComingSoon -> currentStrings.snackbarFeaturedComingSoon
                HomeSnackbarKey.SearchComingSoon   -> currentStrings.snackbarSearchComingSoon
                HomeSnackbarKey.LinkCopied         -> "Link copied"
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    PhotogramTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize()) {
            HomeContent(uiState = uiState, onAction = viewModel::onAction)
            SnackbarHost(
                hostState = snackbarHostState,
                modifier  = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 108.dp),
            )
            if (uiState.showCameraSheet) {
                CameraSheet(onAction = viewModel::onAction)
            }
            if (uiState.showInviteWelcome) {
                val context = LocalContext.current
                InviteWelcomeOverlay(
                    onDismiss     = { viewModel.onAction(HomeUiAction.InviteWelcomeDismissed) },
                    onWhatsApp    = {
                        viewModel.onAction(HomeUiAction.InviteSharedOnce)
                        shareViaWhatsApp(context, INVITE_LINK_PROVISIONAL)
                    },
                    onInstagram   = {
                        viewModel.onAction(HomeUiAction.InviteSharedOnce)
                        shareViaInstagram(context, INVITE_LINK_PROVISIONAL)
                    },
                    onFacebook    = {
                        viewModel.onAction(HomeUiAction.InviteSharedOnce)
                        shareViaFacebook(context, INVITE_LINK_PROVISIONAL)
                    },
                    onCopyLink    = {
                        copyToClipboard(context, INVITE_LINK_PROVISIONAL)
                        viewModel.onAction(HomeUiAction.InviteLinkCopied)
                    },
                    onMoreOptions = {
                        viewModel.onAction(HomeUiAction.InviteSharedOnce)
                        shareGeneric(context, INVITE_LINK_PROVISIONAL)
                    },
                )
            }
        }
    }
}

@Composable
private fun HomeContent(uiState: HomeUiState, onAction: (HomeUiAction) -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(HomeBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 160.dp),
        ) {
            HomeHeader(
                unreadCount   = uiState.unreadNotifications,
                onBellClick   = { onAction(HomeUiAction.BellClicked) },
                onSearchClick = { onAction(HomeUiAction.SearchClicked) },
            )
            Spacer(Modifier.height(12.dp))
            StoriesRow(
                stories       = uiState.stories,
                onStoryClick  = { onAction(HomeUiAction.StoryClicked(it)) },
                onAddNewClick = { onAction(HomeUiAction.AddNewStoryClicked) },
            )
            Spacer(Modifier.height(28.dp))
            if (uiState.featuredMemory != null) {
                FeaturedMemoryCard(
                    memory   = uiState.featuredMemory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick  = { onAction(HomeUiAction.FeaturedMemoryClicked) },
                )
            } else {
                FeaturedMemoryPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }
            Spacer(Modifier.height(36.dp))
            AlbumsSection(
                albums       = uiState.albums,
                onAlbumClick = { onAction(HomeUiAction.AlbumClicked(it)) },
            )
            Spacer(Modifier.height(20.dp))
        }
        PhotogramBottomNav(
            modifier          = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            activeDestination = PhotogramNavDestination.Home,
            unreadMessages    = uiState.unreadMessages,
            onHome    = { onAction(HomeUiAction.HomeNavClicked) },
            onGallery = { onAction(HomeUiAction.GalleryNavClicked) },
            onCreate  = { onAction(HomeUiAction.CreateClicked) },
            onChat    = { onAction(HomeUiAction.ChatNavClicked) },
            onProfile = { onAction(HomeUiAction.ProfileNavClicked) },
            avatarContent = if (uiState.currentUserAvatarUrl != null) {
                {
                    AsyncImage(
                        model              = uiState.currentUserAvatarUrl,
                        contentDescription = "Profile",
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier
                            .size(38.dp)
                            .clip(CircleShape),
                    )
                }
            } else null,
        )
    }
}

// ── Header ──────────────────────────────────────────────────────────────────────

@Composable
private fun HomeHeader(
    unreadCount: Int,
    onBellClick: () -> Unit,
    onSearchClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        // Wordmark — italic cursive, left-anchored
        Text(
            text       = "Photogram",
            fontFamily = FontFamily.Cursive,
            fontStyle  = FontStyle.Italic,
            fontWeight = FontWeight.Normal,
            fontSize   = 26.sp,
            color      = Color.White,
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            IconButton(onClick = onSearchClick, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector        = Icons.Default.Search,
                    contentDescription = "Search",
                    tint               = Color.White.copy(alpha = 0.80f),
                    modifier           = Modifier.size(21.dp),
                )
            }
            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = onBellClick, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector        = Icons.Default.Favorite,
                        contentDescription = "Notifications",
                        tint               = Color.White.copy(alpha = 0.80f),
                        modifier           = Modifier.size(21.dp),
                    )
                }
                if (unreadCount > 0) {
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
}

// ── Stories ──────────────────────────────────────────────────────────────────────

@Composable
private fun StoriesRow(
    stories: List<HomeStory>,
    onStoryClick: (String) -> Unit,
    onAddNewClick: () -> Unit,
) {
    LazyRow(
        contentPadding        = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(stories, key = { it.id }) { story ->
            StoryBubble(
                story          = story,
                onClick        = {
                    if (story.isAddNew) onAddNewClick()
                    else onStoryClick(story.id)
                },
                onAddMoreClick = if (story.canAddMore) onAddNewClick else null,
            )
        }
    }
}

@Composable
private fun StoryBubble(
    story: HomeStory,
    onClick: () -> Unit,
    /** Non-null only when story.canAddMore == true. Tapping the badge navigates to Camera. */
    onAddMoreClick: (() -> Unit)? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier            = Modifier.clickable(onClick = onClick),
    ) {
        if (story.isAddNew) {
            // Dashed ring with "+" — NEW story slot (no story yet)
            Box(
                modifier         = Modifier.size(64.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2f - 2.dp.toPx()
                    drawCircle(
                        color  = Color.White.copy(alpha = 0.38f),
                        radius = radius,
                        style  = Stroke(
                            width      = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(9f, 6f)),
                        ),
                    )
                }
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(22.dp),
                )
            }
        } else {
            // 72dp outer box  →  58dp avatar  →  7dp total breathing room.
            // Ring is drawn via Canvas stroke (not filled circles) — genuinely thin.
            // Unseen: thin sweep-gradient ring + subtle outer glow.
            // Seen:   same thin ring geometry, muted white — consistent shape.
            Box(
                modifier         = Modifier.size(72.dp),
                contentAlignment = Alignment.Center,
            ) {
                // Avatar — 58dp leaves ~3.5dp gap between it and the ring stroke
                if (story.imageUrl != null) {
                    AsyncImage(
                        model              = story.imageUrl,
                        contentDescription = story.label,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier
                            .size(58.dp)
                            .clip(CircleShape),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Color(story.coverColorArgb)),
                    )
                }

                // Thin ring drawn via Canvas — proportions match reference
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokePx  = 1.5.dp.toPx()
                    // Inset by half stroke so the stroke doesn't clip at the edge
                    val ringRadius = size.minDimension / 2f - strokePx / 2f - 0.5.dp.toPx()

                    if (!story.isSeen) {
                        // Outer glow: wider, very low alpha — premium halo
                        drawCircle(
                            color  = Color(0xFFC9A96E).copy(alpha = 0.12f),
                            radius = ringRadius + 2.5.dp.toPx(),
                            style  = Stroke(width = 5.dp.toPx()),
                        )
                        // Main ring: thin golden sweep gradient
                        drawCircle(
                            brush  = Brush.sweepGradient(
                                listOf(
                                    Color(0xFF6B4A18),
                                    Color(0xFFC9A96E),
                                    Color(0xFFF5E4A8),
                                    Color(0xFFE8C88A),
                                    Color(0xFFC9A96E),
                                    Color(0xFF6B4A18),
                                )
                            ),
                            radius = ringRadius,
                            style  = Stroke(width = strokePx),
                        )
                    } else {
                        // Seen: identical thin ring, muted — no glow
                        drawCircle(
                            color  = Color.White.copy(alpha = 0.20f),
                            radius = ringRadius,
                            style  = Stroke(width = 1.dp.toPx()),
                        )
                    }
                }

                // "+" badge — smaller and less invasive than before
                if (onAddMoreClick != null) {
                    Box(
                        modifier         = Modifier
                            .align(Alignment.BottomEnd)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(HomeBg)
                            .border(1.dp, Gold, CircleShape)
                            .clickable(onClick = onAddMoreClick),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Add,
                            contentDescription = "Add to story",
                            tint               = Gold,
                            modifier           = Modifier.size(10.dp),
                        )
                    }
                }
            }
        }
        Text(
            text          = story.label,
            fontFamily    = FontFamily.SansSerif,
            fontWeight    = FontWeight.Medium,
            fontSize      = 10.sp,
            letterSpacing = 0.6.sp,
            color         = if (story.isSeen) Color.White.copy(alpha = 0.38f)
                            else Color.White.copy(alpha = 0.70f),
        )
    }
}

// ── Featured Memory ──────────────────────────────────────────────────────────────

@Composable
private fun FeaturedMemoryCard(
    memory: HomeFeaturedMemory,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val strings = HomeStrings.forCode(LocalLanguageCode.current)

    Box(
        modifier = modifier
            .aspectRatio(1.52f)
            .clip(RoundedCornerShape(22.dp))
            .border(0.7.dp, Gold.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
    ) {
        // ── Layer 1: dark forest gradient (loading state / offline fallback)
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.00f to Color(0xFF1C3020),
                        0.40f to Color(0xFF102016),
                        0.75f to Color(0xFF071510),
                        1.00f to Color(0xFF030C06),
                    )
                )
            )
        )

        // ── Layer 2: actual photo (Unsplash)
        if (memory.imageUrl != null) {
            AsyncImage(
                model              = memory.imageUrl,
                contentDescription = null,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
            )
        }

        // ── Layer 3: warm amber highlight at top — enhances "light through canopy"
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.00f to Color(0x14C9A96E),
                        0.30f to Color.Transparent,
                    )
                )
            )
        )

        // ── Layer 4: bottom scrim for text legibility
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.30f to Color.Transparent,
                        1.00f to Color(0xCC000000),
                    )
                )
            )
        )

        // ── Layer 5: editorial content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 22.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text       = memory.title,
                fontFamily = FontFamily.Cursive,
                fontStyle  = FontStyle.Italic,
                fontSize   = 22.sp,
                lineHeight = 28.sp,
                color      = Color.White.copy(alpha = 0.97f),
            )
            Text(
                text       = memory.subtitle,
                fontFamily = FontFamily.SansSerif,
                fontSize   = 13.sp,
                lineHeight = 19.sp,
                color      = Color.White.copy(alpha = 0.70f),
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                // RELIVE pill
                Text(
                    text          = strings.relive,
                    fontWeight    = FontWeight.SemiBold,
                    fontSize      = 11.sp,
                    letterSpacing = 1.8.sp,
                    color         = Color.White,
                    modifier      = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(AccentBlue)
                        .padding(horizontal = 22.dp, vertical = 10.dp),
                )
                // Date
                Text(
                    text          = memory.date,
                    fontFamily    = FontFamily.SansSerif,
                    fontSize      = 10.sp,
                    letterSpacing = 1.2.sp,
                    color         = Color.White.copy(alpha = 0.55f),
                )
            }
        }
    }
}

// ── Featured Memory placeholder (real user, no data yet) ─────────────────────────

@Composable
private fun FeaturedMemoryPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1.52f)
            .clip(RoundedCornerShape(22.dp))
            .border(0.7.dp, Gold.copy(alpha = 0.18f), RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.00f to Color(0xFF1C3020),
                        0.40f to Color(0xFF102016),
                        0.75f to Color(0xFF071510),
                        1.00f to Color(0xFF030C06),
                    )
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier            = Modifier.padding(horizontal = 32.dp),
        ) {
            Text(
                text       = "Your memories\nwill live here",
                fontFamily = FontFamily.Cursive,
                fontStyle  = FontStyle.Italic,
                fontSize   = 22.sp,
                lineHeight = 30.sp,
                color      = Color.White.copy(alpha = 0.30f),
                textAlign  = TextAlign.Center,
            )
            Text(
                text      = "Create your first album to get started",
                fontSize  = 12.sp,
                color     = Color.White.copy(alpha = 0.16f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

// ── Albums section ───────────────────────────────────────────────────────────────

@Composable
private fun AlbumsSection(albums: List<HomeAlbum>, onAlbumClick: (String) -> Unit) {
    val strings = HomeStrings.forCode(LocalLanguageCode.current)
    val (fullWidth, grid) = remember(albums) { albums.partition { it.isFullWidth } }

    Column(
        modifier            = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // "YOUR ALBUMS" with fine divider
        Row(
            modifier          = Modifier.fillMaxWidth().padding(bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text          = strings.yourAlbums,
                fontFamily    = FontFamily.SansSerif,
                fontWeight    = FontWeight.SemiBold,
                fontSize      = 10.sp,
                letterSpacing = 2.sp,
                color         = Color.White,
            )
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(0.5.dp)
                    .background(Color.White.copy(alpha = 0.14f))
            )
        }

        if (albums.isEmpty()) {
            AlbumsEmptyState()
        } else {
            grid.chunked(2).forEach { pair ->
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    pair.forEach { album ->
                        AlbumCard(
                            album    = album,
                            onClick  = { onAlbumClick(album.id) },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(0.76f),
                        )
                    }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            fullWidth.forEach { album ->
                AlbumCard(
                    album    = album,
                    onClick  = { onAlbumClick(album.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp),
                )
            }
        }
    }
}

@Composable
private fun AlbumsEmptyState() {
    // Two ghost cards matching real AlbumCard proportions — preserves grid structure
    // without surfacing fake content.
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(2) {
            AlbumGhostCard(modifier = Modifier.weight(1f).aspectRatio(0.76f))
        }
    }
}

@Composable
private fun AlbumGhostCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(0.7.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(18.dp)),
    )
}

@Composable
private fun AlbumCard(album: HomeAlbum, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val coverTop    = Color(album.coverColorArgb)
    val coverBottom = if (album.coverColorArgb2 != 0L) Color(album.coverColorArgb2) else coverTop

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
    ) {
        // ── Layer 1: gradient fallback (loading / offline)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(coverTop, coverBottom)))
        )

        // ── Layer 2: photo
        if (album.imageUrl != null) {
            AsyncImage(
                model              = album.imageUrl,
                contentDescription = album.name,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
            )
        }

        // ── Layer 3: bottom scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.30f to Color.Transparent,
                            1.00f to Color(0xBB000000),
                        )
                    )
                )
        )

        // ── Top-left: genre tag + new count
        Column(
            modifier            = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (album.tagLabel.isNotEmpty()) {
                AlbumTagChip(label = album.tagLabel)
            }
            if (album.newCount > 0) {
                Text(
                    text          = "${album.newCount} NEW",
                    fontWeight    = FontWeight.Medium,
                    fontSize      = 9.sp,
                    letterSpacing = 0.4.sp,
                    color         = Color.White.copy(alpha = 0.90f),
                )
            }
        }

        // ── Bottom-left: album name
        Text(
            text       = album.name,
            fontFamily = FontFamily.Serif,
            fontStyle  = FontStyle.Italic,
            fontSize   = 15.sp,
            lineHeight = 19.sp,
            color      = Color.White,
            modifier   = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 14.dp, end = 40.dp),
        )
    }
}

@Composable
private fun AlbumTagChip(label: String) {
    Text(
        text          = label,
        fontWeight    = FontWeight.Bold,
        fontSize      = 8.sp,
        letterSpacing = 0.8.sp,
        color         = Color(0xFFC9A96E),
        modifier      = Modifier
            .background(Color(0x52C9A96E), RoundedCornerShape(4.dp))
            .border(1.dp, Color(0xFFC9A96E).copy(alpha = 0.75f), RoundedCornerShape(4.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp),
    )
}


// ── Camera sheet (unchanged) ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraSheet(onAction: (HomeUiAction) -> Unit) {
    val strings = HomeStrings.forCode(LocalLanguageCode.current)
    ModalBottomSheet(
        onDismissRequest = { onAction(HomeUiAction.CameraSheetDismissed) },
        sheetState       = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor   = Color(0xFF0B0B0D),
        dragHandle       = { CreateSheetHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text          = strings.create,
                color         = Color.White.copy(alpha = 0.30f),
                fontSize      = 10.sp,
                fontWeight    = FontWeight.SemiBold,
                letterSpacing = 3.sp,
                modifier      = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 2.dp),
            )
            CreateCardWide(
                icon        = Icons.Default.AutoAwesome,
                label       = strings.story,
                subtitle    = strings.shareWithGroup,
                gradient    = Brush.verticalGradient(listOf(Color(0xFF1E1108), Color(0xFF110B05))),
                accentColor = Terracotta,
                onClick     = { onAction(HomeUiAction.CameraStoryTapped) },
            )
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CreateCardSquare(
                    modifier    = Modifier.weight(1f),
                    icon        = Icons.Default.PhotoLibrary,
                    label       = strings.gallery,
                    subtitle    = strings.cameraRoll,
                    gradient    = Brush.verticalGradient(listOf(Color(0xFF0C1425), Color(0xFF08101E))),
                    accentColor = Color(0xFF5B9BD5),
                    onClick     = { onAction(HomeUiAction.CameraGalleryTapped) },
                )
                CreateCardSquare(
                    modifier    = Modifier.weight(1f),
                    icon        = Icons.Default.PhotoAlbum,
                    label       = strings.album,
                    subtitle    = strings.organizeShare,
                    gradient    = Brush.verticalGradient(listOf(Color(0xFF181410), Color(0xFF100E0B))),
                    accentColor = Gold,
                    onClick     = { onAction(HomeUiAction.CameraAlbumTapped) },
                )
            }
        }
    }
}

@Composable
private fun CreateSheetHandle() {
    Box(
        modifier         = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 4.dp)
                .clip(RoundedCornerShape(50))
                .background(Terracotta.copy(alpha = 0.45f)),
        )
    }
}

@Composable
private fun CreateCardWide(
    icon: ImageVector,
    label: String,
    subtitle: String,
    gradient: Brush,
    accentColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(gradient)
            .border(1.dp, accentColor.copy(alpha = 0.18f), RoundedCornerShape(28.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Box(
                modifier         = Modifier.size(52.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(26.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = label,    color = Color.White,                     fontSize = 18.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.2.sp)
                Text(text = subtitle, color = Color.White.copy(alpha = 0.42f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun CreateCardSquare(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    subtitle: String,
    gradient: Brush,
    accentColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(gradient)
            .border(1.dp, accentColor.copy(alpha = 0.18f), RoundedCornerShape(28.dp))
            .clickable(onClick = onClick)
            .padding(20.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Box(
                modifier         = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = label,    color = Color.White,                     fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, color = Color.White.copy(alpha = 0.42f), fontSize = 11.sp)
            }
        }
    }
}

// ── Invite Welcome Overlay ───────────────────────────────────────────────────────
// PROVISIONAL: no backend invite system yet. Replace with real dynamic link when available.
private const val INVITE_LINK_PROVISIONAL = "https://photogram.app/invite"

@Composable
private fun InviteWelcomeOverlay(
    onDismiss: () -> Unit,
    onWhatsApp: () -> Unit,
    onInstagram: () -> Unit,
    onFacebook: () -> Unit,
    onCopyLink: () -> Unit,
    onMoreOptions: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties       = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF111113))
                .border(0.7.dp, Gold.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                .padding(24.dp),
        ) {
            // Close button
            IconButton(
                onClick  = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp),
            ) {
                Icon(
                    imageVector        = Icons.Default.Close,
                    contentDescription = "Close",
                    tint               = Color.White.copy(alpha = 0.45f),
                    modifier           = Modifier.size(18.dp),
                )
            }

            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text       = "Welcome to Photogram",
                    fontFamily = FontFamily.Cursive,
                    fontStyle  = FontStyle.Italic,
                    fontSize   = 22.sp,
                    color      = Color.White,
                    textAlign  = TextAlign.Center,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text       = "Invite the people you love to share memories together.",
                    fontSize   = 13.sp,
                    lineHeight = 19.sp,
                    color      = Color.White.copy(alpha = 0.60f),
                    textAlign  = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                // WhatsApp
                InviteActionRow(
                    label       = "Invite via WhatsApp",
                    accentColor = Color(0xFF25D366),
                    onClick     = onWhatsApp,
                    leadingIcon = {
                        Box(
                            modifier         = Modifier.size(34.dp).clip(CircleShape).background(Color(0xFF25D366)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter            = painterResource(R.drawable.ic_whatsapp),
                                contentDescription = null,
                                tint               = Color.White,
                                modifier           = Modifier.size(20.dp),
                            )
                        }
                    },
                )

                Spacer(Modifier.height(8.dp))

                // Instagram
                InviteActionRow(
                    label       = "Invite via Instagram",
                    accentColor = Color(0xFFE1306C),
                    onClick     = onInstagram,
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF833AB4), Color(0xFFE1306C), Color(0xFFF77737)),
                                    ),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter            = painterResource(R.drawable.ic_instagram),
                                contentDescription = null,
                                tint               = Color.White,
                                modifier           = Modifier.size(20.dp),
                            )
                        }
                    },
                )

                Spacer(Modifier.height(8.dp))

                // Facebook
                InviteActionRow(
                    label       = "Invite via Facebook",
                    accentColor = Color(0xFF1877F2),
                    onClick     = onFacebook,
                    leadingIcon = {
                        Box(
                            modifier         = Modifier.size(34.dp).clip(CircleShape).background(Color(0xFF1877F2)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter            = painterResource(R.drawable.ic_facebook),
                                contentDescription = null,
                                tint               = Color.White,
                                modifier           = Modifier.size(20.dp),
                            )
                        }
                    },
                )

                Spacer(Modifier.height(8.dp))

                InviteActionRow(
                    label       = "Copy invite link",
                    accentColor = Gold,
                    onClick     = onCopyLink,
                    leadingIcon = {
                        Box(
                            modifier         = Modifier.size(34.dp).clip(CircleShape).background(Gold.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = Gold, modifier = Modifier.size(16.dp))
                        }
                    },
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text          = "More options",
                    fontSize      = 13.sp,
                    fontWeight    = FontWeight.Medium,
                    letterSpacing = 0.3.sp,
                    color         = Color.White.copy(alpha = 0.40f),
                    modifier      = Modifier
                        .clickable(onClick = onMoreOptions)
                        .padding(vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun InviteActionRow(
    label: String,
    accentColor: Color,
    onClick: () -> Unit,
    leadingIcon: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(accentColor.copy(alpha = 0.10f))
            .border(1.dp, accentColor.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        leadingIcon()
        Text(
            text       = label,
            fontSize   = 14.sp,
            fontWeight = FontWeight.Medium,
            color      = Color.White.copy(alpha = 0.88f),
        )
    }
}

/**
 * Placeholder brand icon — solid colored circle with a letter initial.
 * Replace the Box content with:
 *   Icon(painter = painterResource(R.drawable.ic_<brand>), contentDescription = null, tint = Color.White)
 * once official brand SVG assets (ic_whatsapp.xml, ic_facebook.xml) are added to the drawable resources.
 */
@Composable
private fun BrandIconCircle(containerColor: Color, letter: String) {
    Box(
        modifier         = Modifier.size(34.dp).clip(CircleShape).background(containerColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = letter, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

/**
 * Placeholder Instagram brand icon — gradient circle (purple → red → orange).
 * Replace Box content with:
 *   Icon(painter = painterResource(R.drawable.ic_instagram), contentDescription = null, tint = Color.White)
 * once ic_instagram.xml official SVG is added to drawable resources.
 */
@Composable
private fun BrandIconCircleGradient() {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF833AB4), Color(0xFFE1306C), Color(0xFFF77737)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "in", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

// ── Share / clipboard utilities ──────────────────────────────────────────────────

private fun shareViaWhatsApp(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        setPackage("com.whatsapp")
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        shareGeneric(context, text)   // WhatsApp not installed — fall back to chooser
    }
}

private fun shareViaInstagram(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        setPackage("com.instagram.android")
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        shareGeneric(context, text)   // Instagram not installed — fall back to chooser
    }
}

private fun shareViaFacebook(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        setPackage("com.facebook.katana")
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        shareGeneric(context, text)   // Facebook not installed — fall back to chooser
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("invite_link", text))
}

private fun shareGeneric(context: Context, text: String) {
    val chooser = Intent.createChooser(
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        },
        null,
    )
    context.startActivity(chooser)
}

// Kept for potential future use — not called from album cards in current design
@Suppress("UnusedPrivateMember")
@Composable
private fun PremiumBadge(modifier: Modifier = Modifier) {
    Row(
        modifier              = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x33000000))
            .border(0.5.dp, Gold.copy(alpha = 0.50f), RoundedCornerShape(12.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Gold, modifier = Modifier.size(8.dp))
        Text(text = "Premium", fontSize = 9.sp, color = Gold)
    }
}
