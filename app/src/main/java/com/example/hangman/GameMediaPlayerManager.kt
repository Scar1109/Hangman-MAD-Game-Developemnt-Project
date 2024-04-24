package com.example.hangman

import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity

object GameMediaPlayerManager : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null

    fun getMediaPlayer(context: Context): MediaPlayer {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.game_bg)
            mediaPlayer?.isLooping = true
            mediaPlayer?.setVolume(0.7f,0.7f)
        }
        return mediaPlayer!!
    }
}
