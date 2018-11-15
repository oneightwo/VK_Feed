package ru.oshkin.vk_feed.newsLine

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.oshkin.vk_feed.R
import ru.oshkin.vk_feed.UserData
import ru.oshkin.vk_feed.retrofit.FeedResponse
import ru.oshkin.vk_feed.retrofit.GetNews
import ru.oshkin.vk_feed.retrofit.Network
import ru.oshkin.vk_feed.retrofit.RequestModel

class NewsActivity : AppCompatActivity() {

    private val adapter by lazy { AdapterNews(::loadData) }
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        val recyclerView : RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        swipeRefreshLayout = findViewById(R.id.swipe_container)
        swipeRefreshLayout.setOnRefreshListener{
            GetNews.clear()
            loadData()
        }
        loadData()
        swipeRefreshLayout.isRefreshing = true
    }

    private fun loadData() {
        GetNews.getData {
            if (swipeRefreshLayout.isRefreshing) adapter.clear()
            swipeRefreshLayout.isRefreshing = false
            if (it == null) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            } else {
                adapter.add(it)
            }
        }
    }
}