package com.example.hangman.modules

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
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
    private var difficulty : String = "easy"
    private val textViewIds = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_screen)

        sharedPreferences = getSharedPreferences("HangmanPrefs", Context.MODE_PRIVATE)

        checkAndSetDefaultDifficulty()
        difficulty = sharedPreferences.getString("difficulty", "easy").toString()

        gameBgPlayer = GameMediaPlayerManager.getMediaPlayer(this)

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

                val word_section : LinearLayout = findViewById(R.id.word_section)

                word?.forEach { char ->
                    val itemView = layoutInflater.inflate(R.layout.word_letter_item, null) as LinearLayout
                    val textView = itemView.findViewById<TextView>(R.id.letterTextView)
                    textView.text = char.toString().toUpperCase()
                    val textViewId = View.generateViewId() // Generate unique ID for each TextView
                    textView.id = textViewId
                    textViewIds.add(textViewId)
                    // Add the itemView to the LinearLayout
                    word_section.addView(itemView)
                }
            }, delay)
        }


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
        gameBgPlayer.release()
    }

    private fun saveDifficultyToPrefs(difficulty: String) {
        sharedPreferences.edit().putString("difficulty", difficulty).apply()
    }

    private fun checkAndSetDefaultDifficulty() {
        if (!sharedPreferences.contains("difficulty")) {
            saveDifficultyToPrefs("easy")
        }
    }
}