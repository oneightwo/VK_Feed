package ru.oshkin.vk_feed

import android.content.Context
import android.preference.PreferenceManager


class UserData(context: Context) {

    companion object {

        lateinit var instance: UserData

        fun init(context: Context){
            instance = UserData(context)
        }
    }
    private val TOKEN: String = "token"
    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    fun saveData(token: String) {
        preferences.edit().putString(TOKEN, token).apply()
    }

    fun checkData(): Boolean {
        val token = preferences.getString(TOKEN, "")
        return token != ""
    }

    fun getToken(): String = preferences.getString(TOKEN, "") ?: ""
}