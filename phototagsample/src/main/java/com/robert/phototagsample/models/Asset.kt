package com.robert.phototagsample.models

import android.os.Parcel
import android.os.Parcelable

class Asset : Parcelable {
    var name: String?
    var id: Int

    constructor(name: String?, id: Int) {
        this.name = name
        this.id = id
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeInt(id)
    }

    protected constructor(`in`: Parcel) {
        name = `in`.readString()
        id = `in`.readInt()
    }

    companion object {
        val CREATOR: Parcelable.Creator<Asset> = object : Parcelable.Creator<Asset> {
            override fun createFromParcel(source: Parcel): Asset {
                return Asset(source)
            }

            override fun newArray(size: Int): Array<Asset?> {
                return arrayOfNulls(size)
            }
        }
    }
}