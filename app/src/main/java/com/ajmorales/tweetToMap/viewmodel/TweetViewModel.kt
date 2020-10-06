package com.ajmorales.tweetToMap.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajmorales.tweetToMap.model.Tweet
import com.ajmorales.tweetToMap.model.TweetObservable
import com.google.android.gms.maps.model.Marker

class TweetViewModel : ViewModel() {

    private var tweetObservable: TweetObservable = TweetObservable()
    var myMarker: MutableLiveData<Marker>? = null

    var iterator: MutableLiveData<Int>? = null
    private var searchWord: MutableLiveData<String>? = null

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
            iterator?.value = 0
        }
        return iterator
    }

    fun setIterator(iter: Int) {
        iterator?.value = iter
    }

}