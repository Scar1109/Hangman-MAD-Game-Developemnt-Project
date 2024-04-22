package com.example.hangman.modules

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import com.example.hangman.R

class HomePage : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playBtnMusic: MediaPlayer
    private lateinit var btnDefaultMusic: MediaPlayer
    private var isMuted: Boolean = false
    private var isSoundOff : Boolean = false
    private lateinit var muteBtn: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        sharedPreferences = getSharedPreferences("HangmanPrefs", Context.MODE_PRIVATE)

        checkAndSetDefaultDifficulty()

        isMuted = sharedPreferences.getBoolean("isMuted", false)
        isSoundOff = sharedPreferences.getBoolean("isSoundOff", false)

        mediaPlayer = MediaPlayer.create(this, R.raw.game_background_music)
        mediaPlayer.isLooping = true
        playBtnMusic = MediaPlayer.create(this, R.raw.level_won)
        btnDefaultMusic = MediaPlayer.create(this, R.raw.button)
        mediaPlayer.start()
        if(isMuted){
            mediaPlayer.setVolume(0f,0f)
        }

        val dufuculty_btn : RelativeLayout = findViewById(R.id.dufuculty_btn)
        val play_btn : ImageView = findViewById(R.id.play_btn)

        dufuculty_btn.setOnClickListener {
            if(!isSoundOff){
                btnDefaultMusic.start()
            }
            val intent = Intent(this, DificultySelectionScreen::class.java)
            startActivity(intent)
        }

        play_btn.setOnClickListener{
            if(!isSoundOff){
                playBtnMusic.start()
            }
            mediaPlayer.release()
            val intent = Intent(this, GameScreen::class.java)
            startActivity(intent)
        }

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

        val history_btn : ImageView = findViewById(R.id.history_btn)

        history_btn.setOnClickListener{
            if(!isSoundOff) {
                btnDefaultMusic.start()
            }
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

    private fun updateMuteButtonImage() {
        if (isMuted) {
            muteBtn.setImageResource(R.drawable.mute_btn)
        } else {
            muteBtn.setImageResource(R.drawable.speacker_btn)
        }
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

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        btnDefaultMusic.release()
        playBtnMusic.release()
    }

    private fun showCustomDialog() {
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.setContentView(R.layout.settings_popup_activity)

        val close_btn : ImageView = dialog.findViewById(R.id.close_btn)

        close_btn.setOnClickListener{
            if(isSoundOff){
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

    private fun saveIsMutedToPrefs(isMute: Boolean) {
        sharedPreferences.edit().putBoolean("isMuted", isMute).apply()
    }

    private fun saveIsSoundOffToPrefs(isSOff: Boolean) {
        sharedPreferences.edit().putBoolean("isSoundOff", isSOff).apply()
    }

    private fun checkAndSetDefaultDifficulty() {
        if (!sharedPreferences.contains("isMuted")) {
            saveIsMutedToPrefs(false)
        }

        if (!sharedPreferences.contains("isSoundOff")){
            saveIsSoundOffToPrefs(false)
        }
    }
}