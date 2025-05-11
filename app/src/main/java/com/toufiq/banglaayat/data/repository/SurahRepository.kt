package com.toufiq.banglaayat.data.repository

import com.toufiq.banglaayat.data.model.Surah
import com.toufiq.banglaayat.data.remote.QuranApiService
import javax.inject.Inject


interface SurahRepository {
    suspend fun getSurah(surahNumber: Int): Result<Surah>
}

class SurahRepositoryImpl @Inject constructor(
    private val api: QuranApiService
) : SurahRepository {
    override suspend fun getSurah(surahNumber: Int): Result<Surah> = try {
        if (surahNumber in 1..114) {
            Result.success(api.getSurah(surahNumber))
        } else {
            Result.failure(IllegalArgumentException("Surah number must be between 1 and 114"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
} 