package com.minhldn.appmusic.data.network

import com.minhldn.appmusic.data.model.Song
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("api.php?hotsong")
    suspend fun getSongs(): List<Song>

    companion object {

        private const val BASE_URL = "https://m.vuiz.net/getlink/mp3zing/"
        fun create(): ApiService {
            return retrofit.create(ApiService::class.java)
        }

        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}