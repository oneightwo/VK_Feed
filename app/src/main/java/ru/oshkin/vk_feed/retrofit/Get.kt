package ru.oshkin.vk_feed.retrofit

import android.content.Context
import android.os.Environment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.oshkin.vk_feed.retrofit.model.FeedResponse
import ru.oshkin.vk_feed.retrofit.model.Profile
import ru.oshkin.vk_feed.retrofit.model.RequestModel
import ru.oshkin.vk_feed.tools.UserData
import ru.oshkin.vk_feed.tools.getNameFile
import ru.oshkin.vk_feed.tools.writeResponseBodyToDisk
import java.io.File


object Get {

    const val COUNT = 100

    private val retrofit by lazy { Network.retrofit }
    private var startFrom = ""

    fun getFeed(isFeed: Boolean, callback: (FeedResponse?) -> Unit) {
        when (isFeed) {
            true -> retrofit.getFeed(COUNT, startFrom, UserData.instance.getToken())
            false -> retrofit.getRecommended(COUNT, UserData.instance.getToken())
        }.enqueue(object : Callback<RequestModel<FeedResponse>> {
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


    fun getProfile(callback: (Profile?) -> Unit) {
        retrofit.getInfoProfile(UserData.instance.getToken())
            .enqueue(object : Callback<RequestModel<List<Profile>>> {
                override fun onFailure(call: Call<RequestModel<List<Profile>>>, t: Throwable) {
                    t.printStackTrace()
                    callback(null)
                }

                override fun onResponse(
                    call: Call<RequestModel<List<Profile>>>,
                    response: Response<RequestModel<List<Profile>>>
                ) {
                    val profile = response.body()?.response
                    if (profile != null) {
                        callback(profile[0])
                    } else {
                        onFailure(call, Throwable())
                    }
                }
            })
    }

    fun saveImage(context: Context, isShare: Boolean, url: String, callback: (String?) -> Unit) {
        retrofit.downloadFileWithDynamicUrlSync(url)
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    callback.invoke(null)
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val namePicture = getNameFile(url)
                    val file = File(
                        if (isShare) {
                            context.externalCacheDir
                        } else {
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        }, namePicture
                    )
                    if (response.isSuccessful && writeResponseBodyToDisk(file, response.body())) {
                        callback.invoke(file.absolutePath)

                    } else {
                        callback.invoke(null)
                    }
                }
            })
    }

    fun clear() {
        startFrom = ""
    }
}