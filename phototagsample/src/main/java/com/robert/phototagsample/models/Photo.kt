package com.robert.phototagsample.models

import android.os.Parcel
import android.os.Parcelable
import com.robert.phototag.Tag
import java.util.*

class Photo : Parcelable {
    var id: String? = null
    var imageUri: String? = null
    var tags: ArrayList<Tag>? = null

    constructor() {}
    constructor(id: String?, imageUri: String?, tags: ArrayList<Tag>?) {
        this.id = id
        this.imageUri = imageUri
        this.tags = tags
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(imageUri)
        dest.writeTypedList(tags)
    }

    private constructor(parcel: Parcel) {
        id = parcel.readString()
        imageUri = parcel.readString()
        tags = parcel.createTypedArrayList(Tag.CREATOR)
    }


    companion object CREATOR : Parcelable.Creator<Photo> {
        override fun createFromParcel(parcel: Parcel): Photo {
            return Photo(parcel)
        }

        override fun newArray(size: Int): Array<Photo?> {
            return arrayOfNulls(size)
        }
    }
}