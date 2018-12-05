package ru.oshkin.vk_feed.retrofit.Module

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WallPost(
    @SerializedName("date")
    @Expose
    val date: Int,
    @SerializedName("source_id")
    @Expose
    val sourceId: Int,
    @SerializedName("post_id")
    @Expose
    val postId: Int,
    @SerializedName("text")
    @Expose
    val text: String,
    @SerializedName("attachments")
    @Expose
    val attachments: List<Attachments>?,
    @SerializedName("copy_history")
    @Expose
    val copyHistory: List<WallPost>
) {
    fun getPhotos() = attachments?.filter { it.photo != null }?.map { it.photo!! } ?: arrayListOf()

    fun isEmpty() = getPhotos().isEmpty() && text.isEmpty() && getLink() == null
            || text.isNotEmpty() && getPhotos().isEmpty() && getLink() == null && (attachments?.isNotEmpty() ?: false)


    fun getLink()= attachments?.find { it.link != null }
}
