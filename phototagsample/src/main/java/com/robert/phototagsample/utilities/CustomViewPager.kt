package com.robert.phototagsample.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomViewPager(context: Context?, attrs: AttributeSet?) : ViewPager(context!!, attrs) {
    private var mEnabled = true
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mEnabled && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return mEnabled && super.onInterceptTouchEvent(event)
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.mEnabled = enabled
    }
}