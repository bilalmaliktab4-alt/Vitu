package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Public
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class WallpaperStyle(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val promptSuffix: String
) {
    CINEMATIC(
        title = "Cinematic",
        description = "Volumetric 8K lighting, dramatic depth and anamorphic flares",
        icon = Icons.Default.Movie,
        accentColor = Color(0xFF38BDF8),
        promptSuffix = "cinematic volumetric lighting, 8k octane render, photorealistic, dramatic atmosphere, depth of field, raytracing, unreal engine 5, master composition"
    ),
    REALISTIC(
        title = "Realistic",
        description = "Hyper-detailed photographic textures and natural light",
        icon = Icons.Default.CameraAlt,
        accentColor = Color(0xFF34D399),
        promptSuffix = "ultra realistic, 8k resolution, raw photo, Hasselblad 85mm lens, natural subsurface scattering, hyper-detailed, sharp focus, award winning photography"
    ),
    ANIME(
        title = "Anime",
        description = "Ethereal skies, luminous clouds and Makoto Shinkai aesthetics",
        icon = Icons.Default.Face,
        accentColor = Color(0xFFF472B6),
        promptSuffix = "stunning anime visual aesthetic, Makoto Shinkai style, CoMix Wave films, luminous vibrant skies, radiant lighting, highly detailed anime wallpaper"
    ),
    NATURE(
        title = "Nature",
        description = "Bioluminescent botanical paradises and crystalline waters",
        icon = Icons.Default.Landscape,
        accentColor = Color(0xFF4ADE80),
        promptSuffix = "breathtaking nature landscape, crystal clear waterfalls, bioluminescent botanical flora, lush atmosphere, golden hour sun rays, pristine organic beauty, 8k"
    ),
    FANTASY(
        title = "Fantasy",
        description = "Mystical floating realms, glowing ancient runes and magic",
        icon = Icons.Default.AutoAwesome,
        accentColor = Color(0xFFA78BFA),
        promptSuffix = "ethereal high fantasy realm, mythical floating crystal islands, glowing ancient magical runes, enchanted celestial aurora, Greg Rutkowski artstation masterpiece"
    ),
    MINIMAL(
        title = "Minimal",
        description = "Clean liquid forms, subtle glass refractions and soothing space",
        icon = Icons.Default.Lightbulb,
        accentColor = Color(0xFFE2E8F0),
        promptSuffix = "minimalist liquid glass art, clean elegant aesthetic, subtle translucent refraction, smooth fluid curves, soft studio lighting, architectural minimalism, 8k"
    ),
    NEON(
        title = "Neon",
        description = "Cyberpunk nocturnal cityscapes with glowing laser refractions",
        icon = Icons.Default.Flare,
        accentColor = Color(0xFFF43F5E),
        promptSuffix = "cyberpunk neon nocturnal aesthetic, glowing neon cyan and magenta laser lights, wet asphalt rain reflections, holographic prisms, futuristic synthwave vibe"
    ),
    LUXURY(
        title = "Luxury",
        description = "Polished black obsidian, liquid gold ribbons and diamond facets",
        icon = Icons.Default.Diamond,
        accentColor = Color(0xFFFBBF24),
        promptSuffix = "ultra luxury aesthetic, polished obsidian black marble, swirling liquid 24k gold veins, sparkling diamond refraction, elegant high fashion studio lighting"
    ),
    SPACE(
        title = "Space",
        description = "Deep cosmic nebulae, iridescent galactic rings and stardust",
        icon = Icons.Default.Public,
        accentColor = Color(0xFF818CF8),
        promptSuffix = "deep cosmic space nebula, supermassive celestial black hole accretion disk, vibrant stardust clouds, interstellar aurora, James Webb telescope 8k capture"
    )
}

enum class AspectRatioType(
    val title: String,
    val ratioLabel: String,
    val width: Int,
    val height: Int,
    val ratioFloat: Float,
    val geminiValue: String
) {
    PHONE("Phone", "9:16", 1080, 1920, 9f / 16f, "9:16"),
    SQUARE("Square", "1:1", 1080, 1080, 1f, "1:1"),
    DESKTOP("Desktop", "16:9", 1920, 1080, 16f / 9f, "16:9")
}

data class GenerationState(
    val isGenerating: Boolean = false,
    val currentStep: String = "",
    val progress: Float = 0f,
    val error: String? = null
)
