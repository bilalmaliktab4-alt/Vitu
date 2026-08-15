package com.example.ui.viewmodel

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.WallpaperEntity
import com.example.data.model.AspectRatioType
import com.example.data.model.GenerationState
import com.example.data.model.WallpaperStyle
import com.example.data.repository.WallpaperRepository
import com.example.ui.components.AuraTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.random.Random

enum class WallpaperTarget {
    HOME,
    LOCK,
    BOTH
}

class WallpaperViewModel(
    private val repository: WallpaperRepository
) : ViewModel() {

    private val _prompt = MutableStateFlow("")
    val prompt: StateFlow<String> = _prompt.asStateFlow()

    private val _selectedStyle = MutableStateFlow(WallpaperStyle.CINEMATIC)
    val selectedStyle: StateFlow<WallpaperStyle> = _selectedStyle.asStateFlow()

    private val _selectedAspectRatio = MutableStateFlow(AspectRatioType.PHONE)
    val selectedAspectRatio: StateFlow<AspectRatioType> = _selectedAspectRatio.asStateFlow()

    private val _generationState = MutableStateFlow(GenerationState())
    val generationState: StateFlow<GenerationState> = _generationState.asStateFlow()

    private val _currentWallpaper = MutableStateFlow<WallpaperEntity?>(null)
    val currentWallpaper: StateFlow<WallpaperEntity?> = _currentWallpaper.asStateFlow()

    private val _auraTheme = MutableStateFlow(AuraTheme.COSMIC_AURORA)
    val auraTheme: StateFlow<AuraTheme> = _auraTheme.asStateFlow()

    private val _blurIntensity = MutableStateFlow(0.85f)
    val blurIntensity: StateFlow<Float> = _blurIntensity.asStateFlow()

    private val _exportQuality = MutableStateFlow("8K Master")
    val exportQuality: StateFlow<String> = _exportQuality.asStateFlow()

    private val _galleryFilterStyle = MutableStateFlow<String?>(null)
    val galleryFilterStyle: StateFlow<String?> = _galleryFilterStyle.asStateFlow()

    val allWallpapers: StateFlow<List<WallpaperEntity>> = repository.allWallpapers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteWallpapers: StateFlow<List<WallpaperEntity>> = repository.favoriteWallpapers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val curatedPrompts = listOf(
        "Futuristic cybernetic samurai gazing across rainy neon megalopolis",
        "Liquid obsidian and swirling 24k gold veins with diamond refraction",
        "Ethereal bioluminescent giant lotus floating in crystal galaxy ocean",
        "Makoto Shinkai style floating island with glowing cherry blossoms and twilight clouds",
        "Prismatic crystal geometric hypercube refracting volumetric dawn sunbeams",
        "Mythical celestial dragon breathing luminous cyan aurora stardust",
        "Deep space supernova explosion with liquid glass planetary rings in 8k",
        "Minimalist liquid glass water droplet causing concentric glowing ripples on black mirror",
        "Enchanted crystal cavern with glowing emerald moss and subterranean waterfall",
        "Cyberpunk flying sports car drifting above holograms in neo-Tokyo rain"
    )

    init {
        viewModelScope.launch {
            repository.seedInitialWallpapersIfEmpty()
        }
    }

    fun onPromptChanged(newPrompt: String) {
        _prompt.value = newPrompt
    }

    fun onStyleSelected(style: WallpaperStyle) {
        _selectedStyle.value = style
    }

    fun onAspectRatioSelected(aspectRatio: AspectRatioType) {
        _selectedAspectRatio.value = aspectRatio
    }

    fun setAuraTheme(theme: AuraTheme) {
        _auraTheme.value = theme
    }

    fun setBlurIntensity(intensity: Float) {
        _blurIntensity.value = intensity
    }

    fun setExportQuality(quality: String) {
        _exportQuality.value = quality
    }

    fun setGalleryFilterStyle(styleName: String?) {
        _galleryFilterStyle.value = styleName
    }

    fun selectWallpaperForView(wallpaper: WallpaperEntity) {
        _currentWallpaper.value = wallpaper
    }

    fun randomizePrompt() {
        val sample = curatedPrompts.random()
        _prompt.value = sample
    }

    fun generateWallpaper(onSuccess: (WallpaperEntity) -> Unit) {
        val currentP = _prompt.value
        val currentS = _selectedStyle.value
        val currentA = _selectedAspectRatio.value

        _generationState.value = GenerationState(
            isGenerating = true,
            currentStep = "Initiating Liquid Glass Synthesis...",
            progress = 0.1f,
            error = null
        )

        viewModelScope.launch {
            try {
                val generated = repository.generateWallpaper(
                    prompt = currentP,
                    style = currentS,
                    aspectRatio = currentA,
                    onProgress = { step, prog ->
                        _generationState.value = _generationState.value.copy(
                            currentStep = step,
                            progress = prog
                        )
                    }
                )
                _currentWallpaper.value = generated
                _generationState.value = GenerationState(isGenerating = false)
                onSuccess(generated)
            } catch (e: Exception) {
                _generationState.value = GenerationState(
                    isGenerating = false,
                    error = e.message ?: "Generation failed. Please try again."
                )
            }
        }
    }

    fun toggleFavorite(wallpaper: WallpaperEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(wallpaper.id, !wallpaper.isFavorite)
            if (_currentWallpaper.value?.id == wallpaper.id) {
                _currentWallpaper.value = _currentWallpaper.value?.copy(isFavorite = !wallpaper.isFavorite)
            }
        }
    }

    fun deleteWallpaper(wallpaper: WallpaperEntity) {
        viewModelScope.launch {
            repository.deleteWallpaper(wallpaper.id)
            if (_currentWallpaper.value?.id == wallpaper.id) {
                _currentWallpaper.value = null
            }
        }
    }

    fun setAsDeviceWallpaper(
        context: Context,
        wallpaper: WallpaperEntity,
        target: WallpaperTarget,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bitmap = loadWallpaperBitmap(context, wallpaper)
                if (bitmap != null) {
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val flags = when (target) {
                            WallpaperTarget.HOME -> WallpaperManager.FLAG_SYSTEM
                            WallpaperTarget.LOCK -> WallpaperManager.FLAG_LOCK
                            WallpaperTarget.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                        }
                        wallpaperManager.setBitmap(bitmap, null, true, flags)
                    } else {
                        wallpaperManager.setBitmap(bitmap)
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Wallpaper successfully applied!", Toast.LENGTH_SHORT).show()
                        onComplete(true)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to decode wallpaper image", Toast.LENGTH_SHORT).show()
                        onComplete(false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error setting wallpaper: ${e.message}", Toast.LENGTH_SHORT).show()
                    onComplete(false)
                }
            }
        }
    }

    fun saveWallpaperToGallery(context: Context, wallpaper: WallpaperEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bitmap = loadWallpaperBitmap(context, wallpaper) ?: return@launch
                val filename = "LiquidWallpaper_${System.currentTimeMillis()}.jpg"

                var fos: OutputStream? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val resolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AIWallpapers")
                    }
                    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    if (imageUri != null) {
                        fos = resolver.openOutputStream(imageUri)
                    }
                } else {
                    val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/AIWallpapers"
                    val file = File(imagesDir)
                    if (!file.exists()) file.mkdirs()
                    val image = File(imagesDir, filename)
                    fos = FileOutputStream(image)
                }

                fos?.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Saved to Gallery / Pictures!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun shareWallpaper(context: Context, wallpaper: WallpaperEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bitmap = loadWallpaperBitmap(context, wallpaper) ?: return@launch
                val cachePath = File(context.cacheDir, "shared_wallpapers")
                cachePath.mkdirs()
                val tempFile = File(cachePath, "liquid_share_${System.currentTimeMillis()}.jpg")
                FileOutputStream(tempFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                }

                val contentUri: Uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    tempFile
                )

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    putExtra(Intent.EXTRA_TEXT, "✨ Created with AI Wallpaper Maker Liquid Glass:\n\n\"${wallpaper.prompt}\" (${wallpaper.style})")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                withContext(Dispatchers.Main) {
                    context.startActivity(Intent.createChooser(shareIntent, "Share Liquid Glass Wallpaper"))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Fallback to sharing text prompt
                    val textShare = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "AI Wallpaper Prompt: \"${wallpaper.prompt}\" (${wallpaper.style})")
                    }
                    context.startActivity(Intent.createChooser(textShare, "Share Wallpaper Prompt"))
                }
            }
        }
    }

    private fun loadWallpaperBitmap(context: Context, wallpaper: WallpaperEntity): Bitmap? {
        return if (wallpaper.drawableResId != null) {
            BitmapFactory.decodeResource(context.resources, wallpaper.drawableResId)
        } else if (wallpaper.imagePath.isNotBlank()) {
            BitmapFactory.decodeFile(wallpaper.imagePath)
        } else {
            null
        }
    }

    companion object {
        fun provideFactory(
            context: Context,
            repository: WallpaperRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WallpaperViewModel(repository) as T
            }
        }
    }
}
