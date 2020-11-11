package com.ajmorales.tweetToMap.viewmodel

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajmorales.tweetToMap.databinding.ActivityMapsBinding
import com.ajmorales.tweetToMap.model.Geo
import com.ajmorales.tweetToMap.model.Tweet
import com.ajmorales.tweetToMap.model.TweetObservable
import com.ajmorales.tweetToMap.util.Util
import com.google.android.gms.maps.model.LatLng

class TweetViewModel() : ViewModel(), Parcelable {

    private var tweetObservable: TweetObservable = TweetObservable()
    private var myLocations: ArrayList<Geo>? = ArrayList<Geo>()
    private val util= Util()
    var myTweets: MutableLiveData<List<Tweet>>? = null
    var iterator: MutableLiveData<Int>? = null
    var myTweetList: List<Tweet>? = null
    var lifeSpan: Long = 5000
    var mySearchWord:String?=null


    constructor(parcel: Parcel) : this() {
        myTweetList = parcel.createTypedArrayList(Tweet)
    }

    private fun callTweets(word: String) {
        tweetObservable.callTweets(word)
    }

    private fun getTweets(): MutableLiveData<List<Tweet>> {
        return tweetObservable.getTweets()
    }

    private fun setIterator(iter: Int) {
        iterator?.value = iter
    }

    private fun getResponse(): String {
        return tweetObservable.getResponse()
    }

    fun getMyTweet(position: Int): Tweet? {
        return myTweetList?.get(position)
    }

    fun callIterator(): MutableLiveData<Int>? {

        if (iterator == null) {
            iterator = MutableLiveData<Int>()
            iterator?.value = 0
        }
        return iterator
    }


  //Logic according to the Tweeter API answer
   fun apiResponses(binding: ActivityMapsBinding?, context: Context):Boolean {
      var iterator:Int


      callIterator()?.value.let {
          iterator = callIterator()?.value!!
      }


        //When the parsing process is done!
        if (getResponse().contains("DONE") && myTweets == null && myTweetList == null) {

            myTweets = getTweets()
            myTweetList = myTweets?.value

            return false
        }

        if (getResponse().contains("UPDATE")) {
            binding?.progressBarVisibility = true

            util.onSnack(binding?.root!!,"UPDATING DATA", lifeSpan)
            return false
        }

        //Warning response 420: rate limited of tweeter Api free , Update map and increase iterator
        if (getResponse().contains("code=420") || getResponse().contains("DONE")) {
            binding?.progressBarVisibility = false

            if (iterator <  myTweetList?.size ?:1) {
                iterator++
                setIterator(iterator)
            }
            else{
                iterator = 0
                if (getResponse().contains("DONE")) {
                    myTweets = null
                    myTweetList = null
                }
                setIterator(iterator)
                mySearchWord?.let { callTweets(it) } //Automatically relaunch binding?.etSearch?.text.toString()
            }

            if (getResponse().contains("code=420")) {
                val timeRemaining =
                    ((lifeSpan / 1000).toInt() * 30) - ((lifeSpan / 1000).toInt() * iterator)
                val msg =
                    "420 Enhance Your Calm:\n Returned by the Twitter Search and Trends API when the client is being rate limited.\n Get a professional API or try increasing the lifespan \n Showing previous tweets!! \n ¡Merezco el curro por los dolores de cabeza! -> API HORRIBLE :-) \n\n Relauching call in: $timeRemaining sec"
                util.onToast(msg, context)
            }
            return true
        }
       return false
    }

  //Returns or emulates (Tweeter Api free) coordinates of a tweet coordinates
    fun getLatLng(iterator:Int): LatLng {
        val myLatLon: LatLng
        myLocations = util.getSimulatedLocations() //load simulated locations

        //If there are geo or coordinates
        if (myTweetList?.get(iterator)?.geo?.coordinates?.get(0) != null && myTweetList?.get(
                iterator
            )?.geo?.coordinates?.get(
                1
            ) != null
        ) {
            myLatLon = LatLng(
                myTweetList?.get(iterator)?.geo?.coordinates!![0],
                myTweetList?.get(iterator)?.geo?.coordinates!![1]
            )
        } else if (myTweetList?.get(iterator)?.coordinates?.coordinates?.get(0) != null && myTweetList?.get(
                iterator
            )?.coordinates?.coordinates?.get(
                1
            ) != null
        ) {
            myLatLon = LatLng(
                myTweetList?.get(iterator)?.coordinates?.coordinates!![1],
                myTweetList?.get(iterator)?.coordinates?.coordinates!![0]
            )
        } else { //Simulated location
            myLatLon = LatLng(
                myLocations?.get(iterator)?.coordinates!![0],
                myLocations?.get(iterator)?.coordinates!![1]
            )
        }
        return myLatLon
    }

    //Click Listeners Maps Activity (Binding)
    fun setupView(binding: ActivityMapsBinding?,  context: Context) {

        //Lifespan spinner
        val lifeSpanList: List<Int> = listOf(5, 7, 9, 10, 12, 15)
        val spAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, lifeSpanList)
        binding?.spLifespan?.adapter = spAdapter

        binding?.spLifespan?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                lifeSpan = (lifeSpanList[position] * 1000.toLong())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) { //Nothing to do
            }
        }

        //Search button
        binding?.btnSearch?.setOnClickListener {

            if (!util.isOnline(context)) {
                // binding?.setVariable(BR.tvSearching, getString(R.string.tvCheckConnection))
                util.hideKeyboard(context, binding.etSearch)
                util.onSnack(binding.root,"CHECK YOUR CONNECTION",lifeSpan)

            } else {
                util.hideKeyboard(context, binding.etSearch)
                util.onSnack(binding.root,"UPDATING DATA",lifeSpan)
                binding.progressBarVisibility = true

                myTweets = null
                myTweetList = null
                mySearchWord=binding.etSearch.text.toString()
                callTweets(mySearchWord!!)
            }
        }
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




