package ru.oshkin.vk_feed.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class InfoProfile(
    @SerializedName("first_name")
    @Expose
    val firstName: String,
    @SerializedName("last_name")
    @Expose
    val lastName: String,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("photo_100")
    @Expose
    val photo: String
) : Author {
    override fun name() = "$firstName $lastName"
    override fun photo() = photo
}