package com.toufiq.banglaayat.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.toufiq.banglaayat.data.model.QuranResponse
import com.toufiq.banglaayat.ui.surah.SurahScreen
import com.toufiq.banglaayat.ui.viewmodel.QuranUiState
import com.toufiq.banglaayat.ui.viewmodel.QuranViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    viewModel: QuranViewModel = hiltViewModel(),
    onNavigateToSurah: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var isRefreshing by remember { mutableStateOf(false) }
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

    LaunchedEffect(Unit) {
        viewModel.loadQuranAyah(1, 2)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Bar with Random Button
        TopAppBar(
            title = { Text("Quran Ayat") },
            actions = {
                IconButton(
                    onClick = {
                        isRefreshing = true
                        val randomSurah = Random.nextInt(1, 115) // 1 to 114
                        val maxAyah = when (randomSurah) {
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
                            else -> 100 // Default value for other surahs
                        }
                        val randomAyah = Random.nextInt(1, maxAyah + 1)
                        viewModel.loadQuranAyah(randomSurah, randomAyah)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Random Ayat",
                        modifier = Modifier.rotate(if (isRefreshing) rotation else 0f)
                    )
                }
            }
        )

        when (uiState) {
            is QuranUiState.Initial -> Unit
            is QuranUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is QuranUiState.Success -> {
                val data = (uiState as QuranUiState.Success).data
                LaunchedEffect(data) {
                    isRefreshing = false
                }
                QuranContent(
                    data = data,
                    onSurahClick = onNavigateToSurah
                )
            }
            is QuranUiState.Error -> {
                val message = (uiState as QuranUiState.Error).message
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
                        Button(
                            onClick = {
                                isRefreshing = true
                                viewModel.loadQuranAyah(1, 2)
                            }
                        ) {
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranContent(
    data: QuranResponse,
    onSurahClick: (Int) -> Unit
) {
    val context = LocalContext.current
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
            item {
                Text(
                    text = data.surahName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSurahClick(data.surahNo) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data.surahNameArabic,
                    fontSize = 20.sp,
                    modifier = Modifier.clickable { onSurahClick(data.surahNo) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data.surahNameArabicLong,
                    fontSize = 18.sp,
                    modifier = Modifier.clickable { onSurahClick(data.surahNo) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Translation: ${data.surahNameTranslation}",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Revelation Place: ${data.revelationPlace}",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Total Ayah: ${data.totalAyah}",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Arabic Text:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data.arabic1,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data.arabic2,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Translations:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "English: ${data.english}",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bengali: ${data.bengali}",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Urdu: ${data.urdu}",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Audio Reciters:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    data.audio.forEach { (_, reciter) ->
                        ElevatedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(reciter.url))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = reciter.reciter,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
} 