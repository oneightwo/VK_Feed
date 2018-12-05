package ru.oshkin.vk_feed.retrofit.Module

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FeedResponse(
    @SerializedName("items")
    @Expose
    val items: List<WallPost>,
    @SerializedName("groups")
    @Expose
    val groups: List<InfoGroup>,
    @SerializedName("profiles")
    @Expose
    val profiles: List<InfoProfile>,
    @SerializedName("next_from")
    @Expose
    val nextFrom: String
)