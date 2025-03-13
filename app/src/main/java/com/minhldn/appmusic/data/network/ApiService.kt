package com.minhldn.appmusic.data.network

import com.minhldn.appmusic.data.model.Song
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @GET("api.php?hotsong")
    suspend fun getSongs(): List<Song>

    @Multipart
    @POST("api.php")
    suspend fun getSongStream(
        @Part("link") link: String
    ): ResponseBody

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