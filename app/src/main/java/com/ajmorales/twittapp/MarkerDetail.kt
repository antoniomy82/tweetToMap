package com.ajmorales.twittapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MarkerDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker_detail)

        val tvTittle: TextView = findViewById(R.id.tvTittle)

        tvTittle.text = intent.getStringExtra("tittle")

    }
}