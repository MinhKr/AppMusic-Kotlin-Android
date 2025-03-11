package com.minhldn.appmusic.data.network

import com.minhldn.appmusic.data.model.Song
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("api.php?hotsong")
    suspend fun getSongs(): List<Song>

    companion object {
        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl("https://m.vuiz.net/getlink/mp3zing/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}