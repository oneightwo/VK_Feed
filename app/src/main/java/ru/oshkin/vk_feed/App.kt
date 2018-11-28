package ru.oshkin.vk_feed

import android.app.Application
import ru.oshkin.vk_feed.tools.CacheManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        UserData.init(applicationContext)
        CacheManager.init(applicationContext)
    }
}