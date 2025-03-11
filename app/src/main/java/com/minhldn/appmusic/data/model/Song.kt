package com.minhldn.appmusic.data.model

import android.provider.MediaStore.Video.Thumbnails
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Song(
    @SerializedName("title") val nameSong: String,
    @SerializedName("artists_names") val artistName: String,
    @SerializedName("thumbnail") val thumbnail: String
):Serializable
