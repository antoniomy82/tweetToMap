package com.ajmorales.twittapp.model

import android.os.Parcel
import android.os.Parcelable

// 0: LONGITUDE, 1:latitude
class Coordinates() : Parcelable {
    var type: String? = null
    var coordinates: List<Double>? = null

    constructor(parcel: Parcel) : this() {
        type = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Coordinates> {
        override fun createFromParcel(parcel: Parcel): Coordinates {
            return Coordinates(parcel)
        }

        override fun newArray(size: Int): Array<Coordinates?> {
            return arrayOfNulls(size)
        }
    }
}