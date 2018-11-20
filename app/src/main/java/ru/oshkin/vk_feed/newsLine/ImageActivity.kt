package ru.oshkin.vk_feed.newsLine

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.squareup.picasso.Picasso
import ru.oshkin.vk_feed.R

class ImageActivity: AppCompatActivity() {

    val imageView by lazy { findViewById<TouchImageView>(R.id.customImageVIew1) }

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
}