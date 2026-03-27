package com.photogram.feature.profile

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.photogram.core.designsystem.PhotogramTheme

// ── Palette ───────────────────────────────────────────────────────────────────
private val ProfileBgTop  = Color(0xFF1A120A)
private val ProfileBgMid  = Color(0xFF0F0A05)
private val ProfileDarkBg = Color(0xFF050505)
private val ProfileGold   = Color(0xFFC9A96E)
private val AvatarBg      = Color(0xFF3A2518)
private val SettingsBg    = Color(0x33FFFFFF)
private val StatCardBg    = Color(0xFF0B0B0D)
private val TextMuted     = Color(0x66FFFFFF)
private val PotBody       = Color(0xFF9B5A38)
private val PotRimColor   = Color(0xFFB5704A)
private val StemColor     = Color(0xFF4A7A44)
private val BudOuter      = Color(0xFFF5EDE0)
private val BudInner      = Color(0xFFD8C8B8)

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun ProfileScreen(
    onNavigate: (String) -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.navEvent) {
        viewModel.navEvent.collect { route -> onNavigate(route) }
    }

    PhotogramTheme(darkTheme = true) {
        if (uiState.showNewPost) {
            NewPostContent(
                mediaUri        = uiState.newPostMediaUri,
                onClose         = { viewModel.onAction(ProfileUiAction.NewPostDismissed) },
                onCameraClicked = { viewModel.onAction(ProfileUiAction.NewPostCameraClicked) },
                onMediaSelected = { uri -> viewModel.onAction(ProfileUiAction.NewPostMediaSelected(uri)) },
            )
        } else {
            ProfileContent(uiState = uiState, onAction = viewModel::onAction)
        }
    }
}

// ── Root layout ───────────────────────────────────────────────────────────────

@Composable
private fun ProfileContent(uiState: ProfileUiState, onAction: (ProfileUiAction) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfileDarkBg),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 160.dp),
        ) {
            item { HeroSection(uiState = uiState) }
            item { Spacer(Modifier.height(20.dp)) }
            item {
                StatsRow(
                    totalPhotos = uiState.totalPhotos,
                    albumsCount = uiState.albumsCount,
                    daysStreak  = uiState.daysStreak,
                    modifier    = Modifier.padding(horizontal = 16.dp),
                )
            }
            item { Spacer(Modifier.height(28.dp)) }
            item {
                RecapsSection(
                    recaps    = uiState.recaps,
                    onViewAll = { /* recaps milestone */ },
                    onRecap   = { onAction(ProfileUiAction.RecapClicked(it)) },
                )
            }
            item { Spacer(Modifier.height(28.dp)) }
            item {
                AlbumsSection(
                    albums  = uiState.albums,
                    onAlbum = { onAction(ProfileUiAction.AlbumClicked(it)) },
                )
            }
        }

        // Fixed: settings gear
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 52.dp, end = 20.dp)
                .size(46.dp)
                .clip(CircleShape)
                .background(SettingsBg)
                .clickable { onAction(ProfileUiAction.SettingsClicked) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Default.Settings,
                contentDescription = "Settings",
                tint               = Color.White.copy(alpha = 0.80f),
                modifier           = Modifier.size(22.dp),
            )
        }

        // Fixed: bottom nav
        PhotogramBottomNav(
            modifier          = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            activeDestination = PhotogramNavDestination.Profile,
            onHome    = { onAction(ProfileUiAction.HomeNavClicked) },
            onGallery = { onAction(ProfileUiAction.GalleryNavClicked) },
            onCreate  = { onAction(ProfileUiAction.CreateClicked) },
            onChat    = { onAction(ProfileUiAction.ChatNavClicked) },
            onProfile = {},
        )
    }
}

// ── Hero ──────────────────────────────────────────────────────────────────────

@Composable
private fun HeroSection(uiState: ProfileUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to ProfileBgTop,
                        0.65f to ProfileBgMid,
                        1.0f to ProfileDarkBg,
                    )
                )
            )
            .padding(top = 80.dp, bottom = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Plant with avatar overlapping the pot base
            Box(contentAlignment = Alignment.BottomCenter) {
                PlantIllustration(modifier = Modifier.size(width = 150.dp, height = 165.dp))
                ProfileAvatar(
                    modifier = Modifier
                        .size(62.dp)
                        .offset(y = 20.dp),
                )
            }
            Spacer(Modifier.height(32.dp)) // clears the 20dp avatar offset + gap
            IdentityBlock(uiState = uiState)
        }
    }
}

// ── Plant illustration ────────────────────────────────────────────────────────

@Composable
private fun PlantIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) { drawPlant() }
}

private fun DrawScope.drawBud(cx: Float, cy: Float, radius: Float) {
    drawCircle(color = BudOuter,                              radius = radius,        center = Offset(cx, cy))
    drawCircle(color = BudInner,                              radius = radius * 0.55f, center = Offset(cx, cy))
    drawCircle(color = Color(0xFFB89070).copy(alpha = 0.40f), radius = radius * 0.20f, center = Offset(cx, cy))
}

private fun DrawScope.drawPlant() {
    val w  = size.width
    val h  = size.height
    val cx = w / 2f

    // Pot body (trapezoid)
    val potTopY       = h * 0.54f
    val potBottomY    = h * 0.96f
    val potTopHalf    = w * 0.30f
    val potBottomHalf = w * 0.22f
    val potPath = Path().apply {
        moveTo(cx - potTopHalf,    potTopY)
        lineTo(cx + potTopHalf,    potTopY)
        lineTo(cx + potBottomHalf, potBottomY)
        lineTo(cx - potBottomHalf, potBottomY)
        close()
    }
    drawPath(potPath, color = PotBody)

    // Pot rim
    val rimH    = h * 0.055f
    val rimHalf = potTopHalf * 1.10f
    drawRoundRect(
        color        = PotRimColor,
        topLeft      = Offset(cx - rimHalf, potTopY - rimH * 0.6f),
        size         = Size(rimHalf * 2f, rimH),
        cornerRadius = CornerRadius(rimH / 2f),
    )

    // Pot highlight
    drawLine(
        color       = Color.White.copy(alpha = 0.12f),
        start       = Offset(cx - potTopHalf * 0.45f, potTopY + rimH * 0.8f),
        end         = Offset(cx - potBottomHalf * 0.45f, potBottomY - h * 0.02f),
        strokeWidth = 2.dp.toPx(),
        cap         = StrokeCap.Round,
    )

    // Stems
    val stemBase = potTopY - rimH * 0.2f
    val sw       = 2.5f.dp.toPx()

    val leftStem = Path().apply {
        moveTo(cx - w * 0.03f, stemBase)
        cubicTo(cx - w * 0.18f, stemBase - h * 0.12f, cx - w * 0.26f, stemBase - h * 0.24f, cx - w * 0.20f, stemBase - h * 0.38f)
    }
    drawPath(leftStem, color = StemColor, style = Stroke(width = sw, cap = StrokeCap.Round))

    val centerStem = Path().apply {
        moveTo(cx + w * 0.01f, stemBase)
        cubicTo(cx + w * 0.03f, stemBase - h * 0.14f, cx - w * 0.03f, stemBase - h * 0.28f, cx + w * 0.02f, stemBase - h * 0.44f)
    }
    drawPath(centerStem, color = StemColor, style = Stroke(width = sw, cap = StrokeCap.Round))

    val rightStem = Path().apply {
        moveTo(cx + w * 0.05f, stemBase)
        cubicTo(cx + w * 0.16f, stemBase - h * 0.13f, cx + w * 0.24f, stemBase - h * 0.28f, cx + w * 0.18f, stemBase - h * 0.40f)
    }
    drawPath(rightStem, color = StemColor, style = Stroke(width = sw, cap = StrokeCap.Round))

    // Small leaf on center stem
    val leafX = cx - w * 0.03f
    val leafY = stemBase - h * 0.22f
    val leaf  = Path().apply {
        moveTo(leafX, leafY)
        cubicTo(leafX - w * 0.10f, leafY - h * 0.04f, leafX - w * 0.10f, leafY + h * 0.04f, leafX, leafY)
    }
    drawPath(leaf, color = StemColor.copy(alpha = 0.75f), style = Stroke(width = sw * 0.75f, cap = StrokeCap.Round))

    // Buds
    val br = w * 0.048f
    drawBud(cx - w * 0.20f, stemBase - h * 0.38f, br)
    drawBud(cx + w * 0.02f, stemBase - h * 0.44f, br)
    drawBud(cx + w * 0.18f, stemBase - h * 0.40f, br)
}

// ── Avatar ────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileAvatar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .border(3.dp, Color.White, CircleShape)
            .background(AvatarBg),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector        = Icons.Default.Person,
            contentDescription = "Profile avatar",
            tint               = Color.White.copy(alpha = 0.80f),
            modifier           = Modifier.fillMaxSize(0.55f),
        )
    }
}

// ── Identity ──────────────────────────────────────────────────────────────────

@Composable
private fun IdentityBlock(uiState: ProfileUiState) {
    val strings = ProfileStrings.forCode(LocalLanguageCode.current)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text  = strings.favoriteMemory,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontStyle  = FontStyle.Italic,
                fontFamily = FontFamily.Cursive,
                fontSize   = 15.sp,
            ),
            color = Color.White.copy(alpha = 0.65f),
        )
        Text(
            text      = uiState.displayName.ifBlank { "Your Name" },
            style     = MaterialTheme.typography.displaySmall.copy(
                fontStyle  = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                fontSize   = 40.sp,
                fontWeight = FontWeight.Normal,
            ),
            color     = Color.White,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text      = "${strings.capturingSince} ${uiState.capturingSinceYear}",
            style     = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 2.sp,
                fontSize      = 11.sp,
                fontWeight    = FontWeight.Medium,
            ),
            color     = ProfileGold,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Stats ─────────────────────────────────────────────────────────────────────

private fun Int.toDisplayCount(): String =
    if (this >= 1_000) "${this / 1_000},${(this % 1_000).toString().padStart(3, '0')}"
    else toString()

@Composable
private fun StatsRow(
    totalPhotos: Int,
    albumsCount: Int,
    daysStreak: Int,
    modifier: Modifier = Modifier,
) {
    val strings = ProfileStrings.forCode(LocalLanguageCode.current)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatCard(value = totalPhotos.toDisplayCount(), label = strings.totalPhotos, modifier = Modifier.weight(1f))
        StatCard(value = albumsCount.toString(),       label = strings.albums,      modifier = Modifier.weight(1f))
        StatCard(value = daysStreak.toString(),        label = strings.daysStreak,  modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(StatCardBg)
            .padding(vertical = 14.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Text(
            text  = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize   = 20.sp,
            ),
            color = Color.White,
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 0.8.sp,
                fontSize      = 8.sp,
            ),
            color = TextMuted,
        )
    }
}

// ── Section header ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text  = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontStyle  = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                fontSize   = 20.sp,
            ),
            color = Color.White,
        )
        if (actionLabel != null) {
            Text(
                text     = actionLabel,
                style    = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontSize      = 10.sp,
                    fontWeight    = FontWeight.SemiBold,
                ),
                color    = ProfileGold,
                modifier = Modifier.clickable(onClick = onAction),
            )
        }
    }
}

// ── Recaps ────────────────────────────────────────────────────────────────────

@Composable
private fun RecapsSection(
    recaps: List<ProfileRecap>,
    onViewAll: () -> Unit,
    onRecap: (String) -> Unit,
) {
    val strings = ProfileStrings.forCode(LocalLanguageCode.current)
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SectionHeader(title = strings.yourRecaps, actionLabel = if (recaps.isNotEmpty()) strings.viewAll else null, onAction = onViewAll)
        if (recaps.isEmpty()) {
            ProfileSectionEmptyState(message = "Your recaps will appear here once your albums have memories to revisit.")
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                recaps.forEach { recap ->
                    RecapCard(
                        recap    = recap,
                        onClick  = { onRecap(recap.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun RecapCard(recap: ProfileRecap, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(160.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(recap.coverColorArgb))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f  to Color.Transparent,
                            0.55f to Color.Transparent,
                            1.0f  to Color.Black.copy(alpha = 0.65f),
                        )
                    )
                ),
        )
        Text(
            text     = recap.label,
            style    = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.5.sp,
                fontSize      = 10.sp,
                fontWeight    = FontWeight.SemiBold,
            ),
            color    = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
        )
    }
}

// ── Albums ────────────────────────────────────────────────────────────────────

@Composable
private fun AlbumsSection(albums: List<ProfileAlbum>, onAlbum: (String) -> Unit) {
    val fullWidth = albums.filter { it.isFullWidth }
    val staggered = albums.filter { !it.isFullWidth }
    val tall      = staggered.firstOrNull { it.isTall }
    val small     = staggered.filter { !it.isTall }

    val strings = ProfileStrings.forCode(LocalLanguageCode.current)
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SectionHeader(title = strings.yourAlbums)
        Spacer(Modifier.height(8.dp))

        if (tall == null && small.isEmpty() && fullWidth.isEmpty()) {
            ProfileSectionEmptyState(message = "Create your first album to start collecting and sharing moments.")
        } else if (tall != null || small.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (tall != null) {
                    AlbumCard(
                        album    = tall,
                        onClick  = { onAlbum(tall.id) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
                if (small.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        small.forEach { album ->
                            AlbumCard(
                                album    = album,
                                onClick  = { onAlbum(album.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }

        fullWidth.forEach { album ->
            AlbumCard(
                album    = album,
                onClick  = { onAlbum(album.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
            )
        }
    }
}

@Composable
private fun ProfileSectionEmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp, horizontal = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text       = message,
            style      = MaterialTheme.typography.bodySmall.copy(
                fontStyle  = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                fontSize   = 13.sp,
            ),
            color      = Color.White.copy(alpha = 0.28f),
            textAlign  = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun AlbumCard(album: ProfileAlbum, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val coverColor = Color(album.coverColorArgb)
    val isLight    = album.coverColorArgb > 0xFF_A0_A0_A0L

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(coverColor)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.50f to Color.Transparent,
                            1.0f  to Color.Black.copy(alpha = if (isLight) 0.42f else 0.60f),
                        )
                    )
                ),
        )
        Text(
            text     = album.name,
            style    = MaterialTheme.typography.bodyMedium.copy(
                fontStyle  = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                fontSize   = 14.sp,
            ),
            color    = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
        )
    }
}

