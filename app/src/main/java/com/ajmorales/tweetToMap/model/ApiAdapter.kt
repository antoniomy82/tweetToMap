package com.ajmorales.tweetToMap.model

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor
import java.util.concurrent.TimeUnit

class ApiAdapter {

    private val apiKey = "RUkM3QSu5jV3CdyUjnitHVFtT"
    private val apiSecret = "e3DVxZM8XOqFPWA9mYN7rW0NzuaGQSJCWD6sLYxr6e0WMnOy9h"
    private val accessToken = "1310541059802509313-QtjoCoe5oMXiY3fsiXrvIgw54LVKF8"
    private val accessTokenSecret = "slI2TpqjSgK1wK5ZTYzzjGqJVyVOscXyPb3xfkLY5GY5Q"

    private val url = "https://stream.twitter.com/1.1/"
    var api: ApiService? = null

    init {
        val consumer = OkHttpOAuthConsumer(
            apiKey,
            apiSecret
        )
        consumer.setTokenWithSecret(
            accessToken,
            accessTokenSecret
        )

        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .addInterceptor(SigningInterceptor(consumer))
            .build()


        // Retrofit builder to build a retrofit object.
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(ApiService::class.java)
    }
}