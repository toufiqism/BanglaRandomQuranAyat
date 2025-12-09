package com.toufiq.banglaayat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.toufiq.banglaayat.ui.screen.QuranScreen
import com.toufiq.banglaayat.ui.surah.SurahScreen
import com.toufiq.banglaayat.ui.theme.BanglaRandomQuranAyatTheme
import dagger.hilt.android.AndroidEntryPoint

enum class Screen {
    Quran, Surah
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BanglaRandomQuranAyatTheme {
                var currentScreen by remember { mutableStateOf(Screen.Surah) }
                var currentSurahNumber by remember { mutableStateOf(1) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentScreen == Screen.Surah,
                                onClick = { currentScreen = Screen.Surah },
                                icon = { Icon(Icons.Default.List, contentDescription = "Surah") },
                                label = { Text("Full Surah") }
                            )
                            NavigationBarItem(
                                selected = currentScreen == Screen.Quran,
                                onClick = { currentScreen = Screen.Quran },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Ayat") },
                                label = { Text("Random Ayat") }
                            )
                        }
                    }
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when (currentScreen) {
                            Screen.Quran -> QuranScreen(
                                onNavigateToSurah = { surahNo ->
                                    currentSurahNumber = surahNo
                                    currentScreen = Screen.Surah
                                }
                            )
                            Screen.Surah -> SurahScreen(
                                surahNumber = currentSurahNumber,
                                onRandomSurah = { currentSurahNumber = it }
                            )
                        }
                    }
                }
            }
        }
    }
}