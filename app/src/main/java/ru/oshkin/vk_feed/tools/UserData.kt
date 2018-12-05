package ru.oshkin.vk_feed.tools

import android.content.Context
import android.preference.PreferenceManager


class UserData private constructor(context: Context) {
    companion object {
        lateinit var instance: UserData
//        lateinit var instance: UserData

        fun init(context: Context){
            instance = UserData(context)
        }
    }

    private val TOKEN: String = "token"
    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    fun saveToken(token: String) {
        preferences.edit().putString(TOKEN, token).apply()
    }

    fun deleteToken() {
        preferences.edit().putString(TOKEN, "").apply()
    }

    fun getToken(): String = preferences.getString(TOKEN, "") ?: ""
}