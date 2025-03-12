package com.minhldn.appmusic.data.model

import android.provider.MediaStore.Video.Thumbnails
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Song(
    @PrimaryKey
    @SerializedName("id") val id: String,
    @SerializedName("title") val nameSong: String,
    @SerializedName("artists_names") val artistName: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("link") val url: String
):Serializable
