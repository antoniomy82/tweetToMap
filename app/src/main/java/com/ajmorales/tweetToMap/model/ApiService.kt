package com.ajmorales.tweetToMap.model

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ApiService {

    @Streaming
    @POST("statuses/filter.json")
    fun getTweet(@Query("track") terms: String?): Call<ResponseBody>
}