package ru.oshkin.vk_feed.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("newsfeed.get?filters=post&v=${Network.VERSION}")
    fun getFeed(
        @Query("count") count: Int,
        @Query("start_from") startFrom: String,
        @Query("access_token") accessToken: String
    ): Call<RequestModel<FeedResponse>>
}