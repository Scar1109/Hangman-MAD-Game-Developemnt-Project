package com.example.hangman.modules

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import com.example.hangman.R

class HomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        val dufuculty_btn : RelativeLayout = findViewById(R.id.dufuculty_btn)
        val play_btn : ImageView = findViewById(R.id.play_btn)

        dufuculty_btn.setOnClickListener {
            val intent = Intent(this, DificultySelectionScreen::class.java)
            startActivity(intent)
        }

        play_btn.setOnClickListener{
            val intent = Intent(this, GameScreen::class.java)
            startActivity(intent)
        }


    }
}