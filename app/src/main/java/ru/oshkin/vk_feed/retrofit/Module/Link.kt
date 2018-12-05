package ru.oshkin.vk_feed.retrofit.Module

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class Link(
    @SerializedName("url")
    @Expose
    val url: String,
    @SerializedName("title")
    @Expose
    val title: String,
    @SerializedName("photo")
    @Expose
    val photo: Photo?
) {
    fun getPhotoLink() = photo?.getOptimalPhoto()
}