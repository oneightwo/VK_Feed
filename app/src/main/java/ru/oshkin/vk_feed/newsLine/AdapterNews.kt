package ru.oshkin.vk_feed.newsLine

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import ru.oshkin.vk_feed.R
import ru.oshkin.vk_feed.retrofit.*
import ru.oshkin.vk_feed.tool.setVisible
import java.text.SimpleDateFormat
import java.util.*

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
        val sdf = SimpleDateFormat("hh:mm dd.MM.yyyy")

        with(viewHolder) {

            textNews.setVisible(post.text.isNotEmpty())
            newsImage.setVisible(post.getPhotos().isNotEmpty())

            setAllText(viewHolder, post)
            nameGroup.text = author.name()
            timePost.text = sdf.format(Date(post.date * 1000L))

            Picasso.get().load(author.photo()).into(iconGroup)

            visibleImageView(viewHolder, post)
        }
    }

    private fun visibleImageView(viewHolder: ViewHolder, post: WallPost) {
        with(viewHolder) {
            if (post.attachments != null && post.attachments[0].photo != null) {
                newsImage.visibility = View.VISIBLE
                val optimalPhoto = post.attachments[0].photo!!.getOptimalPhoto()
                Picasso.get().load(optimalPhoto.url).resize(displayWidth, getHeight(optimalPhoto)).into(newsImage)
            } else {
                newsImage.visibility = View.GONE
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

    protected var layoutManager: RecyclerView.LayoutManager? = null
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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameGroup: TextView = itemView.findViewById(R.id.name_group_tv)
        var textNews: TextView = itemView.findViewById(R.id.news_tv)
        var newsImage: ImageView = itemView.findViewById(R.id.news_iv)
        var iconGroup: ImageView = itemView.findViewById(R.id.icon_group_iv)
        var textNewsAll: TextView = itemView.findViewById(R.id.news_all_tv)
        var timePost: TextView = itemView.findViewById(R.id.time_post_group_tv)

    }

    companion object {
        var THRESHOLD = 5
    }
}