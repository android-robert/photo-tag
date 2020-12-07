package com.robert.phototagsample.models

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

@SuppressLint("ParcelCreator")
class User : Parcelable {
    var userName: String? = null
    var fullName: String? = null
    var url: String? = null

    constructor() {}
    constructor(userName: String?, fullName: String?, url: String?) {
        this.userName = userName
        this.fullName = fullName
        this.url = url
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(userName)
        dest.writeString(fullName)
        dest.writeString(url)
    }

    private constructor(`in`: Parcel) {
        userName = `in`.readString()
        fullName = `in`.readString()
        url = `in`.readString()
    }

    companion object {
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User {
                return User(source)
            }

            override fun newArray(size: Int): Array<User?> {
                return arrayOfNulls(size)
            }
        }
    }
}