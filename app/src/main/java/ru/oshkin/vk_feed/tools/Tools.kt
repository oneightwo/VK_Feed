package ru.oshkin.vk_feed.tools

import android.content.Context
import android.os.Environment
import android.view.View
import okhttp3.ResponseBody
import java.io.*
import android.net.ConnectivityManager




fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.isVisible() = visibility == View.VISIBLE

fun View.toggle() = setVisible(!isVisible())

fun writeResponseBodyToDisk(file: File, body: ResponseBody?): Boolean {
    try {

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            val fileReader = ByteArray(4096)

            val fileSize = body?.contentLength()
            var fileSizeDownloaded: Long = 0

            inputStream = body?.byteStream()
            outputStream = FileOutputStream(file)

            while (true) {
                val read = inputStream!!.read(fileReader)

                if (read == -1) {
                    break
                }

                outputStream!!.write(fileReader, 0, read)

                fileSizeDownloaded += read.toLong()

            }

            outputStream!!.flush()

            return true
        } catch (e: IOException) {
            return false
        } finally {
            if (inputStream != null) {
                inputStream.close()
            }

            if (outputStream != null) {
                outputStream.close()
            }
        }
    } catch (e: IOException) {
        return false
    }

}

fun getNameFile(url: String) = url.split("/").last()

fun isOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val netInfo = cm!!.activeNetworkInfo
    return netInfo != null && netInfo.isConnectedOrConnecting
}