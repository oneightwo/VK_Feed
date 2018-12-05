package ru.oshkin.vk_feed.retrofit.Module

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class InfoGroup(
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("photo_200")
    @Expose
    val photo: String
) : Author {
    override fun name() = name
    override fun photo() = photo
}