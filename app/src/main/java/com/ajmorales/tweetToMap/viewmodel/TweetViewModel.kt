package com.ajmorales.tweetToMap.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajmorales.tweetToMap.model.Tweet
import com.ajmorales.tweetToMap.model.TweetObservable

class TweetViewModel : ViewModel() {

    private var tweetObservable: TweetObservable = TweetObservable()

    var iterator: MutableLiveData<Int>? = null
    var searchWord: MutableLiveData<String>? = null

    fun callTweets(word: String) {
        searchWord?.value = word

        tweetObservable.callTweets(word)
    }

    fun getTweets(): MutableLiveData<List<Tweet>> {

        return tweetObservable.getTweets()
    }


    fun getResponse(): String {
        return tweetObservable.getResponse()
    }


    fun callIterator(): MutableLiveData<Int>? {
        if (iterator == null) {
            iterator = MutableLiveData<Int>()
        }
        return iterator
    }

    fun setIterator(iter: Int) {
        iterator?.value = iter
    }

}