package com.ajmorales.tweetToMap.model

// 0:latitude, 1:LONGITUDE
class Geo(type: String, coordinates: List<Double>) {
    var type: String? = null
    var coordinates: List<Double>? = null

    init {
        this.type = type
        this.coordinates = coordinates
    }
}

