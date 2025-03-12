package com.minhldn.appmusic.presentation

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: SongViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null
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
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                binding.detailPlaying.ivPlayPause.isEnabled = true
                startUpdateSeekbar()
            }
            setOnCompletionListener {
                this@DetailActivity.isPlaying = false
                binding.detailPlaying.ivPlayPause.setImageResource(R.drawable.ic_play)
                handler.removeCallbacksAndMessages(null)
            }
            setOnErrorListener { _, _, _ ->
                Toast.makeText(this@DetailActivity, "Không thể phát bài hát này", Toast.LENGTH_SHORT).show()
                true
            }
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

            detailPlaying.seekSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
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
        mediaPlayer?.start()
        isPlaying = true
        binding.detailPlaying.ivPlayPause.setImageResource(R.drawable.ic_pause)
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        isPlaying = false
        binding.detailPlaying.ivPlayPause.setImageResource(R.drawable.ic_play)
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
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
    }
}