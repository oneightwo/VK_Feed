package ru.oshkin.vk_feed.retrofit

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.oshkin.vk_feed.UserData

object GetNews {

    const val COUNT = 100

    private val retrofit by lazy { Network.retrofit }
    private var startFrom = ""

    fun getData(callback: (FeedResponse?) -> Unit) {
        retrofit.getFeed(COUNT, startFrom, UserData.instance.getToken())
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

    fun clear() {
        startFrom = ""
    }
}