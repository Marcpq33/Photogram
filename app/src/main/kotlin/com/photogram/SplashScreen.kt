package com.photogram

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

// ── Splash-local tokens (from Figma node 1:744 screenshot) ────────────────────
private val SplashBg   = Color(0xFF050505)
private val SplashGold = Color(0xFFC9A96E)   // Gold — premium wordmark

/**
 * Shown while AppViewModel.startDestination == null (DataStore not yet resolved).
 * No fake timers or backend logic — disappears the moment session state is available.
 */
@Composable
internal fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBg),
        contentAlignment = Alignment.Center,
    ) {
        // Warm radial glow centered on wordmark (Figma: soft amber bloom)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x30C5663E),  // warm amber — center
                        Color(0x18265D91),  // cool blue diffuse mid
                        Color.Transparent,
                    ),
                    center = center,
                    radius = size.width * 0.60f,
                ),
                radius = size.width * 0.60f,
                center = center,
            )
        }

        // "Photo" (Thin SansSerif white) + "gram" (Italic Serif gold)
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Thin,
                        fontSize = 40.sp,
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif,
                    )
                ) {
                    append("Photo")
                }
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Italic,
                        fontSize = 40.sp,
                        color = SplashGold,
                        fontFamily = FontFamily.Serif,
                    )
                ) {
                    append("gram")
                }
            },
        )
    }
}
