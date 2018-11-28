package ru.oshkin.vk_feed.tools

import android.os.Environment
import android.view.View
import okhttp3.ResponseBody
import java.io.*


fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.isVisible() = visibility == View.VISIBLE

fun View.toggle() = setVisible(!isVisible())

fun writeResponseBodyToDisk(fileName: String, body: ResponseBody?): Boolean {
    try {
        val futureStudioIconFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName)

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            val fileReader = ByteArray(4096)

            val fileSize = body?.contentLength()
            var fileSizeDownloaded: Long = 0

            inputStream = body?.byteStream()
            outputStream = FileOutputStream(futureStudioIconFile)

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