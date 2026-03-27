package com.photogram.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val PhotogramShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(22.dp),   // Photo cards
    large = RoundedCornerShape(28.dp),    // Album cards
    extraLarge = RoundedCornerShape(50.dp), // Pill buttons, avatars
)
