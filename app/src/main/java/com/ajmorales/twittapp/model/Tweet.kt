package com.ajmorales.twittapp.model

import android.os.Parcel
import android.os.Parcelable

class Tweet() : Parcelable {
    var createdAt: String? = null
    var text: String? = null
    var user: User? = null
    var geo: Geo? = null
    var coordinates: Coordinates? = null

    constructor(parcel: Parcel) : this() {
        createdAt = parcel.readString()
        text = parcel.readString()
        user = parcel.readParcelable(User::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(createdAt)
        parcel.writeString(text)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tweet> {
        override fun createFromParcel(parcel: Parcel): Tweet {
            return Tweet(parcel)
        }

        override fun newArray(size: Int): Array<Tweet?> {
            return arrayOfNulls(size)
        }
    }

}


