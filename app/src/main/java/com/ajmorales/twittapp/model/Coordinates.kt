package com.ajmorales.twittapp.model

// 0: LONGITUDE, 1:latitude
class Coordinates(type: String, coordinates: List<Double>) {
    var type: String? = null
    var coordinates: List<Double>? = null

    init {
        this.type = type
        this.coordinates = coordinates
    }
}