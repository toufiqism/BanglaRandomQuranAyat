package com.toufiq.banglaayat.data.repository

import com.toufiq.banglaayat.data.model.Surah
import retrofit2.http.GET
import retrofit2.http.Path

interface SurahApi {
    @GET("api/{surahNumber}.json")
    suspend fun getSurah(@Path("surahNumber") surahNumber: Int): Surah
}

interface SurahRepository {
    suspend fun getSurah(surahNumber: Int): Result<Surah>
}

class SurahRepositoryImpl(
    private val api: SurahApi
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