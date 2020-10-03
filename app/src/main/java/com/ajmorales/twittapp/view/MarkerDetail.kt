package com.ajmorales.twittapp.view

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ajmorales.twittapp.R
import com.ajmorales.twittapp.model.TweetObservable
import com.squareup.picasso.Picasso

class MarkerDetail : AppCompatActivity() {

    private val tweetsObservable: TweetObservable = TweetObservable()  //@@@@@ Pasarlo a ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker_detail)

        val position = intent.getIntExtra("position", 0)

        val imgProfile: ImageView = findViewById(R.id.imgProfile)
        val tvUserName: TextView = findViewById(R.id.tvUserName)
        val tvText: TextView = findViewById(R.id.tvText)

        tvUserName.text = MainActivity.getTweetAt(position)?.user?.name       //@@@@@@@@@@@@@@@@@@@@
        tvText.text = MainActivity.getTweetAt(position)?.text

        //Cargo el logo almacenado con Picasso
        if ((MainActivity.getTweetAt(position)?.user?.profile_image_url) != null) {
            Picasso.get().load(MainActivity.getTweetAt(position)?.user?.profile_image_url)
                .into(imgProfile)

        } else {
            Picasso.get().load(R.drawable.noimage).into(imgProfile)
        }

    }
}