package com.example.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WallpaperStyle
import com.example.ui.components.GlassBox
import com.example.ui.components.LiquidGlassBackground
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.WallpaperViewModel

enum class NavDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Default.Home, Icons.Outlined.Home),
    EXPLORE("Explore", Icons.Default.Explore, Icons.Outlined.Explore),
    GALLERY("Gallery", Icons.Default.Collections, Icons.Outlined.Collections),
    SETTINGS("Settings", Icons.Default.Settings, Icons.Outlined.Settings)
}

@Composable
fun AppNavigation(
    viewModel: WallpaperViewModel
) {
    var currentDestination by remember { mutableStateOf(NavDestination.HOME) }
    var isViewingResultScreen by remember { mutableStateOf(false) }
    val auraTheme by viewModel.auraTheme.collectAsState()

    LiquidGlassBackground(auraTheme = auraTheme) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main Screen Container
            AnimatedContent(
                targetState = isViewingResultScreen to currentDestination,
                transitionSpec = {
                    fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                },
                label = "ScreenTransition"
            ) { (isResult, dest) ->
                if (isResult) {
                    ResultScreen(
                        viewModel = viewModel,
                        onNavigateBack = { isViewingResultScreen = false },
                        onEditPrompt = { prompt ->
                            viewModel.onPromptChanged(prompt)
                            isViewingResultScreen = false
                            currentDestination = NavDestination.HOME
                        }
                    )
                } else {
                    when (dest) {
                        NavDestination.HOME -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToResult = { isViewingResultScreen = true },
                            onNavigateToExplore = { currentDestination = NavDestination.EXPLORE }
                        )
                        NavDestination.EXPLORE -> ExploreScreen(
                            viewModel = viewModel,
                            onSelectWallpaperForStudio = { prompt, style ->
                                viewModel.onPromptChanged(prompt)
                                viewModel.onStyleSelected(style)
                                currentDestination = NavDestination.HOME
                            },
                            onViewWallpaper = { wallpaper ->
                                viewModel.selectWallpaperForView(wallpaper)
                                isViewingResultScreen = true
                            }
                        )
                        NavDestination.GALLERY -> GalleryScreen(
                            viewModel = viewModel,
                            onViewWallpaper = { wallpaper ->
                                viewModel.selectWallpaperForView(wallpaper)
                                isViewingResultScreen = true
                            },
                            onNavigateToCreate = { currentDestination = NavDestination.HOME }
                        )
                        NavDestination.SETTINGS -> SettingsScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }

            // Floating Liquid Glass Bottom Navigation Dock
            if (!isViewingResultScreen) {
                FloatingGlassBottomBar(
                    currentDestination = currentDestination,
                    onDestinationSelected = { currentDestination = it },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                )
            }
        }
    }
}

@Composable
fun FloatingGlassBottomBar(
    currentDestination: NavDestination,
    onDestinationSelected: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = Color.Black.copy(alpha = 0.7f),
                spotColor = Color.Black.copy(alpha = 0.8f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0x1A0D0D12))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(32.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavDestination.values().forEach { destination ->
                val isSelected = destination == currentDestination
                GlassNavItem(
                    destination = destination,
                    isSelected = isSelected,
                    onClick = { onDestinationSelected(destination) }
                )
            }
        }
    }
}

@Composable
fun GlassNavItem(
    destination: NavDestination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) Color(0x22818CF8) else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("nav_tab_${destination.title.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                contentDescription = destination.title,
                tint = if (isSelected) IndigoPrimary else Color.White.copy(alpha = 0.40f),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = destination.title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    letterSpacing = 1.2.sp
                ),
                color = if (isSelected) IndigoPrimary else Color.White.copy(alpha = 0.40f)
            )
        }
    }
}
