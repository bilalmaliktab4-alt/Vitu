package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.WallpaperEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.WallpaperTarget
import com.example.ui.viewmodel.WallpaperViewModel
import java.io.File

enum class PreviewMode {
    CLEAN,
    LOCKSCREEN_MOCK,
    HOMESCREEN_MOCK
}

@Composable
fun ResultScreen(
    viewModel: WallpaperViewModel,
    onNavigateBack: () -> Unit,
    onEditPrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val wallpaper by viewModel.currentWallpaper.collectAsState()
    val generationState by viewModel.generationState.collectAsState()

    var previewMode by remember { mutableStateOf(PreviewMode.CLEAN) }
    var showSetWallpaperDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }

    if (wallpaper == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "No Wallpaper Selected", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(modifier = Modifier.height(16.dp))
                LiquidGlassButton(text = "Return Home", onClick = onNavigateBack)
            }
        }
        return
    }

    val item = wallpaper!!

    val imageModel = remember(item) {
        if (item.drawableResId != null) {
            item.drawableResId
        } else if (item.imagePath.isNotBlank()) {
            File(item.imagePath)
        } else {
            null
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Full Screen / Prominent Wallpaper Image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageModel)
                    .crossfade(true)
                    .build(),
                contentDescription = item.prompt,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("result_wallpaper_image")
            )

            // Optional Mock Overlays (Lock screen clock or Home screen icons)
            when (previewMode) {
                PreviewMode.LOCKSCREEN_MOCK -> {
                    LockScreenOverlay()
                }
                PreviewMode.HOMESCREEN_MOCK -> {
                    HomeScreenOverlay()
                }
                PreviewMode.CLEAN -> {}
            }

            // Top Floating Glass Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(
                    icon = Icons.Default.ArrowBack,
                    onClick = onNavigateBack,
                    contentDescription = "Back",
                    testTag = "result_back_button"
                )

                // Preview Mode Switcher (Clean / Lock / Home)
                GlassBox(
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = Color(0x35000000),
                    borderWidth = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        PreviewModeChip(
                            icon = Icons.Default.Fullscreen,
                            isSelected = previewMode == PreviewMode.CLEAN,
                            onClick = { previewMode = PreviewMode.CLEAN }
                        )
                        PreviewModeChip(
                            icon = Icons.Default.Lock,
                            isSelected = previewMode == PreviewMode.LOCKSCREEN_MOCK,
                            onClick = { previewMode = PreviewMode.LOCKSCREEN_MOCK }
                        )
                        PreviewModeChip(
                            icon = Icons.Default.Widgets,
                            isSelected = previewMode == PreviewMode.HOMESCREEN_MOCK,
                            onClick = { previewMode = PreviewMode.HOMESCREEN_MOCK }
                        )
                    }
                }

                GlassIconButton(
                    icon = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    onClick = { viewModel.toggleFavorite(item) },
                    contentDescription = "Favorite",
                    tint = if (item.isFavorite) PinkNeon else TextPrimary,
                    glowColor = if (item.isFavorite) PinkNeon else null,
                    testTag = "result_favorite_button"
                )
            }

            // Bottom Floating Glass Control Dock
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Prompt Info Pill
                GlassBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDetailsDialog = true },
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = Color(0x38000000),
                    borderWidth = 1.dp,
                    borderGlowColor = CyanNeon.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.prompt,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = TextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = "${item.style} • ${item.aspectRatio} • Tap for AI Details",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyanBright
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Details",
                            tint = CyanBright,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Primary Floating Action Buttons Row
                GlassBox(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    backgroundColor = Color(0x400A0E1A),
                    borderWidth = 1.5.dp,
                    borderGlowColor = CyanNeon
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FloatingActionButtonItem(
                            icon = Icons.Default.Wallpaper,
                            label = "Apply",
                            accentColor = CyanNeon,
                            onClick = { showSetWallpaperDialog = true },
                            testTag = "action_apply_wallpaper"
                        )

                        FloatingActionButtonItem(
                            icon = Icons.Default.Download,
                            label = "Save",
                            accentColor = EmeraldGlow,
                            onClick = { viewModel.saveWallpaperToGallery(context, item) },
                            testTag = "action_save_wallpaper"
                        )

                        FloatingActionButtonItem(
                            icon = Icons.Default.Share,
                            label = "Share",
                            accentColor = VioletNeon,
                            onClick = { viewModel.shareWallpaper(context, item) },
                            testTag = "action_share_wallpaper"
                        )

                        FloatingActionButtonItem(
                            icon = Icons.Default.Refresh,
                            label = "Regenerate",
                            accentColor = AmberGlow,
                            onClick = {
                                viewModel.generateWallpaper(onSuccess = {})
                            },
                            testTag = "action_regenerate_wallpaper"
                        )

                        FloatingActionButtonItem(
                            icon = Icons.Default.Edit,
                            label = "Edit",
                            accentColor = RoseBright,
                            onClick = {
                                onEditPrompt(item.prompt)
                            },
                            testTag = "action_edit_prompt"
                        )
                    }
                }
            }
        }

        // Set Wallpaper Sheet / Dialog
        if (showSetWallpaperDialog) {
            SetWallpaperGlassDialog(
                onDismiss = { showSetWallpaperDialog = false },
                onSelectTarget = { target ->
                    showSetWallpaperDialog = false
                    viewModel.setAsDeviceWallpaper(context, item, target) { success ->
                        if (success) {
                            Toast.makeText(context, "Wallpaper Applied Successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        // AI Details Dialog
        if (showDetailsDialog) {
            WallpaperDetailsGlassDialog(
                wallpaper = item,
                onDismiss = { showDetailsDialog = false }
            )
        }

        // Loading Overlay if user clicked Regenerate
        GlassLoadingOverlay(
            isVisible = generationState.isGenerating,
            stepText = generationState.currentStep,
            progress = generationState.progress
        )
    }
}

@Composable
fun PreviewModeChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (isSelected) CyanNeon.copy(alpha = 0.35f) else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) CyanNeon else Color.Transparent,
                CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) TextPrimary else TextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun FloatingActionButtonItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .testTag(testTag)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.25f))
                .border(1.dp, accentColor.copy(alpha = 0.7f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            ),
            color = TextPrimary
        )
    }
}

@Composable
fun LockScreenOverlay() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Saturday, August 15",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        )
        Text(
            text = "09:41",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 82.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = (-2).sp,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Futuristic Glass Widget
        GlassBox(
            shape = RoundedCornerShape(20.dp),
            backgroundColor = Color(0x28000000),
            borderWidth = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = AmberGlow,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "72°F • Crystal Clear Glass",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextPrimary)
                )
            }
        }
    }
}

@Composable
fun HomeScreenOverlay() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = 80.dp, start = 24.dp, end = 24.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        repeat(3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(4) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x35FFFFFF))
                                .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apps,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(width = 32.dp, height = 5.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.4f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SetWallpaperGlassDialog(
    onDismiss: () -> Unit,
    onSelectTarget: (WallpaperTarget) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        GlassBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            borderGlowColor = CyanNeon,
            backgroundColor = Color(0x400A0E1A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Apply Wallpaper",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Choose where to apply this liquid glass creation",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                SetWallpaperOption(
                    title = "Home Screen",
                    icon = Icons.Default.Home,
                    onClick = { onSelectTarget(WallpaperTarget.HOME) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                SetWallpaperOption(
                    title = "Lock Screen",
                    icon = Icons.Default.Lock,
                    onClick = { onSelectTarget(WallpaperTarget.LOCK) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                SetWallpaperOption(
                    title = "Both Screens",
                    icon = Icons.Default.Smartphone,
                    accent = true,
                    onClick = { onSelectTarget(WallpaperTarget.BOTH) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun SetWallpaperOption(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (accent) CyanNeon.copy(alpha = 0.25f) else Color(0x18FFFFFF))
            .border(
                1.dp,
                if (accent) CyanNeon else Color.White.copy(alpha = 0.2f),
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (accent) CyanNeon else TextPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = TextPrimary
            )
        }
    }
}

@Composable
fun WallpaperDetailsGlassDialog(
    wallpaper: WallpaperEntity,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        GlassBox(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 520.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            borderGlowColor = VioletNeon,
            backgroundColor = Color(0x450A0E1A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI Wallpaper Details",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                DetailItem(label = "ORIGINAL PROMPT", value = wallpaper.prompt)
                Spacer(modifier = Modifier.height(12.dp))
                DetailItem(label = "AI ENHANCED PROMPT", value = wallpaper.enhancedPrompt, isHighlight = true)
                Spacer(modifier = Modifier.height(12.dp))
                DetailItem(label = "ART STYLE", value = wallpaper.style)
                Spacer(modifier = Modifier.height(12.dp))
                DetailItem(label = "ASPECT RATIO", value = wallpaper.aspectRatio)
                Spacer(modifier = Modifier.height(12.dp))
                DetailItem(label = "RENDER ENGINE", value = "Liquid Glass 8K Neural Refractor")
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, isHighlight: Boolean = false) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = if (isHighlight) CyanNeon else TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextPrimary,
                lineHeight = 20.sp
            )
        )
    }
}
