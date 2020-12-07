package com.robert.phototag

import android.view.GestureDetector
import android.view.GestureDetector.OnDoubleTapListener
import android.view.MotionEvent

interface GestureListener: GestureDetector.OnGestureListener, OnDoubleTapListener {
    override fun onDown(e: MotionEvent): Boolean
    override fun onSingleTapConfirmed(e: MotionEvent): Boolean
    override fun onSingleTapUp(e: MotionEvent): Boolean
    override fun onShowPress(e: MotionEvent)
    override fun onDoubleTap(e: MotionEvent): Boolean
    override fun onDoubleTapEvent(e: MotionEvent): Boolean
    override fun onLongPress(e: MotionEvent)
    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean
    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean
}