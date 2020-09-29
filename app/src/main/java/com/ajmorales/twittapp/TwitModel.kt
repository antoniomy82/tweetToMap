package com.ajmorales.twittapp

import com.google.android.gms.maps.model.LatLng

class TwitModel(latLong: LatLng, tittle: String) {
    var latlong: LatLng? = null
    var tittle: String? = null

    init {
        this.latlong = latLong
        this.tittle = tittle
    }
}