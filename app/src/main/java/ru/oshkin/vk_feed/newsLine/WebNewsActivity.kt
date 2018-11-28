package ru.oshkin.vk_feed.newsLine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient
import ru.oshkin.vk_feed.R

class WebNewsActivity: AppCompatActivity() {

    private val webNews by lazy { findViewById<WebView>(R.id.news_webView) }

    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_news)
        url = intent.extras.getString(AdapterNews.WEB) ?: return
        webNews.webViewClient = ExitWebClient()
        webNews.loadUrl(url)
    }

    private inner class ExitWebClient : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (url == null) return
//            if (!url.startsWith(this@WebNewsActivity.url)) {
//                webNews.visibility = View.GONE
//                finish()
//            }
        }
    }

    companion object {
        fun startActivity(context: Context, url: String) {
            val start = Intent(context, WebNewsActivity::class.java)
            start.putExtra(AdapterNews.WEB, url)
            context.startActivity(start)
        }
    }
}