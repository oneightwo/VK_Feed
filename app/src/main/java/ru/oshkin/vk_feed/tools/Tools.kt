package ru.oshkin.vk_feed.tools

import android.Manifest
import android.app.Activity
import android.content.Context
import android.view.View
import okhttp3.ResponseBody
import java.io.*
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import ru.oshkin.vk_feed.R

fun methodRequiresPerm(activity: Activity): Boolean {
    if (EasyPermissions.hasPermissions(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        return true
    } else {
        requestWriteToFile(activity)
    }
    return false
}

private fun requestWriteToFile(activity: Activity) {
    EasyPermissions.requestPermissions(
        PermissionRequest.Builder(activity, 123, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .setRationale(activity.getString(R.string.permission_write))
            .setNegativeButtonText("Нет")
            .setPositiveButtonText("Да")
            .setTheme(R.style.Theme_AppCompat_DayNight_Dialog)
            .build()
    )
}

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun log(s: String) {
    Log.e("myApp", s)
}

fun View.isVisible() = visibility == View.VISIBLE

fun View.toggle() = setVisible(!isVisible())

fun setToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

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
                outputStream.write(fileReader, 0, read)
                fileSizeDownloaded += read.toLong()
            }

            outputStream.flush()

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