package ru.oshkin.vk_feed

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        UserData.init(applicationContext)
    }
}