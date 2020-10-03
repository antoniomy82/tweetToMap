package com.ajmorales.twittapp.model

import androidx.lifecycle.MutableLiveData

class TweetObservable {

    private var tweetRepository: TweetRepository = TweetRepositoryImpl()

    //Repository
    fun callTweets(str: String) {
        tweetRepository.getTweetsAPI(str)
    }

    //ViewModel
    fun getTweet(): MutableLiveData<List<Tweet>> {
        return tweetRepository.getTweet()
    }
}