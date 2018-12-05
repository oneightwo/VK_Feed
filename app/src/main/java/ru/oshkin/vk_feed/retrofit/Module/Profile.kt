package ru.oshkin.vk_feed.retrofit.Module

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("first_name")
    @Expose
    val firstName: String,
    @SerializedName("last_name")
    @Expose
    val lastName: String,
    @SerializedName("photo_100")
    @Expose
    val photo: String
) {
    fun getName() = "$firstName $lastName"
}