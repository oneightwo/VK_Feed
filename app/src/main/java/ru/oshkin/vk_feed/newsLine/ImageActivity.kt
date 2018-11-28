package ru.oshkin.vk_feed.newsLine

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import com.github.chrisbanes.photoview.PhotoView
import com.squareup.picasso.Picasso
import ru.oshkin.vk_feed.R
import ru.oshkin.vk_feed.retrofit.Get
import ru.oshkin.vk_feed.tools.toggle

class ImageActivity : AppCompatActivity() {

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

        saveImage.setOnClickListener {
            Get.saveImage(url) {
                if (it != null) {
                    Toast.makeText(this, "SAVE", Toast.LENGTH_SHORT).show()
                    addToGallery(this, url)
                } else {
                    Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
                }

            }
        }


        imageView.setOnSingleFlingListener { e1, e2, velocityX, velocityY ->
            if (Math.abs(e2.y - e1.y) > FLING_SIZE) {
                finish()
                return@setOnSingleFlingListener true
            }
            return@setOnSingleFlingListener false
        }
    }

    fun addToGallery(context: Context, path: String) {
        try {
            val cv = ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, context.getString(R.string.app_name))
                put(MediaStore.Images.Media.DESCRIPTION, context.getString(R.string.app_name))
                put(MediaStore.Images.Media.MIME_TYPE, "image/*")
                put(MediaStore.Images.Media.DATA, path)
            }
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
            context.contentResolver.notifyChange(Uri.parse("file://" + path), null)
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(this, "ERROR ADD TO GALLERY", Toast.LENGTH_SHORT).show()
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