package com.example.hangman.modules

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.hangman.GameMediaPlayerManager
import com.example.hangman.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GameScreen : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var word: String? = null
    private var category: String? = null
    private var startTime: Long = 0
    private lateinit var gameBgPlayer : MediaPlayer
    private lateinit var correctBtnPlayer : MediaPlayer
    private lateinit var incorrectBtnPlayer : MediaPlayer
    private var difficulty : String = "easy"
    private val textViewIds = mutableListOf<Int>()
    private var liveCount : Int = 6
    private var isMuted: Boolean = false
    private var isSoundOff : Boolean = false
    private lateinit var btnDefaultMusic : MediaPlayer
    private val guessedLetters = mutableListOf<Char>()
    private var pauseDialog: Dialog? = null
    private var confirmationDialog: Dialog? = null
    private var settingDialog: Dialog? = null
    private var helpDialog: Dialog? = null
    private var hintCount : Int = 0
    private var level : Int = 1
    private var correctCount : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_screen)

        sharedPreferences = getSharedPreferences("HangmanPrefs", Context.MODE_PRIVATE)

        isMuted = sharedPreferences.getBoolean("isMuted", false)
        isSoundOff = sharedPreferences.getBoolean("isSoundOff", false)
        level = sharedPreferences.getInt("level", 1)

        checkAndSetDefaultDifficulty()
        difficulty = sharedPreferences.getString("difficulty", "easy").toString()

        if(difficulty.equals("easy") == true){
            hintCount = 1
        }else if (difficulty.equals("normal") == true){
            hintCount = 2
        }else if (difficulty.equals("hard") == true){
            hintCount = 3
        }
        chnageMan()

        val hint_Count : TextView = findViewById(R.id.hint_count)
        hint_Count.text = "x$hintCount"

        gameBgPlayer = GameMediaPlayerManager.getMediaPlayer(this)
        btnDefaultMusic = MediaPlayer.create(this, R.raw.button)
        correctBtnPlayer = MediaPlayer.create(this, R.raw.alphabet_tap)
        incorrectBtnPlayer = MediaPlayer.create(this,R.raw.game_lost)

        val content_layout : ConstraintLayout = findViewById(R.id.content_layout)
        val loading_layout : ConstraintLayout = findViewById(R.id.loading_layout)
        content_layout.visibility = View.GONE
        loading_layout.visibility = View.VISIBLE

        startTime = System.currentTimeMillis()

        fetchRandomWord(difficulty) { word, category ->

            val uppercaseWord = word?.toUpperCase()
            val uppercaseCategory = category?.toUpperCase()

            val category_txt : TextView = findViewById(R.id.category_txt)

            category_txt.text = uppercaseCategory

            val twoSeconds = 2000L
            val elapsedTime = System.currentTimeMillis() - startTime
            val delay = if (elapsedTime < twoSeconds) twoSeconds - elapsedTime else 0L
            Handler().postDelayed({
                // Hide the progress bar
                val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
                content_layout.startAnimation(fadeInAnimation)
                content_layout.visibility = View.VISIBLE
                loading_layout.visibility = View.GONE
                gameBgPlayer.start()
                if(isMuted){
                    gameBgPlayer.setVolume(0f,0f)
                }

                val word_section : LinearLayout = findViewById(R.id.word_section)

                Log.d("GameScreen", "Fetched word "+ word)

                uppercaseWord?.forEach { char ->
                    val itemView = layoutInflater.inflate(R.layout.word_letter_item, null) as LinearLayout
                    val textView = itemView.findViewById<TextView>(R.id.letterTextView)
                    textView.text = char.toString()
                    val textViewId = View.generateViewId() // Generate unique ID for each TextView
                    textView.id = textViewId
                    textView.visibility = View.INVISIBLE
                    textViewIds.add(textViewId)
                    word_section.addView(itemView)

                }
            }, delay)
        }

        for (char in 'A'..'Z') {
            val keyLayoutId = resources.getIdentifier("key_$char", "id", packageName)
            val keyLayout = findViewById<RelativeLayout>(keyLayoutId)

            keyLayout?.setOnClickListener {
                onKeyClicked(keyLayout)
            }
        }

        val live_count : TextView = findViewById(R.id.live_count)
        live_count.text = "06/0$liveCount"

        val help_btn : ImageView = findViewById(R.id.help_btn)

        help_btn.setOnClickListener {
            if(!isSoundOff){
                btnDefaultMusic.start()
            }
            showHelpDialog()
        }

        val pause_btn : ImageView = findViewById(R.id.pause_btn)

        pause_btn.setOnClickListener {
            if(!isSoundOff){
                btnDefaultMusic.start()
            }
            showPauseDialog()
        }

        val hint:ImageView = findViewById(R.id.hint_btn)

        hint.setOnClickListener {
            if(!isSoundOff){
                btnDefaultMusic.start()
            }
            if(hintCount == 0){
                Toast.makeText(this, "Your hint count exceeded!", Toast.LENGTH_SHORT).show()
            }else{
                if (word?.isNotEmpty() == true) {

                    val randomIndex = (word!!.indices).random()
                    var randomChar = word!![randomIndex]
                    while (randomChar in guessedLetters) {
                        randomChar = word!![word!!.indices.random()]
                    }
                    handleInput(randomChar.toString().toUpperCase())
                }
                hintCount--
                hint_Count.text = "x$hintCount"
            }
        }

    }

    private fun chnageMan(){
        val default_hang : ImageView = findViewById(R.id.default_hang)
        val hanged_man_1 : ImageView = findViewById(R.id.hanged_man_1)
        val hanged_man_2 : ImageView = findViewById(R.id.hanged_man_2)
        val hanged_man_3 : ImageView = findViewById(R.id.hanged_man_3)
        val hanged_man_4 : ImageView = findViewById(R.id.hanged_man_4)
        val hanged_man_5 : ImageView = findViewById(R.id.hanged_man_5)
        val hanged_man_6 : ImageView = findViewById(R.id.hanged_man_6)

        when (liveCount) {
            6 -> {
                default_hang.visibility = View.VISIBLE
            }
            5 -> {
                default_hang.visibility = View.GONE
                hanged_man_1.visibility = View.VISIBLE
            }
            4 -> {
                hanged_man_1.visibility = View.GONE
                hanged_man_2.visibility = View.VISIBLE
            }
            3 -> {
                hanged_man_2.visibility = View.GONE
                hanged_man_3.visibility = View.VISIBLE
            }
            2 -> {
                hanged_man_3.visibility = View.GONE
                hanged_man_4.visibility = View.VISIBLE
            }
            1 -> {
                hanged_man_4.visibility = View.GONE
                hanged_man_5.visibility = View.VISIBLE
            }
            0 -> {
                hanged_man_5.visibility = View.GONE
                hanged_man_6.visibility = View.VISIBLE
            }
        }


    }

    fun onKeyClicked(view: View) {
        val input = getKeyInput(view.id)
        handleInput(input)
    }

    private fun getKeyInput(viewId: Int): String {
        return when (viewId) {
            R.id.key_A -> "A"
            R.id.key_B -> "B"
            R.id.key_C -> "C"
            R.id.key_D -> "D"
            R.id.key_E -> "E"
            R.id.key_F -> "F"
            R.id.key_G -> "G"
            R.id.key_H -> "H"
            R.id.key_I -> "I"
            R.id.key_J -> "J"
            R.id.key_K -> "K"
            R.id.key_L -> "L"
            R.id.key_M -> "M"
            R.id.key_N -> "N"
            R.id.key_O -> "O"
            R.id.key_P -> "P"
            R.id.key_Q -> "Q"
            R.id.key_R -> "R"
            R.id.key_S -> "S"
            R.id.key_T -> "T"
            R.id.key_U -> "U"
            R.id.key_V -> "V"
            R.id.key_W -> "W"
            R.id.key_X -> "X"
            R.id.key_Y -> "Y"
            R.id.key_Z -> "Z"
            else -> ""
        }
    }

    private fun handleInput(value : String) {
        val intent1 = Intent(this, ResultScreen::class.java)

        val guessedChar = value.firstOrNull()?.toUpperCase()
        val uppercaseWord = word?.toUpperCase()
        if (guessedChar != null && guessedChar in 'A'..'Z') {
            if (guessedChar !in guessedLetters) {
                guessedLetters.add(guessedChar)
                val pressedTxtId = resources.getIdentifier("text_$value", "id", packageName)
                val pressedText: TextView = findViewById(pressedTxtId)

                val pressedRelativeLayerId = resources.getIdentifier("key_$value" ,"id", packageName)
                val pressedRelativeLayer : RelativeLayout = findViewById(pressedRelativeLayerId)

                pressedRelativeLayer.setBackgroundResource(R.drawable.keybaord_selected)

                if (uppercaseWord?.contains(guessedChar) != true) {
                    if(!isSoundOff){
                        incorrectBtnPlayer.start()
                    }
                    liveCount--
                    chnageMan()
                    pressedText.setTextColor(resources.getColor(R.color.incorrect_key_txt))
                    pressedText.setShadowLayer(2.16f, 0f, 1.08f, resources.getColor(R.color.correct_key_shadow))
                    if(liveCount == 0){
                        intent1.putExtra("status", "lost")
                        intent1.putExtra("word", word)
                        intent1.putExtra("guessedLetters", guessedLetters.toCharArray())
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        startActivity(intent1)
                        finish()
                    }
                } else {
                    if(!isSoundOff){
                        correctBtnPlayer.start()
                    }
                    correctCount++
                    revealLetters(guessedChar)
                    pressedText.setTextColor(resources.getColor(R.color.correct_key_txt))
                    pressedText.setShadowLayer(2.16f, 0f, 1.08f, resources.getColor(R.color.incorrect_key_txt))
                    if (correctCount == word!!.length){
                        intent1.putExtra("status", "won")
                        intent1.putExtra("word", word)
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        startActivity(intent1)
                        finish()
                    }
                }
            }
        }
        updateUI()
    }

    private fun revealLetters(guessedChar: Char) {
        val uppercaseWord = word?.toUpperCase()
        for (i in uppercaseWord?.indices!!) {
            if (uppercaseWord!![i] == guessedChar) {
                val textViewId = textViewIds[i]
                val textView = findViewById<TextView>(textViewId)
                textView.visibility = View.VISIBLE
            }
        }
    }

    private fun updateUI() {
        // Update the remaining lives count
        val liveCountTextView: TextView = findViewById(R.id.live_count)
        liveCountTextView.text = String.format("06/0%d", liveCount)
    }

    private fun fetchRandomWord(difficulty: String, callback: (word: String?, category: String?) -> Unit) {
        Thread {
            try {
                val url = URL("https://random-word-api-tzz8.onrender.com/api/getRandomWord?difficulty=$difficulty")
                val connection = url.openConnection() as HttpURLConnection
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()
                    val jsonResponse = JSONObject(response.toString())
                    word = jsonResponse.getString("word")
                    category = jsonResponse.getString("category")
                    runOnUiThread {
                        callback(word, category)
                    }
                } else {
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Dismiss any open dialogs to prevent WindowLeaked exception
        pauseDialog?.dismiss()
        confirmationDialog?.dismiss()
        settingDialog?.dismiss()
        helpDialog?.dismiss()
    }

    private fun saveDifficultyToPrefs(difficulty: String) {
        sharedPreferences.edit().putString("difficulty", difficulty).apply()
    }

    private fun checkAndSetDefaultDifficulty() {
        if (!sharedPreferences.contains("difficulty")) {
            saveDifficultyToPrefs("easy")
        }
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

    private fun saveIsSoundOffToPrefs(isSOff: Boolean) {
        sharedPreferences.edit().putBoolean("isSoundOff", isSOff).apply()
    }
    private fun showPauseDialog() {
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        pauseDialog = dialog
        dialog.setContentView(R.layout.pause_popup_activity)

        val close_btn : ImageView = dialog.findViewById(R.id.close_btn)

        close_btn.setOnClickListener{
            if(!isSoundOff){
                btnDefaultMusic.start()
            }
            dialog.hide()
        }

        val resume_btn : RelativeLayout = dialog.findViewById(R.id.resume_btn)

        resume_btn.setOnClickListener{
            if(!isSoundOff){
                btnDefaultMusic.start()
            }
            dialog.hide()
        }

        val restart_btn : RelativeLayout = dialog.findViewById(R.id.restart_btn)

        restart_btn.setOnClickListener{
            dialog.hide()
            recreate()
        }

        val setting_btn : ImageView = dialog.findViewById(R.id.setting_btn)

        setting_btn.setOnClickListener {
            showCustomDialog()
        }

        val help_btn : ImageView = dialog.findViewById(R.id.help_btn)

        help_btn.setOnClickListener {
            showHelpDialog()
        }

        val home_btn : ImageView = dialog.findViewById(R.id.home_btn)

        home_btn.setOnClickListener {
            showConformation()
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
            gameBgPlayer.release()
            btnDefaultMusic.release()
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
}