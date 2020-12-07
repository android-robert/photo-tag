package com.robert.phototagsample.utilities

import android.util.Log
import com.google.gson.Gson

object JsonUtil {
    fun toJson(`object`: Any?): String {
        try {
            val gson = Gson()
            return gson.toJson(`object`)
        } catch (e: Exception) {
            Log.e(JsonUtil::class.java.simpleName, "Error In Converting ModelToJson", e)
        }
        return ""
    }
}