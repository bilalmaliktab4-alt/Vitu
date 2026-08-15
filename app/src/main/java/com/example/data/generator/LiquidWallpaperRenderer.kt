package com.example.data.generator

import android.content.Context
import android.graphics.*
import com.example.data.model.AspectRatioType
import com.example.data.model.WallpaperStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.math.*
import kotlin.random.Random

class LiquidWallpaperRenderer(private val context: Context) {

    suspend fun renderProceduralLiquidWallpaper(
        prompt: String,
        style: WallpaperStyle,
        aspectRatio: AspectRatioType
    ): String = withContext(Dispatchers.Default) {
        val width = when (aspectRatio) {
            AspectRatioType.PHONE -> 1080
            AspectRatioType.SQUARE -> 1080
            AspectRatioType.DESKTOP -> 1920
        }
        val height = when (aspectRatio) {
            AspectRatioType.PHONE -> 1920
            AspectRatioType.SQUARE -> 1080
            AspectRatioType.DESKTOP -> 1080
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val seed = (prompt.hashCode().toLong() + System.currentTimeMillis()).hashCode()
        val rng = Random(seed)

        // 1. Draw Deep Ambient Dark Base
        val bgPaint = Paint().apply { isAntiAlias = true }
        val baseColors = when (style) {
            WallpaperStyle.CINEMATIC -> intArrayOf(
                Color.rgb(10, 15, 26),
                Color.rgb(5, 7, 15),
                Color.rgb(2, 3, 8)
            )
            WallpaperStyle.REALISTIC -> intArrayOf(
                Color.rgb(18, 24, 38),
                Color.rgb(10, 14, 23),
                Color.rgb(5, 8, 14)
            )
            WallpaperStyle.ANIME -> intArrayOf(
                Color.rgb(28, 16, 48),
                Color.rgb(15, 10, 32),
                Color.rgb(8, 5, 20)
            )
            WallpaperStyle.NATURE -> intArrayOf(
                Color.rgb(10, 32, 24),
                Color.rgb(6, 20, 16),
                Color.rgb(3, 10, 8)
            )
            WallpaperStyle.FANTASY -> intArrayOf(
                Color.rgb(32, 14, 48),
                Color.rgb(18, 8, 30),
                Color.rgb(8, 4, 16)
            )
            WallpaperStyle.MINIMAL -> intArrayOf(
                Color.rgb(24, 28, 36),
                Color.rgb(14, 16, 22),
                Color.rgb(7, 8, 12)
            )
            WallpaperStyle.NEON -> intArrayOf(
                Color.rgb(16, 5, 28),
                Color.rgb(8, 3, 18),
                Color.rgb(4, 1, 10)
            )
            WallpaperStyle.LUXURY -> intArrayOf(
                Color.rgb(20, 16, 12),
                Color.rgb(12, 9, 6),
                Color.rgb(4, 3, 2)
            )
            WallpaperStyle.SPACE -> intArrayOf(
                Color.rgb(8, 10, 24),
                Color.rgb(4, 5, 16),
                Color.rgb(2, 2, 8)
            )
        }

        val baseGradient = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            baseColors,
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        bgPaint.shader = baseGradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 2. Liquid Chromatic Gradient Blobs (Multiple soft glowing orbs)
        val orbCount = rng.nextInt(4, 8)
        val orbPalette = getStyleColorPalette(style, rng)

        for (i in 0 until orbCount) {
            val cx = rng.nextFloat() * width
            val cy = rng.nextFloat() * height
            val radius = rng.nextFloat() * (min(width, height) * 0.7f) + min(width, height) * 0.3f
            val orbColor = orbPalette[rng.nextInt(orbPalette.size)]

            val radialPaint = Paint().apply {
                isAntiAlias = true
                shader = RadialGradient(
                    cx, cy, radius,
                    intArrayOf(
                        Color.argb(rng.nextInt(120, 200), Color.red(orbColor), Color.green(orbColor), Color.blue(orbColor)),
                        Color.argb(rng.nextInt(40, 90), Color.red(orbColor), Color.green(orbColor), Color.blue(orbColor)),
                        Color.TRANSPARENT
                    ),
                    floatArrayOf(0f, 0.55f, 1f),
                    Shader.TileMode.CLAMP
                )
                xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
            }
            canvas.drawCircle(cx, cy, radius, radialPaint)
        }

        // 3. Fluid Glass Refraction Curves / Liquid Waves
        drawLiquidGlassRibbons(canvas, width, height, style, orbPalette, rng)

        // 4. Floating 3D Liquid Crystal Prisms / Geometric Glass Facets
        drawGlassPrismsAndSpheres(canvas, width, height, style, orbPalette, rng)

        // 5. Specular Highlights, Light Flares and Micro Glass Shimmer
        drawSpecularFlaresAndStars(canvas, width, height, orbPalette, rng)

        // 6. Vignette and Cinematic Tone Mapping
        drawCinematicVignette(canvas, width, height)

        // Save to file
        val fileName = "liquid_wallpaper_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, fileName)
        withContext(Dispatchers.IO) {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 96, out)
            }
        }
        bitmap.recycle()
        file.absolutePath
    }

    private fun getStyleColorPalette(style: WallpaperStyle, rng: Random): List<Int> {
        return when (style) {
            WallpaperStyle.CINEMATIC -> listOf(
                Color.rgb(56, 189, 248),  // Cyan
                Color.rgb(249, 115, 22),  // Amber orange flare
                Color.rgb(99, 102, 241),  // Indigo
                Color.rgb(224, 242, 254)   // Ice white
            )
            WallpaperStyle.REALISTIC -> listOf(
                Color.rgb(52, 211, 153),  // Emerald
                Color.rgb(56, 189, 248),  // Sky
                Color.rgb(251, 191, 36),  // Golden
                Color.rgb(248, 250, 252)   // Pure white
            )
            WallpaperStyle.ANIME -> listOf(
                Color.rgb(244, 114, 182), // Sakura Pink
                Color.rgb(168, 85, 247),  // Purple
                Color.rgb(56, 189, 248),  // Sky Blue
                Color.rgb(254, 240, 138)  // Dawn Gold
            )
            WallpaperStyle.NATURE -> listOf(
                Color.rgb(74, 222, 128),  // Emerald
                Color.rgb(45, 212, 191),  // Teal
                Color.rgb(250, 204, 21),  // Sunlight
                Color.rgb(192, 132, 252)  // Orchid flora
            )
            WallpaperStyle.FANTASY -> listOf(
                Color.rgb(167, 139, 250), // Mystic Violet
                Color.rgb(244, 63, 94),   // Rose Rune
                Color.rgb(34, 211, 238),  // Mana Cyan
                Color.rgb(253, 230, 138)  // Ethereal Gold
            )
            WallpaperStyle.MINIMAL -> listOf(
                Color.rgb(226, 232, 240), // Platinum
                Color.rgb(148, 163, 184), // Slate
                Color.rgb(96, 165, 250),  // Subdued Ice
                Color.rgb(255, 255, 255)  // Specular White
            )
            WallpaperStyle.NEON -> listOf(
                Color.rgb(244, 63, 94),   // Neon Pink
                Color.rgb(6, 182, 212),   // Electric Cyan
                Color.rgb(168, 85, 247),  // UV Purple
                Color.rgb(234, 179, 8)    // Cyber Yellow
            )
            WallpaperStyle.LUXURY -> listOf(
                Color.rgb(251, 191, 36),  // Imperial Gold
                Color.rgb(245, 158, 11),  // Warm Amber
                Color.rgb(217, 119, 6),   // Deep Bronze
                Color.rgb(255, 255, 255)  // Diamond Flashes
            )
            WallpaperStyle.SPACE -> listOf(
                Color.rgb(129, 140, 248), // Nebula Indigo
                Color.rgb(236, 72, 153),  // Galactic Magenta
                Color.rgb(56, 189, 248),  // Starlight Cyan
                Color.rgb(255, 255, 255)  // Star Core
            )
        }
    }

    private fun drawLiquidGlassRibbons(
        canvas: Canvas,
        width: Int,
        height: Int,
        style: WallpaperStyle,
        palette: List<Int>,
        rng: Random
    ) {
        val ribbonCount = rng.nextInt(3, 6)
        for (r in 0 until ribbonCount) {
            val path = Path()
            val startY = rng.nextFloat() * height * 0.8f + height * 0.1f
            path.moveTo(-100f, startY)

            val segments = 4
            var currentX = -100f
            var currentY = startY

            val points = mutableListOf<PointF>()
            points.add(PointF(currentX, currentY))

            for (s in 1..segments) {
                currentX += (width + 200f) / segments
                currentY = startY + sin((s + r) * 1.2f) * (height * 0.22f) + rng.nextFloat() * 80f - 40f
                points.add(PointF(currentX, currentY))
            }

            for (i in 1 until points.size) {
                val p0 = points[i - 1]
                val p1 = points[i]
                val cx = (p0.x + p1.x) / 2f
                val cy = (p0.y + p1.y) / 2f
                path.quadTo(p0.x, p0.y, cx, cy)
            }
            path.lineTo(width + 100f, height + 100f)
            path.lineTo(-100f, height + 100f)
            path.close()

            // Translucent glass fill with specular refraction gradient
            val col = palette[r % palette.size]
            val ribbonPaint = Paint().apply {
                isAntiAlias = true
                shader = LinearGradient(
                    0f, startY - 100f, width.toFloat(), startY + height * 0.3f,
                    intArrayOf(
                        Color.argb(rng.nextInt(50, 90), Color.red(col), Color.green(col), Color.blue(col)),
                        Color.argb(rng.nextInt(15, 45), 255, 255, 255),
                        Color.argb(rng.nextInt(5, 25), Color.red(col), Color.green(col), Color.blue(col))
                    ),
                    floatArrayOf(0f, 0.4f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawPath(path, ribbonPaint)

            // Luminous glass rim/border
            val rimPaint = Paint().apply {
                isAntiAlias = true
                setStyle(Paint.Style.STROKE)
                strokeWidth = rng.nextFloat() * 3.5f + 1.5f
                shader = LinearGradient(
                    0f, startY, width.toFloat(), startY + 200f,
                    intArrayOf(
                        Color.argb(220, 255, 255, 255),
                        Color.argb(160, Color.red(col), Color.green(col), Color.blue(col)),
                        Color.argb(40, 255, 255, 255)
                    ),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawPath(path, rimPaint)
        }
    }

    private fun drawGlassPrismsAndSpheres(
        canvas: Canvas,
        width: Int,
        height: Int,
        style: WallpaperStyle,
        palette: List<Int>,
        rng: Random
    ) {
        val objectCount = rng.nextInt(5, 10)
        for (i in 0 until objectCount) {
            val cx = rng.nextFloat() * (width * 0.8f) + width * 0.1f
            val cy = rng.nextFloat() * (height * 0.75f) + height * 0.12f
            val radius = rng.nextFloat() * (min(width, height) * 0.18f) + 40f
            val col = palette[rng.nextInt(palette.size)]

            // 3D Glass Sphere / Prism with refraction highlight
            val glassBodyPaint = Paint().apply {
                isAntiAlias = true
                shader = RadialGradient(
                    cx - radius * 0.35f, cy - radius * 0.35f, radius * 1.3f,
                    intArrayOf(
                        Color.argb(180, 255, 255, 255),
                        Color.argb(120, Color.red(col), Color.green(col), Color.blue(col)),
                        Color.argb(40, 255, 255, 255),
                        Color.argb(90, 15, 20, 35)
                    ),
                    floatArrayOf(0f, 0.35f, 0.75f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawCircle(cx, cy, radius, glassBodyPaint)

            // Specular glass rim with dual refraction
            val glassRimPaint = Paint().apply {
                isAntiAlias = true
                setStyle(Paint.Style.STROKE)
                strokeWidth = rng.nextFloat() * 3f + 1.5f
                shader = LinearGradient(
                    cx - radius, cy - radius, cx + radius, cy + radius,
                    intArrayOf(
                        Color.argb(230, 255, 255, 255),
                        Color.argb(120, Color.red(col), Color.green(col), Color.blue(col)),
                        Color.argb(50, 255, 255, 255)
                    ),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawCircle(cx, cy, radius, glassRimPaint)

            // Internal luminous refraction crescent
            val specularCrescent = Paint().apply {
                isAntiAlias = true
                color = Color.argb(200, 255, 255, 255)
            }
            canvas.drawCircle(cx - radius * 0.38f, cy - radius * 0.38f, radius * 0.18f, specularCrescent)
        }
    }

    private fun drawSpecularFlaresAndStars(
        canvas: Canvas,
        width: Int,
        height: Int,
        palette: List<Int>,
        rng: Random
    ) {
        val flareCount = rng.nextInt(15, 30)
        val flarePaint = Paint().apply {
            isAntiAlias = true
            xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
        }

        for (i in 0 until flareCount) {
            val fx = rng.nextFloat() * width
            val fy = rng.nextFloat() * height
            val fSize = rng.nextFloat() * 12f + 3f
            val col = palette[rng.nextInt(palette.size)]

            flarePaint.color = Color.argb(rng.nextInt(160, 255), Color.red(col), Color.green(col), Color.blue(col))
            canvas.drawCircle(fx, fy, fSize, flarePaint)

            // Cross star spikes for large flares
            if (fSize > 8f) {
                val spikeLength = fSize * 3.5f
                val spikePaint = Paint().apply {
                    isAntiAlias = true
                    color = Color.argb(190, 255, 255, 255)
                    strokeWidth = 1.8f
                }
                canvas.drawLine(fx - spikeLength, fy, fx + spikeLength, fy, spikePaint)
                canvas.drawLine(fx, fy - spikeLength, fx, fy + spikeLength, spikePaint)
            }
        }
    }

    private fun drawCinematicVignette(canvas: Canvas, width: Int, height: Int) {
        val cx = width / 2f
        val cy = height / 2f
        val maxDim = max(width, height).toFloat()

        val vignettePaint = Paint().apply {
            isAntiAlias = true
            shader = RadialGradient(
                cx, cy, maxDim * 0.75f,
                intArrayOf(Color.TRANSPARENT, Color.argb(140, 0, 0, 0), Color.argb(220, 0, 0, 0)),
                floatArrayOf(0f, 0.65f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), vignettePaint)
    }
}
