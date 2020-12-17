package com.robert.phototag

import android.os.Parcel
import android.os.Parcelable

class Tag : Parcelable {
    var tagId: String?
    var x: Float
    var y: Float

    constructor(unique_tag_id: String?, x: Float, y: Float) {
        this.tagId = unique_tag_id
        this.x = x
        this.y = y
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(tagId)
        dest.writeValue(x)
        dest.writeValue(y)
    }

    private constructor(parcel: Parcel) {
        tagId = parcel.readString()
        x = parcel.readValue(Float::class.javaPrimitiveType!!.classLoader) as Float
        y = parcel.readValue(Float::class.javaPrimitiveType!!.classLoader) as Float
    }

    companion object CREATOR: Parcelable.Creator<Tag> {
        override fun createFromParcel(parcel: Parcel): Tag {
            return Tag(parcel)
        }

        override fun newArray(size: Int): Array<Tag?> {
            return arrayOfNulls(size)
        }
    }
}