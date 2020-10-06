package com.ajmorales.tweetToMap.view

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ajmorales.tweetToMap.R
import com.ajmorales.tweetToMap.model.Tweet
import com.ajmorales.tweetToMap.model.User
import com.squareup.picasso.Picasso

class MarkerDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker_detail)
        supportActionBar?.title = "Tweet detail"

        val imgProfile: ImageView = findViewById(R.id.imgProfile)
        val tvUserName: TextView = findViewById(R.id.tvUserName)
        val tvText: TextView = findViewById(R.id.tvText)

        val tweetSelected: Tweet? = intent.getParcelableExtra("tweet") as Tweet?
        val userSelected: User? = intent.getParcelableExtra("user") as User?

        tvUserName.text = userSelected!!.name
        tvText.text = tweetSelected!!.text

        if ((userSelected.profile_image_url) != null) {
            Picasso.get().load(userSelected.profile_image_url)
                .into(imgProfile)

        } else {
            Picasso.get().load(R.drawable.noimage).into(imgProfile)
        }
    }
}