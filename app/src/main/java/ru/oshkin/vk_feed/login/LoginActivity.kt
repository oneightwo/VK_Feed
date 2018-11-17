package ru.oshkin.vk_feed.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import ru.oshkin.vk_feed.R
import ru.oshkin.vk_feed.UserData
import ru.oshkin.vk_feed.newsLine.NewsActivity
import java.net.URL
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private val data = UserData(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        if (data.checkData()) {
            startActivity(Intent(this, NewsActivity::class.java))
            finish()
        } else {
            val url = URL("https://oauth.vk.com/authorize?client_id=6734151&display=mobile&redirect_uri=https://oauth.vk.com/blank.html&scope=notify,friends,photos,pages,notes,messages,wall,offline,groups&response_type=token&v=5.87&state=123456")

            val login: WebView = findViewById(R.id.login_wv)
            var t: Toast
            login.webViewClient = ParsingWebClient()
            login.loadUrl(url.toString())
            try {
                val jsonStr = URL(url.toString()).openStream()
                t = Toast.makeText(applicationContext, jsonStr.read(), Toast.LENGTH_SHORT)
                t.show()
            } catch (e: Exception) {
                t = Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG)
                t.show()
            }
        }
    }


    fun doneWithThis(url: String) {
        val token = extract(url, "access_token=(.*?)&")
        val uid: Int
        try {
            uid = extract(url, "user_id=(\\d*)").toInt()
        } catch (e: Exception) {
            return
        }
        Toast.makeText(this, "$uid $token", Toast.LENGTH_SHORT).show()
        data.saveData(token)
        startActivity(Intent(this, NewsActivity::class.java))
        finish()
    }

    private fun extract(from: String, regex: String): String {
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(from)
        if (!matcher.find()) {
            return ""
        }
        return matcher.toMatchResult().group(1)
    }


    private inner class ParsingWebClient : WebViewClient() {

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (url.startsWith("https://oauth.vk.com/blank.html")) {
                doneWithThis(url)
            }
        }
    }
}