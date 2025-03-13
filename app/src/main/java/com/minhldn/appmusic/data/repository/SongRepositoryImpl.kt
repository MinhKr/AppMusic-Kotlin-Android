package com.minhldn.appmusic.data.repository

import android.util.Log
import com.minhldn.appmusic.data.model.Song
import com.minhldn.appmusic.data.network.ApiService
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SongRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SongRepository {
    override suspend fun fetchSongs(): List<Song> {
        return apiService.getSongs()
    }

    override suspend fun getSongStream(songUrl: String): String {
        try {
            val fullUrl = "https://zingmp3.vn${songUrl}"
            Log.d("SongRepository", "Requesting stream for URL: $fullUrl")
            
            val response = apiService.getSongStream(fullUrl)
            val html = response.string()
            Log.d("SongRepository", "Received HTML response: $html")
            
            val streamUrl = extractStreamUrl(html)
            Log.d("SongRepository", "Extracted stream URL: $streamUrl")
            
            return streamUrl
        } catch (e: Exception) {
            Log.e("SongRepository", "Error getting stream URL", e)
            throw e
        }
    }

    private fun extractStreamUrl(html: String): String {
        val regex = """data-src=\\"([^"]+)\\"""".toRegex()
        return regex.find(html)?.groupValues?.get(1)
            ?: throw Exception("Không tìm thấy URL stream")
    }
}