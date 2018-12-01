package ru.oshkin.vk_feed.newsLine

import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.LayoutManager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import ru.oshkin.vk_feed.R
import ru.oshkin.vk_feed.retrofit.*
import ru.oshkin.vk_feed.tools.setVisible
import java.text.SimpleDateFormat
import java.util.*
import android.support.v7.widget.CardView
import android.widget.Toast
import java.lang.StringBuilder


class AdapterNews(
    private val activity: NewsActivity,
    private val loader: () -> Unit
) : RecyclerView.Adapter<AdapterNews.ViewHolder>() {

    private val posts = arrayListOf<WallPost>()
    private val groups = hashSetOf<InfoGroup>()
    private val profiles = hashSetOf<InfoProfile>()
    private val displayWidth by lazy {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val post = posts[position]
        val author = getAuthor(post.sourceId) ?: return
        val sdf = SimpleDateFormat("HH:mm dd.MM.yyyy")

        //initRecyclerViewImage(viewHolder, post)

        with(viewHolder) {
            textNews.setVisible(post.text.isNotEmpty())
            newsImage.setVisible(post.getPhotos().isNotEmpty())

            setAllText(viewHolder, post)
            nameGroup.text = author.name()
            timePost.text = sdf.format(Date(post.date * 1000L))

            Picasso.get().load(author.photo()).into(iconGroup)

            visibleView(viewHolder, post)
        }
    }

    private fun initRecyclerViewImage(viewHolder: ViewHolder, post: WallPost) {
        with(viewHolder) {
            recyclerViewImage.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewImage.adapter = AdapterNewsImage(post.getPhotos(), activity)
        }
    }


    private fun visibleView(viewHolder: ViewHolder, post: WallPost) {
        with(viewHolder) {
            when {
                post.getLink() != null && post.getPhotos().size == 1 -> {
                    val photoLink = post.getLink()!!.link?.getPhotoLink()
                    newsImage.visibility = View.VISIBLE
                    recyclerViewImage.visibility = View.GONE
                    newsImageLinkText.visibility = View.VISIBLE
                    newsImageLinkText.text = post.getLink()!!.link?.title
                    newsImageLinkText.layoutParams.width = displayWidth
                    val optimalPhoto = post.getPhotos()[0].getOptimalPhoto()
                    newsImageLinkText.layoutParams.height = getHeight(optimalPhoto)
                    Picasso.get().load(optimalPhoto.url).resize(displayWidth, getHeight(optimalPhoto)).into(newsImage)

                }
                post.getPhotos().size == 1 -> {
                    newsImage.visibility = View.VISIBLE
                    recyclerViewImage.visibility = View.GONE
                    newsImageLinkText.visibility = View.GONE
                    val optimalPhoto = post.getPhotos()[0].getOptimalPhoto()
                    Picasso.get().load(optimalPhoto.url).resize(displayWidth, getHeight(optimalPhoto)).into(newsImage)
                }
                post.getPhotos().isNotEmpty() -> {
                    recyclerViewImage.visibility = View.VISIBLE
                    newsImageLinkText.visibility = View.GONE
                    initRecyclerViewImage(viewHolder, post)
                    newsImage.visibility = View.GONE
                }
                post.getLink() != null -> {
                    val photoLink = post.getLink()!!.link?.getPhotoLink()
                    newsImage.visibility = View.VISIBLE
                    recyclerViewImage.visibility = View.GONE
                    newsImageLinkText.visibility = View.VISIBLE
                    newsImageLinkText.text = post.getLink()!!.link?.title
                    newsImageLinkText.layoutParams.width = displayWidth
                    if (photoLink != null) {
                        newsImageLinkText.layoutParams.height = getHeight(photoLink)
                        Picasso.get().load(photoLink.url).resize(displayWidth, getHeight(photoLink)).into(newsImage)
                    } else {
                        newsImage.setImageResource(R.drawable.blue)
                        val height = getHeightLink(newsImage.drawable)
                        // Log.e("d", "${d}")

                        newsImageLinkText.layoutParams.height = height
                        //Picasso.get().load(R.drawable.blue).resize(displayWidth, height).into(newsImage)

                    }
                }
                else -> {
                    newsImage.visibility = View.GONE
                    recyclerViewImage.visibility = View.GONE
                    newsImageLinkText.visibility = View.GONE

                }
            }
        }
    }

    private fun setAllText(viewHolder: ViewHolder, post: WallPost) {
        with(viewHolder) {
            val postText = post.text
            if (postText.length > 400) {
                textNewsAll.visibility = View.VISIBLE
                textNews.text = postText.substring(0, 400) + "..."
            } else {
                textNewsAll.visibility = View.GONE
                textNews.text = postText
            }

            textNewsAll.setOnClickListener {
                textNews.text = postText
                textNewsAll.visibility = View.GONE
            }
        }
    }


    private fun getHeight(photoSize: PhotoSize) =
        (photoSize.height * (displayWidth.toDouble() / photoSize.width)).toInt()

    private fun getHeightLink(d: Drawable) =
        (d.intrinsicHeight * (displayWidth.toDouble() / d.intrinsicWidth)).toInt()

    fun clear() {
        posts.clear()
        notifyDataSetChanged()
    }

    fun add(response: FeedResponse) {
        isLoading = false
        val before = itemCount
        posts.addAll(response.items.filter { !it.isEmpty() })
        groups.addAll(response.groups)
        profiles.addAll(response.profiles)
        notifyItemRangeInserted(before, response.items.size)
    }

    private fun getAuthor(id: Int): Author? = if (id > 0) {
        profiles.find { it.id == id }
    } else {
        groups.find { it.id == -id }
    }


    override fun onCreateViewHolder(viewHolder: ViewGroup, p1: Int): AdapterNews.ViewHolder {
        val itemView = LayoutInflater.from(viewHolder.context).inflate(R.layout.recycler_item, viewHolder, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = posts.size

    protected var layoutManager: LayoutManager? = null
    var isLoading: Boolean = false
        protected set

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        layoutManager = recyclerView.layoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0)
                    return
                val total = itemCount
                val last = lastVisiblePosition()
                if (!isLoading && last >= total - THRESHOLD) {
                    loader.invoke()
                    isLoading = true
                }
            }
        })
    }

    fun lastVisiblePosition() =
        if (layoutManager != null) {
            (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        } else {
            -1
        }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameGroup: TextView = itemView.findViewById(R.id.name_group_tv)
        val textNews: TextView = itemView.findViewById(R.id.news_tv)
        val newsImage: ImageView = itemView.findViewById(R.id.news_iv)
        val iconGroup: ImageView = itemView.findViewById(R.id.icon_group_iv)
        val textNewsAll: TextView = itemView.findViewById(R.id.news_all_tv)
        val timePost: TextView = itemView.findViewById(R.id.time_post_group_tv)
        val recyclerViewImage: RecyclerView = itemView.findViewById(R.id.recycler_view_image)
        val newsImageLinkText: TextView = itemView.findViewById(R.id.news_textLink)
        val shareImageView: ImageView = itemView.findViewById(R.id.share_iv)
        val itemCardView: CardView = itemView.findViewById(R.id.itemCardView)

        init {
            newsImage.setOnClickListener {
                val url = posts[adapterPosition].getPhotos()[0].getMaxPhoto().url
                ImageActivity.outputImage(activity, url)
            }
            newsImageLinkText.setOnClickListener {
                val url = posts[adapterPosition].getLink()?.link?.url
                if (url != null) {
                    WebNewsActivity.startActivity(activity, url)
                } else {
                    Toast.makeText(activity, "Ошибка ссылки", Toast.LENGTH_SHORT).show()
                }
            }
            shareImageView.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_TEXT,  getLinkShare(adapterPosition))
                activity.startActivity(Intent.createChooser(shareIntent, "Share link using"))
            }
        }

    }

    fun getLinkShare(adapterPosition: Int): String {
//        url = "https://vk.com/wall" + wallPost.sourceId + "_" + wallPost.postId
        val post = posts[adapterPosition]
        val url = StringBuilder()
        url.append("https://vk.com/wall", post.sourceId, "_", post.postId)
        return url.toString()
    }


    companion object {
        const val WEB = "web"
        var THRESHOLD = 5
    }
}