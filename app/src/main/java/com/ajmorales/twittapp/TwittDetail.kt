package com.ajmorales.twittapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class TwittDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitt_detail)

        val tvTittle: TextView=findViewById(R.id.tvTittle)

        tvTittle.text=intent.getStringExtra("tittle")

    }
}