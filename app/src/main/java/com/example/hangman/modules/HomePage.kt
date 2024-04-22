package com.example.hangman.modules

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import com.example.hangman.R

class HomePage : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playBtnMusic: MediaPlayer
    private lateinit var btnDefaultMusic: MediaPlayer
    private var isMuted: Boolean = false
    private lateinit var muteBtn: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        mediaPlayer = MediaPlayer.create(this, R.raw.game_background_music)
        mediaPlayer.isLooping = true
        playBtnMusic = MediaPlayer.create(this, R.raw.level_won)
        btnDefaultMusic = MediaPlayer.create(this, R.raw.button)
        mediaPlayer.start()

        val dufuculty_btn : RelativeLayout = findViewById(R.id.dufuculty_btn)
        val play_btn : ImageView = findViewById(R.id.play_btn)

        dufuculty_btn.setOnClickListener {
            btnDefaultMusic.start()
            val intent = Intent(this, DificultySelectionScreen::class.java)
            startActivity(intent)
        }

        play_btn.setOnClickListener{
            playBtnMusic.start()
            mediaPlayer.release()
            val intent = Intent(this, GameScreen::class.java)
            startActivity(intent)
        }

        muteBtn = findViewById(R.id.mute_btn)

        updateMuteButtonImage()

        muteBtn.setOnClickListener {
            btnDefaultMusic.start()
            toggleMuteState()
        }

        val settings_btn : ImageView = findViewById(R.id.settings_btn)

        settings_btn.setOnClickListener{
            btnDefaultMusic.start()
        }

        val history_btn : ImageView = findViewById(R.id.history_btn)

        history_btn.setOnClickListener{
            btnDefaultMusic.start()
        }

    }

    private fun toggleMuteState() {
        if (isMuted) {
            mediaPlayer.setVolume(0.7f, 0.7f)
        } else {
            mediaPlayer.setVolume(0f, 0f)
        }
        isMuted = !isMuted
        updateMuteButtonImage()
    }

    private fun updateMuteButtonImage() {
        if (isMuted) {
            muteBtn.setImageResource(R.drawable.mute_btn)
        } else {
            muteBtn.setImageResource(R.drawable.speacker_btn)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        btnDefaultMusic.release()
        playBtnMusic.release()
    }
}