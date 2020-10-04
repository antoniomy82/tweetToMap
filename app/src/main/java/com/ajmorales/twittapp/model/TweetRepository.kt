package com.ajmorales.twittapp.model

import androidx.lifecycle.MutableLiveData

interface TweetRepository {

    fun getTweets(): MutableLiveData<List<Tweet>>
    fun callTweetsAPI(str: String)
    fun getResponse(): String

}