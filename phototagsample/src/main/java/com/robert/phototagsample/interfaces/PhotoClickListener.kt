package com.robert.phototagsample.interfaces

import com.robert.phototagsample.models.Photo

interface PhotoClickListener {
    fun onPhotoClick(photo: Photo?, position: Int)
}