package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.R
import com.example.data.local.WallpaperEntity
import com.example.data.model.WallpaperStyle
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassBox
import com.example.ui.components.GlassIconButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.WallpaperViewModel
import java.io.File

data class ExploreItem(
    val title: String,
    val prompt: String,
    val style: WallpaperStyle,
    val drawableResId: Int,
    val tags: List<String>
)

@Composable
fun ExploreScreen(
    viewModel: WallpaperViewModel,
    onSelectWallpaperForStudio: (String, WallpaperStyle) -> Unit,
    onViewWallpaper: (WallpaperEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<WallpaperStyle?>(null) }

    val exploreWallpapers = remember {
        listOf(
            ExploreItem(
                title = "Cinematic Liquid Glass Sphere",
                prompt = "Cinematic liquid glass sphere floating over misty hyper-realistic neon cyber city in rain, reflections on wet asphalt, volumetric cinematic lighting, 8k",
                style = WallpaperStyle.CINEMATIC,
                drawableResId = R.drawable.wallpaper_cinematic_1786811243830,
                tags = listOf("8K", "Octane", "Liquid")
            ),
            ExploreItem(
                title = "Deep Space Crystal Rings",
                prompt = "Vibrant cosmic nebula with glowing liquid glass floating geometric rings and starlight, deep violet and cyan aurora, ultra high detail 8k",
                style = WallpaperStyle.SPACE,
                drawableResId = R.drawable.wallpaper_neon_space_1786811256932,
                tags = listOf("Cosmic", "Aurora", "Nebula")
            ),
            ExploreItem(
                title = "Ethereal Glass Waterfall Forest",
                prompt = "Enchanted ethereal waterfall cascading through translucent liquid glass crystal forest, bioluminescent flora, golden sun rays, 8k",
                style = WallpaperStyle.NATURE,
                drawableResId = R.drawable.wallpaper_nature_crystal_1786811272243,
                tags = listOf("Bioluminescent", "Flora", "Sunlight")
            ),
            ExploreItem(
                title = "Anime Sky Islands & Aurora",
                prompt = "Stunning anime aesthetic floating sky islands with glowing cherry blossoms and liquid crystal lakes under starry aurora twilight, Makoto Shinkai style",
                style = WallpaperStyle.ANIME,
                drawableResId = R.drawable.wallpaper_anime_dream_1786811285455,
                tags = listOf("Anime", "Makoto", "Twilight")
            )
        )
    }

    val filteredItems = if (selectedCategory == null) {
        exploreWallpapers
    } else {
        exploreWallpapers.filter { it.style == selectedCategory }
    }

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
                    text = "Explore Liquid Glass",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
                Text(
                    text = "Trending AI Masterpieces",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = CyanBright
                    )
                )
            }

            GlassBadge(
                text = "FEATURED 8K",
                icon = Icons.Default.AutoAwesome,
                accentColor = AmberGlow
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Categories Scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryFilterChip(
                title = "All Styles",
                isSelected = selectedCategory == null,
                onClick = { selectedCategory = null }
            )

            WallpaperStyle.values().forEach { style ->
                CategoryFilterChip(
                    title = style.title,
                    isSelected = selectedCategory == style,
                    onClick = { selectedCategory = style },
                    accentColor = style.accentColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Wallpaper Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 120.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredItems) { item ->
                ExploreWallpaperCard(
                    item = item,
                    onTryPrompt = {
                        onSelectWallpaperForStudio(item.prompt, item.style)
                    },
                    onViewClick = {
                        val entity = WallpaperEntity(
                            prompt = item.title,
                            enhancedPrompt = item.prompt,
                            style = item.style.name,
                            aspectRatio = "9:16",
                            imagePath = "",
                            drawableResId = item.drawableResId
                        )
                        onViewWallpaper(entity)
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryFilterChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color = CyanNeon
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.25f) else Color(0x15FFFFFF))
            .border(
                1.dp,
                if (isSelected) accentColor else Color.White.copy(alpha = 0.15f),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
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
fun ExploreWallpaperCard(
    item: ExploreItem,
    onTryPrompt: () -> Unit,
    onViewClick: () -> Unit
) {
    val context = LocalContext.current

    GlassBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clickable(onClick = onViewClick),
        shape = RoundedCornerShape(22.dp),
        borderWidth = 1.dp,
        borderGlowColor = item.style.accentColor,
        backgroundColor = Color(0x200A0E1A)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(item.drawableResId)
                    .crossfade(true)
                    .build(),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Glass Gradient Overlay on card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Style Badge on Top Left
            GlassBadge(
                text = item.style.title,
                accentColor = item.style.accentColor,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            )

            // Bottom info & Try in Studio button
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(item.style.accentColor.copy(alpha = 0.25f))
                        .border(1.dp, item.style.accentColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable(onClick = onTryPrompt)
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = item.style.accentColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Use Prompt",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}
