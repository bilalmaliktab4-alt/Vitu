package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.gemini.GeminiWallpaperService
import com.example.data.generator.LiquidWallpaperRenderer
import com.example.data.local.WallpaperDao
import com.example.data.local.WallpaperEntity
import com.example.data.model.AspectRatioType
import com.example.data.model.WallpaperStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class WallpaperRepository(
    private val context: Context,
    private val wallpaperDao: WallpaperDao,
    private val geminiService: GeminiWallpaperService,
    private val renderer: LiquidWallpaperRenderer
) {
    val allWallpapers: Flow<List<WallpaperEntity>> = wallpaperDao.getAllWallpapers()
    val favoriteWallpapers: Flow<List<WallpaperEntity>> = wallpaperDao.getFavoriteWallpapers()

    suspend fun getWallpapersByStyle(style: String): Flow<List<WallpaperEntity>> {
        return wallpaperDao.getWallpapersByStyle(style)
    }

    suspend fun getWallpaperById(id: Int): WallpaperEntity? {
        return wallpaperDao.getWallpaperById(id)
    }

    suspend fun toggleFavorite(id: Int, isFavorite: Boolean) {
        wallpaperDao.updateFavorite(id, isFavorite)
    }

    suspend fun deleteWallpaper(id: Int) {
        wallpaperDao.deleteWallpaperById(id)
    }

    suspend fun generateWallpaper(
        prompt: String,
        style: WallpaperStyle,
        aspectRatio: AspectRatioType,
        onProgress: (String, Float) -> Unit
    ): WallpaperEntity {
        onProgress("Consulting AI Prompt Enhancer...", 0.2f)
        val enhancedPrompt = geminiService.enhancePrompt(prompt, style)

        onProgress("Synthesizing chromatic liquid glass...", 0.5f)
        var imagePath = geminiService.generateImageWithGemini(enhancedPrompt, aspectRatio)

        if (imagePath == null) {
            onProgress("Rendering 8K crystal caustics & refractive layers...", 0.75f)
            imagePath = renderer.renderProceduralLiquidWallpaper(enhancedPrompt, style, aspectRatio)
        }

        onProgress("Applying liquid glass polish...", 0.95f)
        val entity = WallpaperEntity(
            prompt = prompt.ifBlank { "Futuristic Liquid Glass" },
            enhancedPrompt = enhancedPrompt,
            style = style.name,
            aspectRatio = aspectRatio.ratioLabel,
            imagePath = imagePath,
            drawableResId = null,
            isFavorite = false,
            createdAt = System.currentTimeMillis()
        )

        val id = wallpaperDao.insertWallpaper(entity)
        return entity.copy(id = id.toInt())
    }

    suspend fun seedInitialWallpapersIfEmpty() {
        val count = wallpaperDao.getCount()
        if (count == 0) {
            val starterItems = listOf(
                WallpaperEntity(
                    prompt = "Cinematic Liquid Glass Sphere in Neon City",
                    enhancedPrompt = "Cinematic liquid glass sphere floating over misty hyper-realistic neon cyber city in rain, volumetric lighting, 8k octane render",
                    style = WallpaperStyle.CINEMATIC.name,
                    aspectRatio = "9:16",
                    imagePath = "",
                    drawableResId = R.drawable.wallpaper_cinematic_1786811243830,
                    isFavorite = true,
                    createdAt = System.currentTimeMillis() - 100000
                ),
                WallpaperEntity(
                    prompt = "Deep Cosmic Nebula with Liquid Crystal Rings",
                    enhancedPrompt = "Vibrant cosmic nebula with glowing liquid glass floating geometric rings and starlight, deep violet and cyan aurora, 8k wallpaper",
                    style = WallpaperStyle.SPACE.name,
                    aspectRatio = "9:16",
                    imagePath = "",
                    drawableResId = R.drawable.wallpaper_neon_space_1786811256932,
                    isFavorite = true,
                    createdAt = System.currentTimeMillis() - 80000
                ),
                WallpaperEntity(
                    prompt = "Bioluminescent Crystal Waterfall in Glass Forest",
                    enhancedPrompt = "Enchanted ethereal waterfall cascading through translucent liquid glass crystal forest, bioluminescent flora, golden sun rays, 8k",
                    style = WallpaperStyle.NATURE.name,
                    aspectRatio = "9:16",
                    imagePath = "",
                    drawableResId = R.drawable.wallpaper_nature_crystal_1786811272243,
                    isFavorite = false,
                    createdAt = System.currentTimeMillis() - 60000
                ),
                WallpaperEntity(
                    prompt = "Anime Floating Sky Islands & Radiant Lakes",
                    enhancedPrompt = "Stunning anime aesthetic floating sky islands with glowing cherry blossoms and liquid crystal lakes under starry aurora twilight, Makoto Shinkai style",
                    style = WallpaperStyle.ANIME.name,
                    aspectRatio = "9:16",
                    imagePath = "",
                    drawableResId = R.drawable.wallpaper_anime_dream_1786811285455,
                    isFavorite = false,
                    createdAt = System.currentTimeMillis() - 40000
                )
            )

            starterItems.forEach { wallpaperDao.insertWallpaper(it) }
        }
    }
}
