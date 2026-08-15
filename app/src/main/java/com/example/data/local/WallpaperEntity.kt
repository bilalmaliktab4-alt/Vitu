package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallpapers")
data class WallpaperEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val prompt: String,
    val enhancedPrompt: String,
    val style: String,
    val aspectRatio: String,
    val imagePath: String,
    val drawableResId: Int? = null,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
