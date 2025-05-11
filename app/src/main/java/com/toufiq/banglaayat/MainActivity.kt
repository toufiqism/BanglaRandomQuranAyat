package com.toufiq.banglaayat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.toufiq.banglaayat.ui.surah.SurahScreen
import com.toufiq.banglaayat.ui.theme.BanglaRandomQuranAyatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BanglaRandomQuranAyatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentSurahNumber by remember { mutableStateOf(1) }
                    SurahScreen(
                        surahNumber = currentSurahNumber,
                        onRandomSurah = { currentSurahNumber = it }
                    )
                }
            }
        }
    }
}