package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.data.gemini.GeminiWallpaperService
import com.example.data.generator.LiquidWallpaperRenderer
import com.example.data.local.AppDatabase
import com.example.data.repository.WallpaperRepository
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.WallpaperViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: WallpaperViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val geminiService = GeminiWallpaperService(applicationContext)
        val renderer = LiquidWallpaperRenderer(applicationContext)
        val repository = WallpaperRepository(
            context = applicationContext,
            wallpaperDao = db.wallpaperDao(),
            geminiService = geminiService,
            renderer = renderer
        )
        WallpaperViewModel.provideFactory(applicationContext, repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF06080E)
                ) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
