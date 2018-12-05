package ru.oshkin.vk_feed.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import ru.oshkin.vk_feed.retrofit.model.FeedResponse
import ru.oshkin.vk_feed.retrofit.model.Profile
import ru.oshkin.vk_feed.retrofit.model.RequestModel


interface ApiService {

    @GET("newsfeed.get?filters=post&v=${Network.VERSION}")
    fun getFeed(
        @Query("count") count: Int,
        @Query("start_from") startFrom: String,
        @Query("access_token") accessToken: String
    ): Call<RequestModel<FeedResponse>>

    @GET("newsfeed.getRecommended?v=${Network.VERSION}")
    fun getRecommended(
        @Query("count") count: Int,
//        @Query("start_from") startFrom: String,
        @Query("access_token") accessToken: String
    ): Call<RequestModel<FeedResponse>>

    @GET("users.get?fields=photo_100&v=${Network.VERSION}")
    fun getInfoProfile(
        @Query("access_token") accessToken: String
    ): Call<RequestModel<List<Profile>>>

    @GET
    fun downloadFileWithDynamicUrlSync(@Url fileUrl: String): Call<ResponseBody>
}