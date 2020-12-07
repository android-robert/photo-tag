package com.robert.phototag

import android.os.Parcel
import android.os.Parcelable

class Tag : Parcelable {
    var unique_tag_id: String?
    var x_co_ord: Float
    var y_co_ord: Float

    constructor(unique_tag_id: String?, x_co_ord: Float, y_co_ord: Float) {
        this.unique_tag_id = unique_tag_id
        this.x_co_ord = x_co_ord
        this.y_co_ord = y_co_ord
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(unique_tag_id)
        dest.writeValue(x_co_ord)
        dest.writeValue(y_co_ord)
    }

    private constructor(`in`: Parcel) {
        unique_tag_id = `in`.readString()
        x_co_ord = `in`.readValue(Float::class.javaPrimitiveType!!.classLoader) as Float
        y_co_ord = `in`.readValue(Float::class.javaPrimitiveType!!.classLoader) as Float
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Tag> = object : Parcelable.Creator<Tag> {
            override fun createFromParcel(source: Parcel): Tag {
                return Tag(source)
            }

            override fun newArray(size: Int): Array<Tag?> {
                return arrayOfNulls(size)
            }
        }
    }
}