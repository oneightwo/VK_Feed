package ru.oshkin.vk_feed.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PhotoSize(
    @SerializedName("type")
    @Expose
    val type: String,
    @SerializedName("url")
    @Expose
    val url: String,
    @SerializedName("width")
    @Expose
    val width: Int,
    @SerializedName("height")
    @Expose
    val height: Int
)