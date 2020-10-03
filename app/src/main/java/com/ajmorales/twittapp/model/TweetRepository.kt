package com.ajmorales.twittapp.model

import androidx.lifecycle.MutableLiveData

interface TweetRepository {

    fun getTweet(): MutableLiveData<List<Tweet>>
    fun getTweetsAPI(str: String)
}