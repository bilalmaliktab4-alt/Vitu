package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SophisticatedDarkColorScheme = darkColorScheme(
    primary = IndigoPrimary,
    onPrimary = ObsidianDeep,
    primaryContainer = Color(0x336366F1),
    onPrimaryContainer = IndigoLight,
    secondary = IndigoAccentGlow,
    onSecondary = ObsidianDeep,
    secondaryContainer = Color(0x26818CF8),
    onSecondaryContainer = TextPrimary,
    tertiary = EmeraldGlow,
    onTertiary = ObsidianDeep,
    background = ObsidianDeep,
    onBackground = TextPrimary,
    surface = ObsidianDeep,
    onSurface = TextPrimary,
    surfaceVariant = Color(0x0DFFFFFF),
    onSurfaceVariant = TextSecondary,
    outline = GlassBorderSubtle,
    outlineVariant = GlassBorderVerySubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SophisticatedDarkColorScheme,
        typography = Typography,
        content = content
    )
}

