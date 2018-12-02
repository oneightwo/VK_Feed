package ru.oshkin.vk_feed.tools

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        UserData.init(applicationContext)
        CacheManager.init(applicationContext)
    }
}