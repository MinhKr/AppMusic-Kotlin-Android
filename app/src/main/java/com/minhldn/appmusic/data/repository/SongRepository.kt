package com.minhldn.appmusic.data.repository

import com.minhldn.appmusic.data.model.Song

interface SongRepository {
    suspend fun fetchSongs():List<Song>
}