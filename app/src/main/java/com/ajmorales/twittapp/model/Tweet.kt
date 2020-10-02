package com.ajmorales.twittapp.model

class Tweet(createdAt: String?, user: User?, text: String?, geo: Geo?) {

    var createdAt: String? = null
    var idStr: String? = null
    var text: String? = null
    var user: User? = null
    var geo: Geo? = null
    var coordinates: Coordinates? = null

    init {
        this.createdAt = createdAt
        this.user = user
        this.text = text
        this.geo = geo
    }
}