package ru.oshkin.vk_feed.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.WebView
import android.webkit.WebViewClient
import ru.oshkin.vk_feed.R
import ru.oshkin.vk_feed.UserData
import ru.oshkin.vk_feed.newsLine.NewsActivity
import java.net.URL
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private val data = UserData(this)
    private lateinit var webView: WebView


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        webView = findViewById(R.id.login_wv)

        if (data.getToken().isNotEmpty()) {
            startActivity(Intent(this, NewsActivity::class.java))
            finish()
        } else {
            val url = URL("https://oauth.vk.com/authorize?client_id=6734151&display=mobile&redirect_uri=https://oauth.vk.com/blank.html&scope=notify,friends,photos,pages,notes,messages,wall,offline,groups&response_type=token&v=5.87&state=123456")
            webView.loadUrl(url.toString())
            webView.webViewClient = ParsingWebClient()
        }
    }


    fun doneWithThis(url: String) {
        val token = extract(url, "access_token=(.*?)&")
        data.saveToken(token)

        CookieSyncManager.createInstance(webView.context).sync()
        val man = CookieManager.getInstance()
        man.removeAllCookies(null)
        man.flush()
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
                webView.visibility = View.GONE
                doneWithThis(url)
            }
        }
    }
}