package com.toufiq.banglaayat.data.model

data class Surah(
    val surahName: String,
    val surahNameArabic: String,
    val surahNameArabicLong: String,
    val surahNameTranslation: String,
    val revelationPlace: String,
    val totalAyah: Int,
    val surahNo: Int,
    val audio: Map<String, AudioReciter>,
    val english: List<String>,
    val arabic1: List<String>,
    val arabic2: List<String>,
    val bengali: List<String>,
    val urdu: List<String>
)
