package com.ajmorales.twittapp.model

import android.os.Parcel
import android.os.Parcelable

// 0:latitude, 1:LONGITUDE
class Geo(type: String, coordinates: List<Double>) : Parcelable {
    var type: String? = null
    var coordinates: List<Double>? = null

    constructor(parcel: Parcel) : this(
        TODO("type"),
        TODO("coordinates")
    ) {
        type = parcel.readString()
    }

    init {
        this.type = type
        this.coordinates = coordinates
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Geo> {
        override fun createFromParcel(parcel: Parcel): Geo {
            return Geo(parcel)
        }

        override fun newArray(size: Int): Array<Geo?> {
            return arrayOfNulls(size)
        }
    }

}