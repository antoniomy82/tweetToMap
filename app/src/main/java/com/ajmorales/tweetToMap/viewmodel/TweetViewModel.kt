package com.ajmorales.tweetToMap.viewmodel

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajmorales.tweetToMap.model.Tweet
import com.ajmorales.tweetToMap.model.TweetObservable

class TweetViewModel() : ViewModel(), Parcelable {

    private var tweetObservable: TweetObservable = TweetObservable()
    var myTweets: MutableLiveData<List<Tweet>>? = null
    var myTweetList: List<Tweet>? = null

    var iterator: MutableLiveData<Int>? = null
    private var searchWord: MutableLiveData<String>? = null

    constructor(parcel: Parcel) : this() {
        myTweetList = parcel.createTypedArrayList(Tweet)
    }

    fun callTweets(word: String) {
        searchWord?.value = word
        tweetObservable.callTweets(word)
    }

    fun getTweets(): MutableLiveData<List<Tweet>> {
        return tweetObservable.getTweets()
    }


    fun getMyTweet(position: Int): Tweet? {

        return myTweetList?.get(position)
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


    /*
      Parcelable
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(myTweetList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TweetViewModel> {
        override fun createFromParcel(parcel: Parcel): TweetViewModel {
            return TweetViewModel(parcel)
        }

        override fun newArray(size: Int): Array<TweetViewModel?> {
            return arrayOfNulls(size)
        }
    }


}