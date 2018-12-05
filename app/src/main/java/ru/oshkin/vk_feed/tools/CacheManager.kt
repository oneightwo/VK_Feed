package ru.oshkin.vk_feed.tools

import android.content.Context
import com.google.gson.Gson
import ru.oshkin.vk_feed.retrofit.Module.FeedResponse
import ru.oshkin.vk_feed.retrofit.Module.Profile
import java.io.*
import java.lang.Exception

class CacheManager(context: Context) {
    private val newsFile = File(context.cacheDir, FILE_NEWS_FEED).absolutePath
    private val recommendationFile = File(context.cacheDir, FILE_RECOMMENDATION).absolutePath
    private val profileFile = File(context.cacheDir, FILE_PROFILE).absolutePath
    private val gson = Gson()

    private fun writeToFile(fileName: String, data: String) {
        val writer = BufferedWriter(FileWriter(fileName))
        writer.write(data)
        writer.close()
    }

    private fun readFromFile(fileName: String): String {
        val br = BufferedReader(FileReader(File(fileName)))
        val sb = StringBuilder()
        var str: String?
        do {
            str = br.readLine()
            if (str != null) sb.append(str)
        } while (str != null)
        return sb.toString()
    }

    fun saveFeed(feedResponse: FeedResponse, isFeed: Boolean) {
        val serialized = gson.toJson(feedResponse)
        writeToFile(if (isFeed) newsFile else recommendationFile, serialized)
    }

    fun saveProfile(profile: Profile) {
        val serialized = gson.toJson(profile)
        writeToFile(profileFile, serialized)
    }

    fun getFeed(isFeed: Boolean): FeedResponse? {
        try {
            val data = readFromFile(if (isFeed) newsFile else recommendationFile)
            val deserialized = gson.fromJson<FeedResponse>(data, FeedResponse::class.java)
            return deserialized
        } catch (e: Exception) {
            return null
        }
    }

    fun getProfile(): Profile {
        val deserialized = gson.fromJson<Profile>(readFromFile(profileFile), Profile::class.java)
        return deserialized
    }

    companion object {
        const val FILE_NEWS_FEED = "FileNewsFeed"
        const val FILE_RECOMMENDATION = "FileRecommendation"
        const val FILE_PROFILE = "FileProfile"
        lateinit var instance: CacheManager

        fun init(context: Context) {
            instance = CacheManager(context)
        }
    }
}