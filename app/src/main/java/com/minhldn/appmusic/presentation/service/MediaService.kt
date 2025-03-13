package com.minhldn.appmusic.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.minhldn.appmusic.R
import com.minhldn.appmusic.data.model.Song
import com.minhldn.appmusic.presentation.DetailActivity

class MediaService : Service() {
    private var currentSong: Song? = null
    private val notificationId = 1
    private val channelId = "music_playback_channel"
    private var isPlaying = false
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()

        startForeground(notificationId, getEmptyNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows music playback controls"
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getEmptyNotification(): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_play)
            .setContentTitle("Music Player")
            .setContentText("Ready to play")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun createNotification(): Notification {
        val song = currentSong ?: return getEmptyNotification()

        val openAppIntent = Intent(this, DetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIntent = Intent(this, MediaService::class.java).apply {
            action = if (isPlaying) ACTION_PAUSE else ACTION_PLAY
        }
        val playPausePendingIntent = PendingIntent.getService(
            this,
            0,
            playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentTitle(song.nameSong)
            .setContentText(song.artistName)
            .setContentIntent(openAppPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .addAction(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                if (isPlaying) "Pause" else "Play",
                playPausePendingIntent
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0)
            )

        /* try {
             Glide.with(applicationContext)
                 .asBitmap()
                 .load(song.thumbnail)
                 .into(object : CustomTarget<Bitmap>() {
                     override fun onResourceReady(
                         resource: Bitmap,
                         transition: Transition<in Bitmap>?
                     ) {
                         builder.setLargeIcon(resource)
                         notificationManager.notify(notificationId, builder.build())
                     }

                     override fun onLoadCleared(placeholder: Drawable?) {}
                 })
         } catch (e: Exception) {
             Log.e("MediaService", "Error loading thumbnail", e)
         }*/

        return builder.build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MediaService", "Service started with intent: $intent")
        when (intent?.action) {
            "UPDATE_SONG" -> {
                val song = intent.getSerializableExtra("song") as? Song
                if (song != null) {
                    currentSong = song
                    Log.d("MediaService", "Updating song: ${song.nameSong} - ${song.artistName}")
                    startForeground(notificationId, createNotification())
                }
            }

            ACTION_PLAY -> {
                isPlaying = true
                Log.d("MediaService", "Playing song: ${currentSong?.nameSong}")
                notificationManager.notify(notificationId, createNotification())
            }

            ACTION_PAUSE -> {
                isPlaying = false
                notificationManager.notify(notificationId, createNotification())
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_PLAY = "com.minhldn.appmusic.action.PLAY"
        const val ACTION_PAUSE = "com.minhldn.appmusic.action.PAUSE"
    }
}