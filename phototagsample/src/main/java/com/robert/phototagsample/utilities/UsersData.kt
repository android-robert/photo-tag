package com.robert.phototagsample.utilities

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robert.phototagsample.interfaces.AppConstants
import com.robert.phototagsample.models.User
import java.util.*

object UsersData: AppConstants {

    val users = ArrayList<User>()

    fun getFilteredUsers(searchString: String?): ArrayList<User> {
        val filteredUser = ArrayList<User>()
        for (user in users) {
            searchString?.let {  searchStr ->
                if (user.fullName!!.contains(searchStr) ||  user.fullName!!.contains(searchStr)) {
                    filteredUser.add(user)
                }
            }
        }
        if (filteredUser.isEmpty()) {
            filteredUser.add(User(AppConstants.NO_USER_FOUND, AppConstants.NO_USER_FOUND, AppConstants.NO_USER_FOUND))
        }
        return filteredUser
    }

    fun getUsersFromJson(json: String?): ArrayList<User> {
        return if (json != "") {
            Gson().fromJson(json, object : TypeToken<ArrayList<User>?>() {}.type)
        } else {
            ArrayList()
        }
    }
}