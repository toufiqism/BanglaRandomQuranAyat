package com.toufiq.banglaayat.di

import com.toufiq.banglaayat.data.remote.QuranApiService
import com.toufiq.banglaayat.data.repository.SurahRepository
import com.toufiq.banglaayat.data.repository.SurahRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://quranapi.pages.dev/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideQuranApiService(retrofit: Retrofit): QuranApiService {
        return retrofit.create(QuranApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideSurahRepository(api: QuranApiService): SurahRepository {
        return SurahRepositoryImpl(api)
    }
} 