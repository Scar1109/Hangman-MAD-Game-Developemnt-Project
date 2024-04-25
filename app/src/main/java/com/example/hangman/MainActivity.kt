package com.example.hangman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import com.example.hangman.modules.HomePage

class MainActivity : AppCompatActivity() {
    private lateinit var loadingBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        loadingBar = findViewById(R.id.loading)

        val intent = Intent(this, HomePage::class.java)

        progressWithAnimation(0, 100, 3000, intent)

    }

    private fun progressWithAnimation(startProgress: Int, endProgress: Int, duration: Long, intent : Intent) {
        var currentProgress = startProgress
        val step = 1
        val totalTime = duration / (endProgress - startProgress)

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                loadingBar.progress = currentProgress
                currentProgress += step
                if (currentProgress <= endProgress) {
                    handler.postDelayed(this, totalTime)
                } else {
                    startActivity(intent)
                    finish()
                }
            }
        }, totalTime)
    }

}