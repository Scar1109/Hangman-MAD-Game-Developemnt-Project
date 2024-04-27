package com.example.hangman.modules

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.hangman.MediaPlayerManager
import com.example.hangman.R

class ResultScreen : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var status : String = ""
    private var settingDialog: Dialog? = null
    private var isMuted: Boolean = false
    private var isSoundOff : Boolean = false
    private lateinit var gameBgPlayer : MediaPlayer
    private val guessedLetters = mutableListOf<Char>()
    private var word : String = ""
    private var confirmationDialog: Dialog? = null
    private lateinit var btnDefaultMusic : MediaPlayer
    private var helpDialog: Dialog? = null
    private var level : Int = 1
    private var wons : Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result_screen)

        sharedPreferences = getSharedPreferences("HangmanPrefs", Context.MODE_PRIVATE)

        isMuted = sharedPreferences.getBoolean("isMuted", false)
        isSoundOff = sharedPreferences.getBoolean("isSoundOff", false)
        level = sharedPreferences.getInt("level", 1)
        wons = sharedPreferences.getInt("wons", 0)


        status = intent.getStringExtra("status").toString()
        word = intent.getStringExtra("word").toString().toUpperCase()
        val charArr = intent.getCharArrayExtra("guessedLetters")
        if (charArr != null) {
            guessedLetters.addAll(charArr.toList())
        }

        val word_section_won:LinearLayout = findViewById(R.id.word_section_won)
        word?.forEach { char ->
            val itemView = layoutInflater.inflate(R.layout.word_letter_item, null) as LinearLayout
            val textView = itemView.findViewById<TextView>(R.id.letterTextView)
            textView.text = char.toString()
            val textViewId = View.generateViewId()
            textView.id = textViewId
            textView.visibility = View.VISIBLE
            word_section_won.addView(itemView)
        }

        val word_section_lost:LinearLayout = findViewById(R.id.word_section_lost)
        word.forEach { char ->
            val itemView = layoutInflater.inflate(R.layout.word_letter_item, null) as LinearLayout
            val textView = itemView.findViewById<TextView>(R.id.letterTextView)
            textView.text = char.toString()
            val textViewId = View.generateViewId()
            textView.id = textViewId
            if(char !in guessedLetters){
                textView.setTextColor(resources.getColor(R.color.incorrect_key_txt))
            }
            textView.visibility = View.VISIBLE
            word_section_lost.addView(itemView)
        }

        btnDefaultMusic = MediaPlayer.create(this, R.raw.button)
        val mediaPlayerManager = MediaPlayerManager
        gameBgPlayer = mediaPlayerManager.getMediaPlayer(this)

        val won_layout:ConstraintLayout = findViewById(R.id.won_layout)
        val lose_layout:ConstraintLayout = findViewById(R.id.lose_layout)

        if(status.equals("won")){
            wons++
            saveWonsToPrefs(wons)
            won_layout.visibility = View.VISIBLE
            lose_layout.visibility = View.GONE
        }else if(status.equals("lose")){
            won_layout.visibility = View.GONE
            lose_layout.visibility = View.VISIBLE
        }

        val lvl_txt : TextView = findViewById(R.id.lvl_txt)
        val progress_txt : TextView = findViewById(R.id.progress_txt)

        val percentage = wons.toDouble() / 5 * 100
        val formattedPercentage = "%.0f".format(percentage)

        lvl_txt.text = String.format("%02d", level)
        progress_txt.text = "$formattedPercentage%"

        if(wons >= 5){
            level++
            saveLevelToPrefs(level)
            wons = 0
            saveWonsToPrefs(wons)
        }

        val next_btn : RelativeLayout = findViewById(R.id.next_btn)

        next_btn.setOnClickListener{
            if(!isSoundOff){
                btnDefaultMusic.start()
            }

            val intent = Intent(this,GameScreen::class.java)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            startActivity(intent)
        }

        val restart_btn : RelativeLayout = findViewById(R.id.restart_btn)

        restart_btn.setOnClickListener{

            if(!isSoundOff){
                btnDefaultMusic.start()
            }

            val intent = Intent(this,GameScreen::class.java)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            startActivity(intent)
        }

        val setting_btn : ImageView = findViewById(R.id.setting_btn)

        setting_btn.setOnClickListener {

            if(!isSoundOff){
                btnDefaultMusic.start()
            }

            showCustomDialog()
        }

        val home_btn_won : ImageView = findViewById(R.id.home_btn_won)

        home_btn_won.setOnClickListener {
            if(!isSoundOff){
                btnDefaultMusic.start()
            }

            showConformation()
        }

        val home_btn : ImageView = findViewById(R.id.home_btn)

        home_btn.setOnClickListener {

            if(!isSoundOff){
                btnDefaultMusic.start()
            }

            showConformation()
        }

        val help_btn_won : ImageView = findViewById(R.id.help_btn_won)

        help_btn_won.setOnClickListener {
            if(!isSoundOff){
                btnDefaultMusic.start()
            }
            showHelpDialog()
        }

        val help_btn : ImageView = findViewById(R.id.help_btn)

        help_btn.setOnClickListener {
            if(!isSoundOff){
                btnDefaultMusic.start()
            }
            showHelpDialog()
        }
    }

    private fun saveLevelToPrefs(lvl: Int) {
        sharedPreferences.edit().putInt("level", lvl).apply()
    }

    private fun saveWonsToPrefs(lvl: Int) {
        sharedPreferences.edit().putInt("wons", lvl).apply()
    }

    private fun showCustomDialog() {
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        settingDialog = dialog
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

    private fun showConformation() {
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        confirmationDialog = dialog
        dialog.setContentView(R.layout.conformation_popup_activity)

        val close_btn : ImageView = dialog.findViewById(R.id.close_btn)

        close_btn.setOnClickListener{
            if(!isSoundOff){
                btnDefaultMusic.start()
            }
            dialog.hide()
        }

        val yes_btn : RelativeLayout = dialog.findViewById(R.id.yes_btn)

        yes_btn.setOnClickListener{
            if(!isSoundOff){
                btnDefaultMusic.start()
            }
            val intent = Intent(this, HomePage::class.java)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            startActivity(intent)
            finish()
        }

        val no_btn : RelativeLayout = dialog.findViewById(R.id.no_btn)

        no_btn.setOnClickListener{
            if(!isSoundOff){
                btnDefaultMusic.start()
            }
            dialog.hide()
        }

        dialog.show()
    }

    private fun updateMuteSwitch(sound_switch : Switch) {
        if(isMuted){
            sound_switch.isChecked = false
        }else{
            sound_switch.isChecked = true
        }
    }

    private fun saveIsSoundOffToPrefs(isSOff: Boolean) {
        sharedPreferences.edit().putBoolean("isSoundOff", isSOff).apply()
    }

    private fun updateSoundOffSwitch(sound_switch : Switch) {
        if(isSoundOff){
            sound_switch.isChecked = false
        }else{
            sound_switch.isChecked = true
        }
    }

    private fun toggleMuteState() {
        if (isMuted) {
            gameBgPlayer.setVolume(0.7f, 0.7f)
        } else {
            gameBgPlayer.setVolume(0f, 0f)
        }
        isMuted = !isMuted
        saveIsMutedToPrefs(isMuted)
    }

    private fun saveIsMutedToPrefs(isMute: Boolean) {
        sharedPreferences.edit().putBoolean("isMuted", isMute).apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        confirmationDialog?.dismiss()
        settingDialog?.dismiss()
        helpDialog?.dismiss()
    }

    private fun showHelpDialog() {
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        helpDialog = dialog
        dialog.setContentView(R.layout.help_popup_activity)

        val close_btn : ImageView = dialog.findViewById(R.id.close_btn)

        close_btn.setOnClickListener{
            if(!isSoundOff){
                btnDefaultMusic.start()
            }
            dialog.hide()
        }

        dialog.show()
    }
}