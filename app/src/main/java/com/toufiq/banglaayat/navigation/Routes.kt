package com.toufiq.banglaayat.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object SurahRoute : NavKey

@Serializable
data object QuranRoute: NavKey

@Serializable
data object SettingsRoute: NavKey

@Serializable
data class SurahDetailRoute(val surahNumber: Int)
