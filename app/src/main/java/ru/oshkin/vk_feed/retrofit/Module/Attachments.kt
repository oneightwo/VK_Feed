package ru.oshkin.vk_feed.retrofit.Module

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Attachments(
    @SerializedName("type")
    @Expose
    val type: String,
    @SerializedName("photo")
    @Expose
    val photo: Photo?,
    @SerializedName("link")
    @Expose
    val link: Link?
)