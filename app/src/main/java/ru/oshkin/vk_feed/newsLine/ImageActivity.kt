package ru.oshkin.vk_feed.newsLine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.chrisbanes.photoview.PhotoView
import com.squareup.picasso.Picasso
import ru.oshkin.vk_feed.R

class ImageActivity: AppCompatActivity() {

    private val imageView by lazy { findViewById<PhotoView>(R.id.photo_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val url = intent.extras.getString(AdapterNewsImage.KEY)
        if (url == null) {
            finish()
            return
        }

        Picasso.get().load(url).into(imageView)
    }

    companion object {
        fun outputImage(context: Context, url: String) {
            val start = Intent(context, ImageActivity::class.java)
            start.putExtra(AdapterNewsImage.KEY, url)
            context.startActivity(start)
        }
    }
}