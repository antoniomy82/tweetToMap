package com.ajmorales.tweetToMap.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStreamReader

class TweetRepositoryImpl : TweetRepository {

    private var tweets = MutableLiveData<List<Tweet>>()
    var responseStr: String? = "NO"

    val parseScope = CoroutineScope(Dispatchers.IO)

    override fun getTweets(): MutableLiveData<List<Tweet>> {
        return tweets
    }

    override fun callTweetsAPI(str: String) {
        val currentCall: Call<ResponseBody>? = ApiAdapter().api!!.getTweet(str)
        currentCall?.enqueue(streamResponse)
    }

    override fun getResponse(): String {
        return responseStr!!
    }

    private val streamResponse: Callback<ResponseBody> = object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            val tweetsList: ArrayList<Tweet>? = ArrayList<Tweet>()

            if (response.isSuccessful) {
                Log.e("Response: ", "successful!!")
                responseStr = "UPDATE"

                    try {
                        val reader = JsonReader(InputStreamReader(response.body()!!.byteStream()))
                        val gson = GsonBuilder().create()

                        parseScope.launch {
                        for (i in 0..29) {
                            val j = gson.fromJson<JsonObject>(reader, JsonObject::class.java)

                            if (j.getAsJsonObject("user") != null) {
                                val tweet = gson.fromJson(j, Tweet::class.java)
                                tweetsList?.add(tweet)

                                //Show twitter parse by author without location
                                Log.d(
                                    "Parsing..(",
                                    i.toString() + ") [Autor]:" + tweet.user?.name.toString()
                                )

                                //Show twitter parse with locations
                                if (tweet.coordinates != null || tweet.geo != null) {
                                    //  Log.d("Parse", "JSON: $j")

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

                            }
                        }
                     }
                        tweets.value = tweetsList
                        responseStr = "DONE"


                    } catch (e: Exception) {
                        Log.e("Error", "Exception in response : ${e.message}")
                        responseStr = e.message
                       parseScope.cancel()
                    }


            } else {
                responseStr = "WARNING$response"
                Log.e("Warning:", "[Warning] $response")
            }

        }


        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.e("Error", "onFailure")
        }
    }
}


