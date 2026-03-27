package com.photogram.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ── Palette (matches Home) ─────────────────────────────────────────────────────
private val BnBg    = Color(0xFF050505)
private val BnPill  = Color(0xFF111114)
private val BnTerra = Color(0xFFC9A96E)

// ── Active destination ─────────────────────────────────────────────────────────

enum class PhotogramNavDestination { Home, Gallery, Create, Chat, Profile, None }

// ── Shared floating bottom nav ─────────────────────────────────────────────────

/**
 * The single canonical bottom navigation bar used across all main screens.
 * Matches the Home screen capsule exactly: dark rounded pill, five items,
 * center terracotta + FAB, right-side avatar slot.
 *
 * @param activeDestination highlights the item that corresponds to the current screen
 * @param avatarContent optional composable rendered inside the 38dp circular profile slot;
 *                      defaults to a Person icon fallback (no Coil dependency needed here)
 */
@Composable
fun PhotogramBottomNav(
    modifier: Modifier = Modifier,
    activeDestination: PhotogramNavDestination = PhotogramNavDestination.None,
    unreadMessages: Int = 0,
    onHome: () -> Unit = {},
    onGallery: () -> Unit = {},
    onCreate: () -> Unit = {},
    onChat: () -> Unit = {},
    onProfile: () -> Unit = {},
    avatarContent: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, BnBg.copy(alpha = 0.98f)),
                )
            )
            .navigationBarsPadding()
            .padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
                .background(BnPill)
                .padding(horizontal = 6.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            // Home
            BnIconButton(
                icon        = Icons.Default.Home,
                description = "Home",
                tint        = if (activeDestination == PhotogramNavDestination.Home) Color.White
                              else Color.White.copy(alpha = 0.48f),
                size        = if (activeDestination == PhotogramNavDestination.Home) 24.dp else 22.dp,
                onClick     = onHome,
            )

            // Albums / Gallery
            BnIconButton(
                icon        = Icons.Default.Collections,
                description = "Albums",
                tint        = if (activeDestination == PhotogramNavDestination.Gallery) Color.White
                              else Color.White.copy(alpha = 0.48f),
                size        = 22.dp,
                onClick     = onGallery,
            )

            // Create — terracotta circle, slightly raised
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(BnTerra)
                    .clickable(onClick = onCreate),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = "Create",
                    tint               = Color.White,
                    modifier           = Modifier.size(26.dp),
                )
            }

            // Chat — with optional unread red dot
            Box(contentAlignment = Alignment.TopEnd) {
                BnIconButton(
                    icon        = Icons.AutoMirrored.Filled.Chat,
                    description = "Chat",
                    tint        = if (activeDestination == PhotogramNavDestination.Chat) Color.White
                                  else Color.White.copy(alpha = 0.48f),
                    size        = 22.dp,
                    onClick     = onChat,
                )
                if (unreadMessages > 0) {
                    Box(
                        modifier = Modifier
                            .padding(top = 9.dp, end = 9.dp)
                            .size(7.dp)
                            .background(Color(0xFFE53935), CircleShape),
                    )
                }
            }

            // Profile — avatar slot or Person icon fallback
            val profileActive = activeDestination == PhotogramNavDestination.Profile
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(
                        width = if (profileActive) 2.dp else 1.5.dp,
                        color = if (profileActive) Color.White else Color.White.copy(alpha = 0.35f),
                        shape = CircleShape,
                    )
                    .clickable(onClick = onProfile),
                contentAlignment = Alignment.Center,
            ) {
                if (avatarContent != null) {
                    avatarContent()
                } else {
                    Icon(
                        imageVector        = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint               = if (profileActive) Color.White else Color.White.copy(alpha = 0.55f),
                        modifier           = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun BnIconButton(
    icon: ImageVector,
    description: String,
    tint: Color,
    size: Dp,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick, modifier = Modifier.size(44.dp)) {
        Icon(
            imageVector        = icon,
            contentDescription = description,
            tint               = tint,
            modifier           = Modifier.size(size),
        )
    }
}
