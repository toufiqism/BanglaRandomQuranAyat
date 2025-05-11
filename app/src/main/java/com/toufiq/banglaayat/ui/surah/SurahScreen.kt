package com.toufiq.banglaayat.ui.surah

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toufiq.banglaayat.data.model.AudioReciter
import com.toufiq.banglaayat.data.model.Surah
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahScreen(
    surahNumber: Int,
    onRandomSurah: (Int) -> Unit,
    viewModel: SurahViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var isAudioSectionExpanded by remember { mutableStateOf(false) }
    var showSurahSelector by remember { mutableStateOf(false) }
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

    LaunchedEffect(surahNumber) {
        viewModel.loadSurah(surahNumber)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quran Surah") },
                actions = {
                    // Surah Selector Button
                    IconButton(onClick = { showSurahSelector = true }) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Select Surah"
                        )
                    }
                    // Random Surah Button
                    IconButton(
                        onClick = {
                            isRefreshing = true
                            val randomSurah = Random.nextInt(1, 115)
                            onRandomSurah(randomSurah)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Random Surah",
                            modifier = Modifier.rotate(if (isRefreshing) rotation else 0f)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                isRefreshing = true
                                val randomSurah = Random.nextInt(1, 115)
                                onRandomSurah(randomSurah)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Try Another Surah",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Try Another Surah")
                        }
                    }
                }
                uiState.surah != null -> {
                    LaunchedEffect(uiState.surah) {
                        isRefreshing = false
                    }
                    SurahContent(
                        surah = uiState.surah!!,
                        isAudioSectionExpanded = isAudioSectionExpanded,
                        onAudioSectionExpandedChange = { isAudioSectionExpanded = it }
                    )
                }
            }
        }
    }

    // Surah Selection Dialog
    if (showSurahSelector) {
        AlertDialog(
            onDismissRequest = { showSurahSelector = false },
            title = { Text("Select Surah") },
            text = {
                LazyColumn {
                    items((1..114).toList()) { number ->
                        ListItem(
                            headlineContent = { Text("Surah $number") },
                            modifier = Modifier.clickable {
                                onRandomSurah(number)
                                showSurahSelector = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSurahSelector = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SurahContent(
    surah: Surah,
    isAudioSectionExpanded: Boolean,
    onAudioSectionExpandedChange: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SurahHeader(surah)
        }

        item {
            ExpandableAudioSection(
                audioReciters = surah.audio,
                isExpanded = isAudioSectionExpanded,
                onExpandedChange = onAudioSectionExpandedChange
            )
        }

        items(surah.english.indices.toList()) { index ->
            AyahCard(
                arabic = surah.arabic1[index],
                translation = surah.english[index],
                bengali = surah.bengali[index],
                urdu = surah.urdu[index],
                ayahNumber = index + 1
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandableAudioSection(
    audioReciters: Map<String, AudioReciter>,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedReciter by remember { mutableStateOf<AudioReciter?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandedChange(!isExpanded) },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Audio Recitation",
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedReciter?.reciter ?: "Select Reciter",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                audioReciters.values.forEach { reciter ->
                                    DropdownMenuItem(
                                        text = { Text(reciter.reciter) },
                                        onClick = {
                                            selectedReciter = reciter
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                selectedReciter?.let { reciter ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(reciter.url))
                                    context.startActivity(intent)
                                }
                            },
                            enabled = selectedReciter != null,
                            modifier = Modifier.weight(.5f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Audio"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Play")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SurahHeader(surah: Surah) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = surah.surahNameArabicLong,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${surah.surahName} (${surah.surahNameTranslation})",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Revealed in ${surah.revelationPlace} • ${surah.totalAyah} Verses",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AyahCard(
    arabic: String,
    translation: String,
    bengali: String,
    urdu: String,
    ayahNumber: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = arabic,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = bengali,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = translation,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = urdu,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Verse $ayahNumber",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
} 