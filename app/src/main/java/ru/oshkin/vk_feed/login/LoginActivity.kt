package ru.oshkin.vk_feed.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_login.*
import ru.oshkin.vk_feed.R
import ru.oshkin.vk_feed.tools.UserData
import ru.oshkin.vk_feed.newsLine.NewsActivity
import ru.oshkin.vk_feed.tools.isOnline
import ru.oshkin.vk_feed.tools.setToast
import ru.oshkin.vk_feed.tools.setVisible
import java.net.URL
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private val webView by lazy { findViewById<WebView>(R.id.login_wv) }

    companion object {
        private const val URL = "https://oauth.vk.com/authorize?client_id=6734151&display=mobile&redirect_uri=https://oauth.vk.com/blank.html&scope=notify,friends,photos,pages,notes,messages,wall,offline,groups&response_type=token&v=5.87&state=123456"
        private const val TOKEN_REGEX = "access_token=(.*?)&"
        private const val BLANK_HTML = "https://oauth.vk.com/blank.html"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        webView.settings.javaScriptEnabled = true
    }

    override fun onResume() {
        super.onResume()
        if (UserData.instance.getToken().isNotEmpty()) {
            startActivity(Intent(this, NewsActivity::class.java))
            finish()
        } else if (isOnline(this)){
            webView.loadUrl(URL)
            webView.webViewClient = ParsingWebClient()
        } else {
            startActivityForResult(Intent(android.provider.Settings.ACTION_SETTINGS), 0)
            setToast(this, getString(R.string.on_internet))
        }
    }

    fun doneWithThis(url: String) {
        val token = extract(url, TOKEN_REGEX)
        UserData.instance.saveToken(token)
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
            if (url.startsWith(BLANK_HTML)) {
                webView.setVisible(false)
                doneWithThis(url)
            }
        }
    }
}