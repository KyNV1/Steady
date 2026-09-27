package com.steady.app.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// M3 shape slots, aligned to Steady's card/button radii.
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

// Named tokens for shapes that don't map cleanly onto the 5 M3 slots.
object SteadyShapes {
    val hero = RoundedCornerShape(24.dp)
    val card = RoundedCornerShape(16.dp)
    val button = RoundedCornerShape(14.dp)
    val chip = CircleShape
    val iconTile = RoundedCornerShape(12.dp)
    val bottomSheetTop = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
}
