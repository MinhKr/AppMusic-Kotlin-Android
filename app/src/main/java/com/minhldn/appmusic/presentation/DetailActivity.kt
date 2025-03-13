package com.minhldn.appmusic.presentation

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.minhldn.appmusic.R
import com.minhldn.appmusic.data.model.Song
import com.minhldn.appmusic.databinding.ActivityDetailBinding
import com.minhldn.appmusic.presentation.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: SongViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false
    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val song = intent.getSerializableExtra("song") as? Song
        song?.let {
            setupUI(it)
            viewModel.setCurrentSong(it)
        }

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.streamUrl.observe(this) { url ->
            url?.let { setupMediaPlayer(it) }
        }
    }

    private fun setupUI(song: Song) {
        binding.detailPlaying.apply {
            txtSongTitle.text = song.nameSong
            txtArtist.text = song.artistName
            txtTimeSong.text = convertSecondsToMinutes(song.duration)
            seekSpeed.max = song.duration * 1000
        }
    }

    private fun setupMediaPlayer(url: String) {
        try {

            val decodedUrl = URLDecoder.decode(url.replace("\\", ""), "UTF-8")
            mediaPlayer?.release()
            mediaPlayer = null

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                setDataSource(decodedUrl)

                binding.detailPlaying.ivPlayPause.isEnabled = false

                setOnPreparedListener { mp ->
                    isPrepared = true
                    binding.detailPlaying.ivPlayPause.isEnabled = true
                    playMusic()
                }

                setOnCompletionListener {
                    isPrepared = false
                    this@DetailActivity.isPlaying = false
                    binding.detailPlaying.ivPlayPause.setImageResource(R.drawable.ic_play)
                }

                setOnErrorListener { mp, what, extra ->
                    Log.e("MediaPlayer", "Error: $what, extra: $extra")
                    isPrepared = false
                    this@DetailActivity.isPlaying = false
                    binding.detailPlaying.ivPlayPause.isEnabled = false
                    Toast.makeText(
                        this@DetailActivity,
                        "Không thể phát bài hát này (Error: $what)",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("MediaPlayer", "Error setting up MediaPlayer", e)
            Toast.makeText(
                this,
                "Lỗi khởi tạo player: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            ivExit.setOnClickListener {
                finish()
            }

            detailPlaying.ivPlayPause.setOnClickListener {
                if (isPlaying) {
                    pauseMusic()
                } else {
                    playMusic()
                }
            }

            detailPlaying.seekSpeed.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mediaPlayer?.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun playMusic() {
        if (isPrepared && !isPlaying) {
            try {
                mediaPlayer?.start()
                isPlaying = true
                binding.detailPlaying.ivPlayPause.setImageResource(R.drawable.ic_pause)
                startUpdateSeekbar()
            } catch (e: Exception) {
                Log.e("MediaPlayer", "Error playing", e)
                Toast.makeText(this, "Lỗi khi phát nhạc: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pauseMusic() {
        if (isPrepared && isPlaying) {
            try {
                mediaPlayer?.pause()
                isPlaying = false
                binding.detailPlaying.ivPlayPause.setImageResource(R.drawable.ic_play)
            } catch (e: Exception) {
                Log.e("MediaPlayer", "Error pausing", e)
            }
        }
    }

    private fun startUpdateSeekbar() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                mediaPlayer?.let { player ->
                    binding.detailPlaying.seekSpeed.progress = player.currentPosition
                    binding.detailPlaying.txtCurrentTime.text =
                        convertSecondsToMinutes(player.currentPosition / 1000)
                    handler.postDelayed(this, 1000)
                }
            }
        }, 0)
    }

    fun convertSecondsToMinutes(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
            isPrepared = false
            isPlaying = false
        } catch (e: Exception) {
            Log.e("MediaPlayer", "Error releasing MediaPlayer", e)
        }
    }
}