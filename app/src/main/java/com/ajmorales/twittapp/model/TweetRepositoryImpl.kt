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


    private var tweets =
        MutableLiveData<List<Tweet>>() //Cualquier dato que pueda sudecer puede refescar a los demás "MutableLiveData", es parte del patrón observador.

    var responseStr: String? = "NO"


    //Subject MutableLiveData
    //Observers List tweet - Cuando la lista cambia va a afertar al estado del subjeto
    //Change List tweet- MutableLiveData
    //Observed - Actualizar los cambios dónde se esté llamando el método especial.

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

            Log.d("debug", "Getting data - ON RESPONSE")

            if (response.isSuccessful) {
                Log.e("SUCCESS!", "Call OK")

                try {
                    val reader = JsonReader(InputStreamReader(response.body()!!.byteStream()))
                    val gson = GsonBuilder().create()

                    var i = 0

                    while (i < 40) {
                        val j = gson.fromJson<JsonObject>(reader, JsonObject::class.java)

                        if (j.getAsJsonObject("user") != null) {
                            val tweet = gson.fromJson(j, Tweet::class.java)

                            tweetsList?.add(tweet)

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

                    tweets.value = tweetsList
                    responseStr = "OK"

                } catch (e: Exception) {
                    Log.e("error", "ERROR : ${e.message}")
                    responseStr = e.message
                }

            } else {
                responseStr = "WARNING" + response.toString()
                Log.e("Warning:", "[Warning] " + response.toString())
            }
        }


        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            Log.e("error", "onFailure call")
        }
    }


}


