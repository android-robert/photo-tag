package com.robert.phototagsample

import android.app.Application
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robert.phototagsample.interfaces.AppConstants
import com.robert.phototagsample.interfaces.AppConstants.PreferenceKeys
import com.robert.phototagsample.models.Photo
import com.robert.phototagsample.utilities.JsonUtil
import com.robert.phototagsample.utilities.RawJsonToStringUtil
import com.robert.phototagsample.utilities.UsersData
import net.grandcentrix.tray.AppPreferences
import java.util.*

class PhotoTagApplication : Application(), AppConstants {
    var tagShowAnimation = 0
    var tagHideAnimation = 0
    var likeAnimation = 0
    var carrotTopColor = 0
    var tagBackgroundColor = 0
    var tagTextColor = 0
    var likeColor = 0
    override fun onCreate() {
        super.onCreate()
        appPreferences = AppPreferences(photoTagApplication)
        UsersData.users.addAll(UsersData.getUsersFromJson(RawJsonToStringUtil.rawJsonToString(photoTagApplication!!.resources, R.raw.users)))
        tagShowAnimation = R.anim.zoom_in
        tagHideAnimation = R.anim.zoom_out

        carrotTopColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        tagBackgroundColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        tagTextColor = ContextCompat.getColor(this, android.R.color.white)
        likeColor = ContextCompat.getColor(this, android.R.color.white)
    }

    val photos: ArrayList<Photo>
        get() {
            val json = appPreferences!!.getString(PreferenceKeys.TAGGED_PHOTOS, "")
            val photoArrayList: ArrayList<Photo>
            photoArrayList = if (json != "") {
                Gson().fromJson(json, object : TypeToken<ArrayList<Photo?>?>() {}.type)
            } else {
                ArrayList()
            }
            return photoArrayList
        }

    fun savePhotos(photoArrayList: ArrayList<Photo>?) {
        appPreferences!!.put(
                PreferenceKeys.TAGGED_PHOTOS,
                JsonUtil.toJson(photoArrayList
                ))
    }

    companion object {
        private var photoTagApplication: PhotoTagApplication? = null
        private var appPreferences: AppPreferences? = null
        val instance: PhotoTagApplication?
            get() {
                if (photoTagApplication == null) {
                    photoTagApplication = PhotoTagApplication()
                }
                return photoTagApplication
            }
    }

    init {
        photoTagApplication = this
    }
}