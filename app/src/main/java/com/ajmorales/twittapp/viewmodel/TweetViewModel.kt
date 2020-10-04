package com.ajmorales.twittapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajmorales.twittapp.model.Geo
import com.ajmorales.twittapp.model.Tweet
import com.ajmorales.twittapp.model.TweetObservable

class TweetViewModel : ViewModel() {

    private var tweetObservable: TweetObservable = TweetObservable()
    private var myTweets: List<Tweet>? = null

    fun callTweets(word: String) {
        tweetObservable.callTweets(word)
    }

    private fun getTweets(): MutableLiveData<List<Tweet>> {
        return tweetObservable.getTweets()
    }

    fun setListTweets() {
        myTweets = null
        myTweets = getTweets().value
    }

    fun getListTweets(): List<Tweet>? {
        return myTweets
    }

    fun getResponse(): String {
        return tweetObservable.getResponse()
    }

    fun getSimulatedLocations(): ArrayList<Geo> {

        val locationLits: ArrayList<Geo> = ArrayList<Geo>()

        locationLits.add(Geo("Málaga", listOf(36.72016, -4.42034)))
        locationLits.add(Geo("Londres", listOf(51.51279, -0.09184)))
        locationLits.add(Geo("Tetuan", listOf(35.5784500, -5.3683700)))
        locationLits.add(Geo("Madrid", listOf(40.4631, -3.6501)))
        locationLits.add(Geo("Granada ", listOf(37.18817, -3.60667)))
        locationLits.add(Geo("Nueva York", listOf(40.73, -73.99)))
        locationLits.add(Geo("Mexico", listOf(19.4978, -99.1269)))
        locationLits.add(Geo("Buenos Aires", listOf(-34.61315, -58.37723)))
        locationLits.add(Geo("Toronto", listOf(43.70011, -79.4163)))
        locationLits.add(Geo("Sydney", listOf(-33.862, 151.21)))

        return locationLits
    }


}