package com.toufiq.banglaayat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.toufiq.banglaayat.data.datastore.SettingsDataStore
import com.toufiq.banglaayat.navigation.QuranRoute
import com.toufiq.banglaayat.navigation.SettingsRoute
import com.toufiq.banglaayat.navigation.SurahRoute
import com.toufiq.banglaayat.ui.screen.QuranScreen
import com.toufiq.banglaayat.ui.settings.SettingsScreen
import com.toufiq.banglaayat.ui.surah.SurahScreen
import com.toufiq.banglaayat.ui.theme.BanglaRandomQuranAyatTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkMode by settingsDataStore.isDarkMode.collectAsStateWithLifecycle(initialValue = false)
            val scope = rememberCoroutineScope()

            BanglaRandomQuranAyatTheme(darkTheme = isDarkMode) {
                MainContent(
                    isDarkMode = isDarkMode,
                    onDarkModeChange = remember(scope, settingsDataStore) {
                        { enabled: Boolean ->
                            scope.launch {
                                settingsDataStore.setDarkMode(enabled)
                            }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    val backStack = rememberNavBackStack(SurahRoute)
    var currentSurahNumber by rememberSaveable { mutableIntStateOf(1) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val currentRoute = backStack.lastOrNull()
            if (currentRoute !is SettingsRoute) {
                AppBottomNavigation(
                    currentRoute = currentRoute,
                    onSurahClick = remember(backStack) {
                        {
                            backStack.clear()
                            backStack.add(SurahRoute)
                        }
                    },
                    onQuranClick = remember(backStack) {
                        {
                            backStack.clear()
                            backStack.add(QuranRoute)
                        }
                    },
                    onSettingsClick = remember(backStack) {
                        { backStack.add(SettingsRoute) }
                    }
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
            NavDisplay(
                backStack = backStack,
                entryProvider = entryProvider {
                    entry<SurahRoute> {
                        SurahScreen(
                            surahNumber = currentSurahNumber,
                            onRandomSurah = remember { { surahNo: Int -> currentSurahNumber = surahNo } }
                        )
                    }
                    entry<QuranRoute> {
                        QuranScreen(
                            onNavigateToSurah = remember(backStack) {
                                { surahNo: Int ->
                                    currentSurahNumber = surahNo
                                    backStack.clear()
                                    backStack.add(SurahRoute)
                                }
                            }
                        )
                    }
                    entry<SettingsRoute> {
                        SettingsScreen(
                            isDarkMode = isDarkMode,
                            onDarkModeChange = onDarkModeChange,
                            onNavigateBack = remember(backStack) {
                                { backStack.removeLastOrNull() }
                            }
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun AppBottomNavigation(
    currentRoute: Any?,
    onSurahClick: () -> Unit,
    onQuranClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute is SurahRoute,
            onClick = onSurahClick,
            icon = { NavIcon(Icons.Default.List, "Surah") },
            label = { Text("Full Surah") }
        )
        NavigationBarItem(
            selected = currentRoute is QuranRoute,
            onClick = onQuranClick,
            icon = { NavIcon(Icons.Default.Home, "Ayat") },
            label = { Text("Random Ayat") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onSettingsClick,
            icon = { NavIcon(Icons.Default.Settings, "Settings") },
            label = { Text("Settings") }
        )
    }
}

@Composable
private fun NavIcon(
    imageVector: ImageVector,
    contentDescription: String
) {
    Icon(imageVector = imageVector, contentDescription = contentDescription)
}