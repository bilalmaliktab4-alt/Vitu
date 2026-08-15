package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.WallpaperEntity
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassBox
import com.example.ui.components.GlassIconButton
import com.example.ui.components.LiquidGlassButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.WallpaperViewModel
import java.io.File

enum class GalleryTab {
    ALL,
    FAVORITES
}

@Composable
fun GalleryScreen(
    viewModel: WallpaperViewModel,
    onViewWallpaper: (WallpaperEntity) -> Unit,
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allWallpapers by viewModel.allWallpapers.collectAsState()
    val favoriteWallpapers by viewModel.favoriteWallpapers.collectAsState()
    var currentTab by remember { mutableStateOf(GalleryTab.ALL) }

    val displayedList = if (currentTab == GalleryTab.ALL) allWallpapers else favoriteWallpapers

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
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
                    text = "Liquid Gallery",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
                Text(
                    text = "${displayedList.size} Wallpapers Created",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = CyanBright
                    )
                )
            }

            // Tab Selector Pill
            GlassBox(
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color(0x20FFFFFF),
                borderWidth = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    GalleryTabPill(
                        title = "All",
                        isSelected = currentTab == GalleryTab.ALL,
                        onClick = { currentTab = GalleryTab.ALL }
                    )
                    GalleryTabPill(
                        title = "Favorites",
                        isSelected = currentTab == GalleryTab.FAVORITES,
                        onClick = { currentTab = GalleryTab.FAVORITES }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (displayedList.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 120.dp),
                contentAlignment = Alignment.Center
            ) {
                GlassBox(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(24.dp),
                    shape = RoundedCornerShape(28.dp),
                    borderGlowColor = CyanNeon,
                    backgroundColor = Color(0x250A0E1A)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(CyanNeon.copy(alpha = 0.2f))
                                .border(1.dp, CyanNeon.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (currentTab == GalleryTab.FAVORITES) Icons.Default.FavoriteBorder else Icons.Default.Wallpaper,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (currentTab == GalleryTab.FAVORITES) "No Favorites Yet" else "Gallery is Empty",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (currentTab == GalleryTab.FAVORITES) "Tap the heart on any wallpaper to add it here." else "Generate your first liquid glass masterpiece now!",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        LiquidGlassButton(
                            text = "Create Wallpaper",
                            onClick = onNavigateToCreate,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        } else {
            // Grid of Saved Wallpapers
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 120.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(displayedList, key = { it.id }) { item ->
                    GalleryWallpaperCard(
                        wallpaper = item,
                        onClick = { onViewWallpaper(item) },
                        onToggleFavorite = { viewModel.toggleFavorite(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun GalleryTabPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) CyanNeon.copy(alpha = 0.35f) else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) CyanNeon else Color.Transparent,
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TextPrimary else TextSecondary
            )
        )
    }
}

@Composable
fun GalleryWallpaperCard(
    wallpaper: WallpaperEntity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    val imageModel = remember(wallpaper) {
        if (wallpaper.drawableResId != null) {
            wallpaper.drawableResId
        } else if (wallpaper.imagePath.isNotBlank()) {
            File(wallpaper.imagePath)
        } else {
            null
        }
    }

    GlassBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .testTag("gallery_card_${wallpaper.id}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        borderWidth = 1.dp,
        borderGlowColor = if (wallpaper.isFavorite) PinkNeon else null,
        backgroundColor = Color(0x200A0E1A)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageModel)
                    .crossfade(true)
                    .build(),
                contentDescription = wallpaper.prompt,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.25f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Favorite Button Top Right
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x35000000))
            ) {
                Icon(
                    imageVector = if (wallpaper.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (wallpaper.isFavorite) PinkNeon else Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Bottom Prompt Summary
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = wallpaper.prompt,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary,
                    maxLines = 1
                )

                Text(
                    text = "${wallpaper.style} • ${wallpaper.aspectRatio}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyanBright
                    )
                )
            }
        }
    }
}
