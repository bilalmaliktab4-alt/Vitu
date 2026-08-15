package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AspectRatioType
import com.example.data.model.WallpaperStyle
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.WallpaperViewModel

@Composable
fun HomeScreen(
    viewModel: WallpaperViewModel,
    onNavigateToResult: () -> Unit,
    onNavigateToExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prompt by viewModel.prompt.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val selectedAspectRatio by viewModel.selectedAspectRatio.collectAsState()
    val generationState by viewModel.generationState.collectAsState()
    val scrollState = rememberScrollState()

    val quickSuggestionPrompts = listOf(
        "A futuristic neon metropolis in the rain, cinematic lighting, 8k resolution, hyper-realistic" to WallpaperStyle.NEON,
        "Liquid obsidian with luminous 24k gold veins" to WallpaperStyle.LUXURY,
        "Makoto Shinkai glowing clouds sky with radiant twilight" to WallpaperStyle.ANIME,
        "Deep cosmic nebula starlight rings with prismatic refraction" to WallpaperStyle.SPACE,
        "Minimalist liquid glass water ripple with soft lighting" to WallpaperStyle.MINIMAL
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 120.dp)
        ) {
            // Sophisticated Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "AI ",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = (-0.5).sp
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = "Wallpapers",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = TextPrimary.copy(alpha = 0.90f)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x0DFFFFFF))
                        .border(1.dp, Color.White.copy(alpha = 0.10f), CircleShape)
                        .clickable { viewModel.randomizePrompt() }
                        .testTag("randomize_top_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Randomize Prompt",
                        tint = IndigoLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card 1: Generate Card (Sophisticated Dark Card)
            GlassBox(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                borderWidth = 1.dp,
                backgroundColor = Color(0x0DFFFFFF)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Text(
                        text = "GENERATE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.5.sp
                        ),
                        color = Color.White.copy(alpha = 0.40f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = prompt,
                        onValueChange = { viewModel.onPromptChanged(it) },
                        placeholderText = "A futuristic neon metropolis in the rain, cinematic lighting, 8k resolution...",
                        onRandomizeClick = { viewModel.randomizePrompt() },
                        onClearClick = { viewModel.onPromptChanged("") },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "home_prompt_input"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Idea Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickSuggestionPrompts.forEach { (suggestion, style) ->
                            SuggestionGlassChip(
                                text = suggestion,
                                onClick = {
                                    viewModel.onPromptChanged(suggestion)
                                    viewModel.onStyleSelected(style)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Generate Wallpaper Button
                    LiquidGlassButton(
                        text = "Generate Wallpaper",
                        onClick = {
                            viewModel.generateWallpaper(
                                onSuccess = {
                                    onNavigateToResult()
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = selectedStyle.accentColor,
                        testTag = "generate_wallpaper_button"
                    )

                    if (generationState.error != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x20EF4444))
                                .border(1.dp, ErrorRed.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = generationState.error ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFCA5A5)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Card 2: Style & Aspect Card (Sophisticated Dark Card)
            GlassBox(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                borderWidth = 1.dp,
                backgroundColor = Color(0x0DFFFFFF)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STYLE & ASPECT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 2.5.sp
                            ),
                            color = Color.White.copy(alpha = 0.40f)
                        )

                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = IndigoPrimary
                            ),
                            modifier = Modifier
                                .clickable { onNavigateToExplore() }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(WallpaperStyle.values()) { style ->
                            StyleGlassCard(
                                style = style,
                                isSelected = style == selectedStyle,
                                onClick = { viewModel.onStyleSelected(style) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Aspect Ratio Segmented Selector
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0x33000000))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(18.dp))
                            .padding(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AspectRatioType.values().forEach { ratio ->
                                AspectRatioGlassPill(
                                    aspectRatio = ratio,
                                    isSelected = ratio == selectedAspectRatio,
                                    onClick = { viewModel.onAspectRatioSelected(ratio) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Discover Showcase Card
            GlassBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToExplore() },
                shape = RoundedCornerShape(24.dp),
                borderWidth = 1.dp,
                backgroundColor = Color(0x0DFFFFFF)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0x1AFFFFFF))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = IndigoLight,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Explore Trending Wallpapers",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = TextPrimary
                        )
                        Text(
                            text = "Discover 8K liquid glass & dark creations",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp
                            ),
                            color = TextSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Explore",
                        tint = Color.White.copy(alpha = 0.40f)
                    )
                }
            }
        }

        // Loading Animation Modal
        GlassLoadingOverlay(
            isVisible = generationState.isGenerating,
            stepText = generationState.currentStep,
            progress = generationState.progress
        )
    }
}

@Composable
fun SuggestionGlassChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x0DFFFFFF))
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = IndigoPrimary,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun StyleGlassCard(
    style: WallpaperStyle,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "styleScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .width(100.dp)
            .height(115.dp)
            .testTag("style_card_${style.name.lowercase()}")
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) Color(0x336366F1) else Color(0x14FFFFFF)
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) Color(0x66818CF8) else Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color(0x40818CF8)
                        else Color(0x14FFFFFF)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) IndigoPrimary else Color.White.copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = style.icon,
                    contentDescription = style.title,
                    tint = if (isSelected) IndigoLight else Color.White.copy(alpha = 0.70f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = style.title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.sp
                ),
                color = if (isSelected) IndigoLight else Color.White.copy(alpha = 0.70f)
            )
        }
    }
}

@Composable
fun AspectRatioGlassPill(
    aspectRatio: AspectRatioType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .testTag("ratio_pill_${aspectRatio.name.lowercase()}")
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) Color(0x29FFFFFF) else Color.Transparent
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color.White.copy(alpha = 0.15f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = when (aspectRatio) {
                    AspectRatioType.PHONE -> Icons.Default.PhoneAndroid
                    AspectRatioType.SQUARE -> Icons.Default.CropSquare
                    AspectRatioType.DESKTOP -> Icons.Default.Laptop
                },
                contentDescription = aspectRatio.title,
                tint = if (isSelected) TextPrimary else Color.White.copy(alpha = 0.40f),
                modifier = Modifier.size(14.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "${aspectRatio.ratioLabel} ${aspectRatio.title}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = 10.sp
                ),
                color = if (isSelected) TextPrimary else Color.White.copy(alpha = 0.40f)
            )
        }
    }
}

