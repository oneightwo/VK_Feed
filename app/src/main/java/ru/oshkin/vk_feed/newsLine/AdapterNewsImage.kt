package ru.oshkin.vk_feed.newsLine

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import ru.oshkin.vk_feed.R
import ru.oshkin.vk_feed.retrofit.model.Photo
import ru.oshkin.vk_feed.retrofit.model.PhotoSize
import ru.oshkin.vk_feed.tools.setVisible

class AdapterNewsImage(photo: List<Photo>, private val activity: NewsActivity) :
    RecyclerView.Adapter<AdapterNewsImage.ImageViewHolder>() {

    private val photo = ArrayList(photo)
    private val cardHeight =
        with(activity.resources) { getDimensionPixelSize(R.dimen.recycler_view_height) - getDimensionPixelSize(R.dimen.card_margin) }

    override fun onCreateViewHolder(viewHolder: ViewGroup, p1: Int): ImageViewHolder {
        val itemView = LayoutInflater.from(viewHolder.context).inflate(R.layout.recycler_item_image, viewHolder, false)
        return ImageViewHolder(itemView)
    }

    override fun getItemCount() = photo.size

    override fun onBindViewHolder(imageViewHolder: ImageViewHolder, position: Int) {
        val optimalPhoto = photo[position].getOptimalPhoto()
        val width = getWidth(optimalPhoto)
        with(imageViewHolder) {
            cardView.layoutParams.width = width
            if (position == 0) {
                imageViewLibrary.setVisible(true)
            } else {
                imageViewLibrary.setVisible(false)
            }
            Picasso.get().load(optimalPhoto.url).resize(width, cardHeight).into(imageViewNews)
        }
    }

    private fun getWidth(photoSize: PhotoSize) =
        (photoSize.width.toDouble() / photoSize.height * cardHeight).toInt()


    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewLibrary: ImageView = view.findViewById(R.id.library_image_iv)
        val imageViewNews: ImageView = view.findViewById(R.id.imageNews_cv)
        val cardView: CardView = view.findViewById(R.id.imageCardView)

        init {
            imageViewNews.setOnClickListener {
                val url = photo[adapterPosition].getMaxPhoto().url
                ImageActivity.outputImage(activity, url)
            }
        }
    }

    companion object {
        const val KEY = "image"
    }

}