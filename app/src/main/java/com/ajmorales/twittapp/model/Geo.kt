package com.ajmorales.twittapp.model

class Geo(type: String?, coordinates: List<Double>) {
    // 0:latitude, 1:LONGITUDE
    var type: String? = null
    var coordinates: List<Double>? = null

    init {
        this.type = type
        this.coordinates = coordinates
    }

}