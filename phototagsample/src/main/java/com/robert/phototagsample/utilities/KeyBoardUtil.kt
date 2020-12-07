package com.robert.phototagsample.utilities

import android.app.Activity
import android.view.inputmethod.InputMethodManager

object KeyBoardUtil {
    fun hideKeyboard(activity: Activity?) {
        val inputMethodManager = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (activity.currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
    }

    fun showKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (activity.currentFocus != null) {
            inputMethodManager.showSoftInput(activity.currentFocus, InputMethodManager.SHOW_FORCED)
        }
    }
}