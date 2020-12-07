package com.robert.phototagsample.utilities

import android.content.res.Resources
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringWriter
import java.io.Writer

object RawJsonToStringUtil {
    private val TAG = RawJsonToStringUtil::class.java.simpleName
    fun rawJsonToString(resources: Resources, id: Int): String {
        val resourceReader = resources.openRawResource(id)
        val writer: Writer = StringWriter()
        try {
            val reader = BufferedReader(InputStreamReader(resourceReader, "UTF-8"))
            var line = reader.readLine()
            while (line != null) {
                writer.write(line)
                line = reader.readLine()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unhandled exception while using rawJsonToString", e)
        } finally {
            try {
                resourceReader.close()
            } catch (e: Exception) {
                Log.e(TAG, "Unhandled exception while using rawJsonToString", e)
            }
        }
        return writer.toString()
    }
}