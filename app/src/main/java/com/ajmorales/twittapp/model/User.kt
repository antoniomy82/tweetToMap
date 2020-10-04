package com.ajmorales.twittapp.model

import android.os.Parcel
import android.os.Parcelable


class User() : Parcelable {

    var profile_image_url: String? = null
    var name: String? = null

    constructor(parcel: Parcel) : this() {
        profile_image_url = parcel.readString()
        name = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(profile_image_url)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}