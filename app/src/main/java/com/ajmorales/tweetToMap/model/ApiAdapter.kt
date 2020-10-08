package com.ajmorales.tweetToMap.model

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor
import java.util.concurrent.TimeUnit


class ApiAdapter {

    //Twitter API Keys
    private val apiKey = "YOUR_API_KEY"
    private val apiSecret = "YOUR_API_KEY"
    private val accessToken = "YOUR_API_KEY"
    private val accessTokenSecret = "YOUR_API_KEY"

    private val url = "https://stream.twitter.com/1.1/"
    var api: ApiService? = null

    init {
        val consumer = OkHttpOAuthConsumer(apiKey, apiSecret)

        consumer.setTokenWithSecret(accessToken, accessTokenSecret)

        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .addInterceptor(SigningInterceptor(consumer))
            .build()


        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(ApiService::class.java)
    }
}
