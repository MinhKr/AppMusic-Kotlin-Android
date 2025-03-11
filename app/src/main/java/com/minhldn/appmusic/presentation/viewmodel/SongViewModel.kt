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
}