package com.ajmorales.twittapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ajmorales.twittapp.model.Tweet
import com.squareup.picasso.Picasso

class MarkerDetail : AppCompatActivity() {

    private var myTweet: Tweet? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker_detail)

        val position = intent.getIntExtra("position", 0)
        myTweet = MainActivity.getTweet(position)

        val imgProfile: ImageView = findViewById(R.id.imgProfile)
        val tvUserName: TextView = findViewById(R.id.tvUserName)
        val tvText: TextView = findViewById(R.id.tvText)

        tvUserName.text = myTweet?.user?.name
        tvText.text = myTweet?.text

        //val tvTittle: TextView = findViewById(R.id.tvTittle)

        // tvTittle.text= myTweet?.user?.name

        //Cargo el logo almacenado con Picasso
        if ((myTweet?.user?.profile_image_url) != null) {
            Picasso.get().load(myTweet?.user?.profile_image_url).into(imgProfile)

        } else {
            Picasso.get().load(R.drawable.noimage).into(imgProfile)
        }

    }
}