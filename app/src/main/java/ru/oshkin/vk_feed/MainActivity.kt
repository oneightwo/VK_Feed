package ru.oshkin.vk_feed

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.oshkin.vk_feed.retrofit.FeedResponse
import ru.oshkin.vk_feed.retrofit.Network
import ru.oshkin.vk_feed.retrofit.RequestModel

class MainActivity : AppCompatActivity() {

    private val retrofit by lazy { Network.retrofit }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val text = findViewById<TextView>(R.id.tv)

    }
}
