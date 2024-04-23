package com.example.hangman.modules

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import com.example.hangman.MediaPlayerManager
import com.example.hangman.R

class DificultySelectionScreen : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var playBtnMusic: MediaPlayer
    private lateinit var mediaPlayer :MediaPlayer
    private lateinit var btnDefaultMusic: MediaPlayer
    private var isMuted: Boolean = false
    private var isSoundOff : Boolean = false
    private lateinit var muteBtn: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dificulty_selection_screen)

        sharedPreferences = getSharedPreferences("HangmanPrefs", Context.MODE_PRIVATE)
        checkAndSetDefaultDifficulty()

        val mediaPlayerManager = MediaPlayerManager
        mediaPlayer = mediaPlayerManager.getMediaPlayer(this)

        isMuted = sharedPreferences.getBoolean("isMuted", false)
        isSoundOff = sharedPreferences.getBoolean("isSoundOff", false)

        playBtnMusic = MediaPlayer.create(this, R.raw.level_won)
        btnDefaultMusic = MediaPlayer.create(this, R.raw.button)

        muteBtn = findViewById(R.id.mute_btn)

        updateMuteButtonImage()

        muteBtn.setOnClickListener {
            if(!isSoundOff) {
                btnDefaultMusic.start()
            }
            toggleMuteState()
        }

        val settings_btn : ImageView = findViewById(R.id.settings_btn)

        settings_btn.setOnClickListener{
            if(!isSoundOff) {
                btnDefaultMusic.start()
            }
            showCustomDialog()
        }

        val back_btn: ImageView =  findViewById<ImageView>(R.id.back_btn)

            back_btn.setOnClickListener {
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
                finish()
            }

        val easy_btn = findViewById<RelativeLayout>(R.id.easy_btn)

        easy_btn.setOnClickListener{
            if(!isSoundOff) {
                btnDefaultMusic.start()
            }
            saveDifficultyPrefs("easy")
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        val normal_btn = findViewById<RelativeLayout>(R.id.normal_btn)

        normal_btn.setOnClickListener{
            if(!isSoundOff) {
                btnDefaultMusic.start()
            }
            saveDifficultyPrefs("normal")
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        val hard_btn = findViewById<RelativeLayout>(R.id.hard_btn)

        hard_btn.setOnClickListener{
            if(!isSoundOff) {
                btnDefaultMusic.start()
            }
            saveDifficultyPrefs("hard")
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

    }

    private fun saveDifficultyPrefs(difficulty: String) {
        sharedPreferences.edit().putString("difficulty", difficulty).apply()
    }

    private fun checkAndSetDefaultDifficulty() {
        if (!sharedPreferences.contains("isMuted")) {
            saveIsMutedToPrefs(false)
        }

        if (!sharedPreferences.contains("isSoundOff")){
            saveIsSoundOffToPrefs(false)
        }
    }

    private fun saveIsMutedToPrefs(isMute: Boolean) {
        sharedPreferences.edit().putBoolean("isMuted", isMute).apply()
    }

    private fun saveIsSoundOffToPrefs(isSOff: Boolean) {
        sharedPreferences.edit().putBoolean("isSoundOff", isSOff).apply()
    }

    private fun updateMuteButtonImage() {
        if (isMuted) {
            muteBtn.setImageResource(R.drawable.mute_btn)
        } else {
            muteBtn.setImageResource(R.drawable.speacker_btn)
        }
    }

    private fun toggleMuteState() {
        if (isMuted) {
            mediaPlayer.setVolume(0.7f, 0.7f)
        } else {
            mediaPlayer.setVolume(0f, 0f)
        }
        isMuted = !isMuted
        saveIsMutedToPrefs(isMuted)
        updateMuteButtonImage()
    }

    private fun showCustomDialog() {
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.setContentView(R.layout.settings_popup_activity)

        val close_btn : ImageView = dialog.findViewById(R.id.close_btn)

        close_btn.setOnClickListener{
            if(!isSoundOff){
                btnDefaultMusic.start()
            }
            dialog.hide()
        }

        val sound_switch : Switch = dialog.findViewById(R.id.sound_switch)

        updateMuteSwitch(sound_switch)

        sound_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                isMuted = true
            }else{
                isMuted = false
            }
            toggleMuteState()
            updateMuteSwitch(sound_switch)
        }

        val music_switch : Switch = dialog.findViewById(R.id.music_switch)
        updateSoundOffSwitch(music_switch)

        music_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                isSoundOff = false
            }
            else{
                isSoundOff = true
            }
            saveIsSoundOffToPrefs(isSoundOff)
            updateSoundOffSwitch(music_switch)
        }


        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        btnDefaultMusic.release()
        playBtnMusic.release()
    }

    private fun updateMuteSwitch(sound_switch : Switch) {
        if(isMuted){
            sound_switch.isChecked = false
        }else{
            sound_switch.isChecked = true
        }
    }

    private fun updateSoundOffSwitch(sound_switch : Switch) {
        if(isSoundOff){
            sound_switch.isChecked = false
        }else{
            sound_switch.isChecked = true
        }
    }
}