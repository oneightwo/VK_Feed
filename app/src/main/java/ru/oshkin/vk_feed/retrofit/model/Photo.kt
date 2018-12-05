package ru.oshkin.vk_feed.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Photo(
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("sizes")
    @Expose
    val sizes: List<PhotoSize>
) {
    fun getOptimalPhoto() = sizes.filter { it.width < 900 }.sortedByDescending { it.width }[0]
    fun getMaxPhoto() = sizes.sortedByDescending { it.width }[0]
}