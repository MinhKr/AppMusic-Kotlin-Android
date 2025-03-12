package com.minhldn.appmusic.presentation.adapter

import com.minhldn.appmusic.data.model.Song

interface OnSongClickListener {
    fun onSongItemClick(song:Song)
}