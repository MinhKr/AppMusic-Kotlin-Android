package com.minhldn.appmusic.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhldn.appmusic.data.model.Song
import com.minhldn.appmusic.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val songRepository: SongRepository
) : ViewModel() {
    private val _songs = MutableLiveData<List<Song>>(listOf())
    val songs: LiveData<List<Song>> get() = _songs

    private val _currentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song> = _currentSong

    private val _streamUrl = MutableLiveData<String?>()
    val streamUrl: LiveData<String?> = _streamUrl

    fun fetchSongs() {
        viewModelScope.launch {
            try {
                val result = songRepository.fetchSongs()
                _songs.postValue(result)
            } catch (e: Exception) {
                _songs.value = emptyList()
            }
        }
    }

    fun setCurrentSong(song: Song) {
        _currentSong.value = song
        loadStreamUrl(song.link)
    }

    private fun loadStreamUrl(songUrl: String) {
        viewModelScope.launch {
            try {
                val url = songRepository.getSongStream(songUrl)
                _streamUrl.value = url
            } catch (e: Exception) {
                _streamUrl.value = null
            }
        }
    }
}