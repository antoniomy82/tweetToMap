package com.ajmorales.twittapp.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStreamReader

class TweetRepositoryImpl : TweetRepository {

    private var tweets = MutableLiveData<List<Tweet>>()

    //Ver como obtener la posición dentro de un MutableLiveData
    override fun getTweet(): MutableLiveData<List<Tweet>> {
        return tweets
    }

    override fun getTweetsAPI(str: String) {
        val currentCall: Call<ResponseBody>? = ApiAdapter().api!!.getTweet(str)
        currentCall?.enqueue(streamResponse)
    }

    private val streamResponse: Callback<ResponseBody> = object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            val myTweetList: ArrayList<Tweet>? = ArrayList<Tweet>()
            Log.d("debug", "Getting data - ON RESPONSE")

            if (response.isSuccessful) {
                Log.e("SUCCESS!", "Call OK")


                try {
                    val reader = JsonReader(InputStreamReader(response.body()!!.byteStream()))
                    val gson = GsonBuilder().create()

                    var i = 0

                    while (i < 10) {
                        val j = gson.fromJson<JsonObject>(reader, JsonObject::class.java)

                        if (j.getAsJsonObject("user") != null) {
                            val tweet = gson.fromJson(j, Tweet::class.java)

                            myTweetList?.add(tweet)

                            Log.d(
                                "Searching location(",
                                i.toString() + ") [Autor]:" + tweet.user?.name.toString()
                            )

                            if (tweet.coordinates != null || tweet.geo != null) {
                                //  Log.d("debug", "JSON: $j")

                                Log.d(
                                    "\n Location found!",
                                    tweet.user?.name.toString() + " GEO: [" + tweet.geo?.coordinates?.get(
                                        0
                                    ) + "," + tweet.geo?.coordinates?.get(1) + "] Coordinates [" +
                                            tweet.coordinates?.coordinates?.get(0) + "," + tweet.coordinates?.coordinates?.get(
                                        1
                                    ) + "]\n[Text:]" + tweet.text
                                )
                            }
                            i++
                        }
                    }
                    tweets.value = myTweetList

                } catch (e: Exception) {
                    Log.e("error", "ERROR : ${e.message}")
                }

            } else {
                Log.e("ERROR:", "[Warning]" + response.toString())
            }

            Thread.interrupted()
        }


        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.e("error", "onFailure call")
        }
    }

}


