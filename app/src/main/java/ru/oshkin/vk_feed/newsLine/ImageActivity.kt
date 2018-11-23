package ru.oshkin.vk_feed.newsLine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.github.chrisbanes.photoview.PhotoView
import com.squareup.picasso.Picasso
import ru.oshkin.vk_feed.R
import ru.oshkin.vk_feed.tools.toggle

class ImageActivity: AppCompatActivity() {

    private val imageView by lazy { findViewById<PhotoView>(R.id.photo_view) }
    private val saveImage by lazy { findViewById<ImageView>(R.id.saveImage) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val url = intent.extras.getString(AdapterNewsImage.KEY)
        if (url == null) {
            finish()
            return
        }

        Picasso.get().load(url).into(imageView)
        imageView.setOnPhotoTapListener { _, _, _ ->
            saveImage.toggle()
        }

        imageView.setOnSingleFlingListener { e1, e2, velocityX, velocityY ->
            if (Math.abs(e2.y - e1.y) > FLING_SIZE) {
                finish()
                return@setOnSingleFlingListener true
            }
            return@setOnSingleFlingListener false
        }
    }

    companion object {
        fun outputImage(context: Context, url: String) {
            val start = Intent(context, ImageActivity::class.java)
            start.putExtra(AdapterNewsImage.KEY, url)
            context.startActivity(start)
        }

        const val FLING_SIZE = 200
    }

}