package ru.oshkin.vk_feed.tools

import android.content.Context
import java.io.*

class CacheManager(context: Context) {
    private val newsFile = File(context.cacheDir, FILE_NEWS_FEED).absolutePath
    private val recommendationFile = File(context.cacheDir, FILE_RECOMMENDATION).absolutePath

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

    fun saveNews(data: String) {
        writeToFile(FILE_NEWS_FEED, data)
    }

    fun saveRecommendation(data: String) {
        writeToFile(FILE_RECOMMENDATION, data)
    }

    fun getNewsFile() {
        readFromFile(FILE_NEWS_FEED)
    }

    fun getRecommendateFile() {
        readFromFile(FILE_RECOMMENDATION)
    }


    companion object {
        const val FILE_NEWS_FEED = "FileNewsFeed"
        const val FILE_RECOMMENDATION = "FileRecommendation"
        lateinit var instance: CacheManager

        fun init(context: Context) {
            instance = CacheManager(context)
        }

    }
}