package ru.oshkin.vk_feed.retrofit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RequestModel<T>(
    val response: T
)

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
    fun getPhotos() = attachments?.filter { it.photo != null } ?: arrayListOf()
    fun isEmpty() = getPhotos().isEmpty() && text.isEmpty()
}

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

data class Link(
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("owner_id")
    @Expose
    val ownerId: Int
)

interface Author {
    fun name(): String

    fun photo(): String
}