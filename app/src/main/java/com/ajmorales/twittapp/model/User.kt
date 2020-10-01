package com.ajmorales.twittapp.model

import com.google.gson.annotations.SerializedName

class User {
    @SerializedName("profile_image_url_https")
    val profilePic: String? = null

    @SerializedName("name")
    val username: String? = null
}