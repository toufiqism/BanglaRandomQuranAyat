package com.toufiq.banglaayat.data.remote

import com.toufiq.banglaayat.data.model.QuranResponse
import com.toufiq.banglaayat.data.model.Surah
import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApiService {
    @GET("api/{surah}/{ayah}.json")
    suspend fun getQuranAyah(
        @Path("surah") surah: Int,
        @Path("ayah") ayah: Int
    ): QuranResponse

    @GET("api/{surahNumber}.json")
    suspend fun getSurah(@Path("surahNumber") surahNumber: Int): Surah


} 