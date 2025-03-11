package com.minhldn.appmusic.data.repository

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
}