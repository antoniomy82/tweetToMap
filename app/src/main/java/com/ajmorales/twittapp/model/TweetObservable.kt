package com.ajmorales.twittapp.model

import androidx.lifecycle.MutableLiveData

class TweetObservable {

    private var tweetRepository: TweetRepository = TweetRepositoryImpl()

    //Repository
    fun callTweets(str: String) {
        tweetRepository.callTweetsAPI(str)
    }

    //ViewModel
    fun getTweets(): MutableLiveData<List<Tweet>> {
        return tweetRepository.getTweets()
    }

    fun getResponse(): String {
        return tweetRepository.getResponse()
    }
}