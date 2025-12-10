package com.toufiq.banglaayat.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.toufiq.banglaayat.data.model.AudioReciter
import com.toufiq.banglaayat.data.model.QuranResponse
import com.toufiq.banglaayat.ui.viewmodel.QuranUiState
import com.toufiq.banglaayat.ui.viewmodel.QuranViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    viewModel: QuranViewModel = hiltViewModel(),
    onNavigateToSurah: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadQuranAyah(1, 2)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        QuranTopBar(
            isRefreshing = isRefreshing,
            onRefreshClick = remember(viewModel) {
                {
                    isRefreshing = true
                    val randomSurah = Random.nextInt(1, 115)
                    val maxAyah = getMaxAyahForSurah(randomSurah)
                    val randomAyah = Random.nextInt(1, maxAyah + 1)
                    viewModel.loadQuranAyah(randomSurah, randomAyah)
                }
            }
        )

        when (val state = uiState) {
            is QuranUiState.Initial -> Unit
            is QuranUiState.Loading -> LoadingContent()
            is QuranUiState.Success -> {
                LaunchedEffect(state.data) {
                    isRefreshing = false
                }
                QuranContent(
                    data = state.data,
                    onSurahClick = onNavigateToSurah
                )
            }
            is QuranUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry = remember(viewModel) {
                        {
                            isRefreshing = true
                            viewModel.loadQuranAyah(1, 2)
                        }
                    }
                )
            }
        }
    }
}

private fun getMaxAyahForSurah(surahNumber: Int): Int = when (surahNumber) {
    1 -> 7
    2 -> 286
    3 -> 200
    4 -> 176
    5 -> 120
    6 -> 165
    7 -> 206
    8 -> 75
    9 -> 129
    10 -> 109
    else -> 100
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuranTopBar(
    isRefreshing: Boolean,
    onRefreshClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "refresh")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    TopAppBar(
        title = { Text("Quran Ayat") },
        actions = {
            IconButton(onClick = onRefreshClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Random Ayat",
                    modifier = Modifier.rotate(if (isRefreshing) rotation else 0f)
                )
            }
        }
    )
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error Loading Ayat",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

@Composable
fun QuranContent(
    data: QuranResponse,
    onSurahClick: (Int) -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(data) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item(key = "header") {
                SurahHeaderSection(
                    surahName = data.surahName,
                    surahNameArabic = data.surahNameArabic,
                    surahNameArabicLong = data.surahNameArabicLong,
                    surahNo = data.surahNo,
                    onSurahClick = onSurahClick
                )
            }

            item(key = "info") {
                SurahInfoSection(
                    surahNameTranslation = data.surahNameTranslation,
                    revelationPlace = data.revelationPlace,
                    totalAyah = data.totalAyah
                )
            }

            item(key = "arabic") {
                ArabicTextSection(
                    arabic1 = data.arabic1,
                    arabic2 = data.arabic2
                )
            }

            item(key = "translations") {
                TranslationsSection(
                    english = data.english,
                    bengali = data.bengali,
                    urdu = data.urdu
                )
            }

            item(key = "audio") {
                AudioRecitersSection(audio = data.audio)
            }
        }
    }
}

@Composable
private fun SurahHeaderSection(
    surahName: String,
    surahNameArabic: String,
    surahNameArabicLong: String,
    surahNo: Int,
    onSurahClick: (Int) -> Unit
) {
    val clickModifier = remember(surahNo, onSurahClick) {
        Modifier.clickable { onSurahClick(surahNo) }
    }
    
    Column {
        Text(
            text = surahName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = clickModifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = surahNameArabic,
            fontSize = 20.sp,
            modifier = clickModifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = surahNameArabicLong,
            fontSize = 18.sp,
            modifier = clickModifier
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SurahInfoSection(
    surahNameTranslation: String,
    revelationPlace: String,
    totalAyah: Int
) {
    Column {
        Text(
            text = "Translation: $surahNameTranslation",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Revelation Place: $revelationPlace",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Total Ayah: $totalAyah",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ArabicTextSection(
    arabic1: String,
    arabic2: String
) {
    Column {
        Text(
            text = "Arabic Text:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = arabic1,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = arabic2,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun TranslationsSection(
    english: String,
    bengali: String,
    urdu: String
) {
    Column {
        Text(
            text = "Translations:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "English: $english",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Bengali: $bengali",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Urdu: $urdu",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AudioRecitersSection(
    audio: Map<String, AudioReciter>
) {
    val context = LocalContext.current
    
    Column {
        Text(
            text = "Audio Reciters:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            audio.forEach { (key, reciter) ->
                key(key) {
                    ReciterButton(
                        reciterName = reciter.reciter,
                        onClick = remember(reciter.url) {
                            {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(reciter.url))
                                context.startActivity(intent)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReciterButton(
    reciterName: String,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = reciterName,
            maxLines = 1
        )
    }
}