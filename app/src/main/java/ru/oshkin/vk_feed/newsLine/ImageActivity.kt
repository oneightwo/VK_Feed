package ru.oshkin.vk_feed.newsLine

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import com.github.chrisbanes.photoview.PhotoView
import com.squareup.picasso.Picasso
import ru.oshkin.vk_feed.R
import ru.oshkin.vk_feed.retrofit.Get
import ru.oshkin.vk_feed.tools.toggle
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import android.widget.LinearLayout
import android.os.StrictMode
import android.os.Build




class ImageActivity : AppCompatActivity() {

    private val imageView by lazy { findViewById<PhotoView>(R.id.photo_view) }
    private val saveImage by lazy { findViewById<ImageView>(R.id.saveImage) }
    private val shareImage by lazy { findViewById<ImageView>(R.id.share_in_image_iv) }
    private val linerLayout by lazy { findViewById<LinearLayout>(R.id.share_save_ll) }

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
            linerLayout.toggle()
        }

        saveImage.setOnClickListener {
            Get.saveImage(url) {
                if (it != null) {
                    Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show()
                    addToGallery(this, it)
                } else {
                    Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
                }

            }
        }
        
        shareImage.setOnClickListener {
            //var uri: Uri = 
            Get.saveImage(url) {
               val uri = Uri.parse("file://" + it)
                Log.e("one", uri.toString())
                imageShare(uri)
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

    private fun imageShare(uri: Uri) {
        val shareIntent: Intent = Intent().apply {
            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                    m.invoke(null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "image/jpeg"
        }
        startActivity(Intent.createChooser(shareIntent, "Калтинка"))
    }

    private fun addToGallery(context: Context, path: String) {
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