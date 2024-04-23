package com.example.hangman

import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity

object MediaPlayerManager : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null

    fun getMediaPlayer(context: Context): MediaPlayer {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.game_background_music)
            mediaPlayer?.isLooping = true
        }
        return mediaPlayer!!
    }
}
