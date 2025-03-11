package com.minhldn.appmusic.di

import com.minhldn.appmusic.data.network.ApiService
import com.minhldn.appmusic.data.repository.SongRepository
import com.minhldn.appmusic.data.repository.SongRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    val baseUrl = "https://m.vuiz.net/getlink/mp3zing/"

    @Provides
    @Singleton
    fun provideRepository(apiService: ApiService): SongRepository {
        return SongRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

}