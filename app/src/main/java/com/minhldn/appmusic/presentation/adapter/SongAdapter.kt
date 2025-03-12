package com.minhldn.appmusic.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minhldn.appmusic.data.model.Song
import com.minhldn.appmusic.databinding.ItemSongBinding

class SongAdapter(private val songs: List<Song>, private val listener: OnSongClickListener) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
    class SongViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.binding.song = song

        Glide.with(holder.itemView.context).load(song.thumbnail).into(holder.binding.imgThumbnail)

        holder.itemView.setOnClickListener {
            listener.onSongItemClick(song)
        }
    }

    override fun getItemCount() = songs.size
}