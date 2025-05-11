package com.toufiq.banglaayat.data.repository

import com.toufiq.banglaayat.data.model.QuranResponse
import com.toufiq.banglaayat.data.remote.QuranApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranRepository @Inject constructor(
    private val apiService: QuranApiService
) {
    suspend fun getQuranAyah(surah: Int, ayah: Int): Result<QuranResponse> {
        return try {
            val response = apiService.getQuranAyah(surah, ayah)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 