package ru.oshkin.vk_feed.retrofit

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.oshkin.vk_feed.UserData

object Get {

    const val COUNT = 100

    private val retrofit by lazy { Network.retrofit }
    private var startFrom = ""

    fun getFeed(isFeed: Boolean, callback: (FeedResponse?) -> Unit) {
        when(isFeed) {
            true -> retrofit.getFeed(COUNT, startFrom, UserData.instance.getToken())
            false -> retrofit.getRecommended(COUNT, UserData.instance.getToken())
        }
            .enqueue(object : Callback<RequestModel<FeedResponse>> {
                override fun onFailure(call: Call<RequestModel<FeedResponse>>, t: Throwable) {
                    t.printStackTrace()
                    callback.invoke(null)
                }

                override fun onResponse(
                    call: Call<RequestModel<FeedResponse>>,
                    response: Response<RequestModel<FeedResponse>>
                ) {
                    val feedResponse = response.body()?.response
                    if (feedResponse != null) {
                        startFrom = feedResponse.nextFrom
                        callback.invoke(feedResponse)
                    } else {
                        onFailure(call, Throwable())
                    }
                }
            })
    }


    fun getProfile(callback: (List<Profile>?) -> Unit){
        retrofit.getInfoProfile(UserData.instance.getToken())
            .enqueue(object : Callback<RequestModel<List<Profile>>> {
                override fun onFailure(call: Call<RequestModel<List<Profile>>>, t: Throwable) {
                    t.printStackTrace()
                    callback(null)
                }

                override fun onResponse(call: Call<RequestModel<List<Profile>>>, response: Response<RequestModel<List<Profile>>>) {
                    val profile = response.body()?.response
                    if (profile != null) {
                        callback(profile)
                    } else {
                        onFailure(call, Throwable())
                    }
                }
            })
    }

    fun clear() {
        startFrom = ""
    }
}