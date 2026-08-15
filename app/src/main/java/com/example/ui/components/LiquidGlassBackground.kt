package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

enum class AuraTheme(val displayName: String, val colors: List<Color>) {
    SOPHISTICATED_DARK(
        "Sophisticated Dark",
        listOf(
            Color(0xFF030303), // Base Canvas
            Color(0xFF07090F), // Surface gradient
            Color(0xFF4F46E5), // Top Indigo-600 glow
            Color(0xFF6366F1), // Center accent glow
            Color(0xFF059669)  // Bottom Emerald-600 glow
        )
    ),
    COSMIC_AURORA(
        "Cosmic Aurora",
        listOf(
            Color(0xFF030303),
            Color(0xFF0A0F1D),
            Color(0xFF4F46E5),
            Color(0xFF9333EA),
            Color(0xFF059669)
        )
    ),
    CYBER_NEON(
        "Cyber Neon",
        listOf(
            Color(0xFF030303),
            Color(0xFF140D24),
            Color(0xFFE11D48),
            Color(0xFF6366F1),
            Color(0xFF7C3AED)
        )
    ),
    DEEP_OBSIDIAN(
        "Deep Obsidian",
        listOf(
            Color(0xFF030303),
            Color(0xFF0B0E14),
            Color(0xFF1E293B),
            Color(0xFF334155),
            Color(0xFF4F46E5)
        )
    ),
    SOLAR_FLARE(
        "Solar Flare",
        listOf(
            Color(0xFF030303),
            Color(0xFF1F0E05),
            Color(0xFFD97706),
            Color(0xFFDC2626),
            Color(0xFF4F46E5)
        )
    ),
    MYSTIC_AMETHYST(
        "Mystic Amethyst",
        listOf(
            Color(0xFF030303),
            Color(0xFF150A24),
            Color(0xFF8B5CF6),
            Color(0xFFEC4899),
            Color(0xFF4F46E5)
        )
    )
}

@Composable
fun LiquidGlassBackground(
    modifier: Modifier = Modifier,
    auraTheme: AuraTheme = AuraTheme.SOPHISTICATED_DARK,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LiquidBackgroundTransition")

    // Slow organic pulsation & orbital movements
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val colors = auraTheme.colors

            // 1. Deep Obsidian Base
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(colors[0], colors[1], Color(0xFF020408))
                )
            )

            val radTime = Math.toRadians(time.toDouble())

            // 2. Liquid Orb 1: Primary Cyan/Violet Orb (Top Left to Center drift)
            val orb1X = width * 0.25f + (cos(radTime).toFloat() * width * 0.18f)
            val orb1Y = height * 0.22f + (sin(radTime * 0.8).toFloat() * height * 0.12f)
            val orb1Radius = width * 0.65f * pulse
            drawLiquidOrb(
                center = Offset(orb1X, orb1Y),
                radius = orb1Radius,
                color = colors[2].copy(alpha = 0.35f)
            )

            // 3. Liquid Orb 2: Secondary Magenta/Purple Orb (Right Side to Bottom drift)
            val orb2X = width * 0.8f + (sin(radTime * 0.7).toFloat() * width * 0.15f)
            val orb2Y = height * 0.55f + (cos(radTime * 0.9).toFloat() * height * 0.14f)
            val orb2Radius = width * 0.7f * (2f - pulse)
            drawLiquidOrb(
                center = Offset(orb2X, orb2Y),
                radius = orb2Radius,
                color = colors[3].copy(alpha = 0.30f)
            )

            // 4. Liquid Orb 3: Emerald/Solar Accent (Bottom Left drift)
            val orb3X = width * 0.3f + (cos(radTime * 1.1).toFloat() * width * 0.2f)
            val orb3Y = height * 0.82f + (sin(radTime * 0.6).toFloat() * height * 0.1f)
            val orb3Radius = width * 0.55f * pulse
            drawLiquidOrb(
                center = Offset(orb3X, orb3Y),
                radius = orb3Radius,
                color = colors[4].copy(alpha = 0.25f)
            )

            // 5. Subtle Shimmer Grid & Star Dust
            drawNoiseAndGlints(width, height, time)
        }

        content()
    }
}

private fun DrawScope.drawLiquidOrb(
    center: Offset,
    radius: Float,
    color: Color
) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color,
                color.copy(alpha = color.alpha * 0.5f),
                color.copy(alpha = color.alpha * 0.15f),
                Color.Transparent
            ),
            center = center,
            radius = radius
        ),
        center = center,
        radius = radius
    )
}

private fun DrawScope.drawNoiseAndGlints(width: Float, height: Float, time: Float) {
    // Elegant soft vignette
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                Color.Black.copy(alpha = 0.45f),
                Color.Black.copy(alpha = 0.75f)
            ),
            center = Offset(width / 2f, height / 2f),
            radius = maxOf(width, height) * 0.85f
        )
    )
}
