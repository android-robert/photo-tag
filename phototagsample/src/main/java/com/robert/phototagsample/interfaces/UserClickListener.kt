package com.robert.phototagsample.interfaces

import com.robert.phototagsample.models.User

interface UserClickListener {
    fun onUserClick(user: User?, position: Int)
}