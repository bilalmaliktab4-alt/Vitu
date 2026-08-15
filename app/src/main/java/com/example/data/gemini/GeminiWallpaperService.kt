package com.example.data.gemini

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.AspectRatioType
import com.example.data.model.WallpaperStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

class GeminiWallpaperService(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val apiKey: String
        get() = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

    suspend fun enhancePrompt(userPrompt: String, style: WallpaperStyle): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext defaultPromptEnhancement(userPrompt, style)
        }

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val systemPrompt = "You are an elite creative AI wallpaper prompt designer. Take the user's input and style: '${style.title}'. " +
                    "Generate a single, vivid, ultra-high-detail prompt focusing on futuristic liquid glass, lighting, reflections, color harmony, and composition. " +
                    "Respond with ONLY the enhanced prompt string (max 60 words), no markdown, no quotes."

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "$systemPrompt\n\nUser Idea: $userPrompt\nStyle: ${style.title}\nStyle Details: ${style.promptSuffix}")
                            })
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url(endpoint)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val respString = response.body?.string() ?: ""
                val jsonResp = JSONObject(respString)
                val candidates = jsonResp.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val parts = candidates.getJSONObject(0).getJSONObject("content").getJSONArray("parts")
                    val text = parts.getJSONObject(0).optString("text")
                    if (text.isNotBlank()) {
                        return@withContext text.trim()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Prompt enhance error: ${e.message}")
        }
        return@withContext defaultPromptEnhancement(userPrompt, style)
    }

    suspend fun generateImageWithGemini(
        prompt: String,
        aspectRatio: AspectRatioType
    ): String? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext null
        }

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-image:generateContent?key=$apiKey"
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseModalities", JSONArray().apply {
                        put("TEXT")
                        put("IMAGE")
                    })
                    put("imageConfig", JSONObject().apply {
                        put("aspectRatio", aspectRatio.geminiValue)
                    })
                })
            }

            val request = Request.Builder()
                .url(endpoint)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val respString = response.body?.string() ?: ""
                val jsonResp = JSONObject(respString)
                val candidates = jsonResp.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val parts = candidates.getJSONObject(0).getJSONObject("content").getJSONArray("parts")
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        if (part.has("inlineData")) {
                            val inlineData = part.getJSONObject("inlineData")
                            val base64Data = inlineData.optString("data")
                            if (base64Data.isNotBlank()) {
                                return@withContext saveBase64Image(base64Data)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Image gen error: ${e.message}")
        }
        return@withContext null
    }

    private fun saveBase64Image(base64Str: String): String? {
        return try {
            val imageBytes = Base64.decode(base64Str, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            saveBitmapToFile(bitmap)
        } catch (e: Exception) {
            Log.e("GeminiService", "Failed to decode/save image: ${e.message}")
            null
        }
    }

    fun saveBitmapToFile(bitmap: Bitmap): String {
        val fileName = "liquid_wallpaper_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, fileName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        }
        return file.absolutePath
    }

    private fun defaultPromptEnhancement(userPrompt: String, style: WallpaperStyle): String {
        val clean = if (userPrompt.isBlank()) "liquid glass celestial refraction" else userPrompt.trim()
        return "$clean, ${style.promptSuffix}, liquid glassmorphic crystal refraction, pristine specular highlights, chromatic dispersion, volumetric 8k masterpiece"
    }
}
