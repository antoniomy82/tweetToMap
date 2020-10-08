package com.ajmorales.tweetToMap.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ajmorales.tweetToMap.BR
import com.ajmorales.tweetToMap.R
import com.ajmorales.tweetToMap.databinding.ActivityMarkerDetailBinding
import com.ajmorales.tweetToMap.viewmodel.TweetViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_marker_detail.*

class MarkerDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Tweet detail"

        val binding: ActivityMarkerDetailBinding =
            ActivityMarkerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val position = intent.getIntExtra("position", 0)

        val model: TweetViewModel? = intent.getParcelableExtra("model")

        binding.setVariable(BR.model, model)
        binding.setVariable(BR.position, position)

        model?.getMyTweet(position)?.user?.profile_image_url

        if ((model?.getMyTweet(position)?.user?.profile_image_url) != null) {
            Picasso.get().load(model.getMyTweet(position)?.user?.profile_image_url).into(imgProfile)
        } else {
            Picasso.get().load(R.drawable.noimage).into(imgProfile)
        }
    }
}