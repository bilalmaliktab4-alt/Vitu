package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AuraTheme
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassBox
import com.example.ui.theme.*
import com.example.ui.viewmodel.WallpaperViewModel

@Composable
fun SettingsScreen(
    viewModel: WallpaperViewModel,
    modifier: Modifier = Modifier
) {
    val auraTheme by viewModel.auraTheme.collectAsState()
    val blurIntensity by viewModel.blurIntensity.collectAsState()
    val exportQuality by viewModel.exportQuality.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 120.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Glass Lab & Settings",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
                Text(
                    text = "Aesthetics & AI Engine",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = CyanBright
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = null,
                tint = CyanNeon,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 1. Aura Themes
        Text(
            text = "LIQUID AURA THEME",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        GlassBox(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            backgroundColor = Color(0x18FFFFFF)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                AuraTheme.values().forEach { theme ->
                    AuraThemeRow(
                        theme = theme,
                        isSelected = theme == auraTheme,
                        onClick = { viewModel.setAuraTheme(theme) }
                    )
                    if (theme != AuraTheme.values().last()) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Refraction & Blur Quality Slider
        Text(
            text = "GLASS REFRACTION & BLUR",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        GlassBox(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            backgroundColor = Color(0x18FFFFFF)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Translucency Intensity",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary
                    )
                    Text(
                        text = "${(blurIntensity * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = CyanNeon
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Slider(
                    value = blurIntensity,
                    onValueChange = { viewModel.setBlurIntensity(it) },
                    valueRange = 0.5f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyanNeon,
                        activeTrackColor = CyanNeon,
                        inactiveTrackColor = Color(0x33FFFFFF)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Export Quality
        Text(
            text = "WALLPAPER RESOLUTION",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        GlassBox(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            backgroundColor = Color(0x18FFFFFF)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Standard HD", "4K Ultra", "8K Master").forEach { quality ->
                    val isSelected = quality == exportQuality
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) CyanNeon.copy(alpha = 0.25f) else Color(0x10FFFFFF))
                            .border(1.dp, if (isSelected) CyanNeon else Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                            .clickable { viewModel.setExportQuality(quality) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = quality,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextPrimary else TextSecondary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. AI Engine Status & Architecture
        Text(
            text = "AI ENGINE ARCHITECTURE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        GlassBox(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            borderGlowColor = CyanNeon,
            backgroundColor = Color(0x18FFFFFF)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Gemini Flash & Liquid Core",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }
                    GlassBadge(text = "ACTIVE", accentColor = EmeraldGlow)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Featuring automated intelligent prompt enhancement, dual-stage 8K neural procedural synthesis, and direct Gemini API multimodal generation.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 18.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. About
        GlassBox(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            backgroundColor = Color(0x12FFFFFF)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "AI Wallpaper Maker • Liquid Glass v1.0",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Crafted with Jetpack Compose & Glassmorphism",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                )
            }
        }
    }
}

@Composable
fun AuraThemeRow(
    theme: AuraTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) theme.colors[2].copy(alpha = 0.25f) else Color(0x10FFFFFF))
            .border(
                1.dp,
                if (isSelected) theme.colors[2] else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Color Swatches
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    theme.colors.drop(2).take(3).forEach { col ->
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(col)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = theme.displayName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = TextPrimary
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = theme.colors[2],
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
