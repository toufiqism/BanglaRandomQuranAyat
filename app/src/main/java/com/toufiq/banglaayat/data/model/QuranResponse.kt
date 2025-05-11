package com.toufiq.banglaayat.data.model

data class QuranResponse(
    val surahName: String,
    val surahNameArabic: String,
    val surahNameArabicLong: String,
    val surahNameTranslation: String,
    val revelationPlace: String,
    val totalAyah: Int,
    val surahNo: Int,
    val ayahNo: Int,
    val audio: Map<String, AudioReciter>,
    val english: String,
    val arabic1: String,
    val arabic2: String,
    val bengali: String,
    val urdu: String
)

data class AudioReciter(
    val reciter: String,
    val url: String,
    val originalUrl: String
) 