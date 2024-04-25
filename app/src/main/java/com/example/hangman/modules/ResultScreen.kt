package com.example.hangman.modules

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.hangman.R

class ResultScreen : AppCompatActivity() {

    private var status : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_screen)

        status = intent.getStringExtra("status").toString()
        
        val status_txt : TextView = findViewById(R.id.status_txt)

        status_txt.text = status
    }
}