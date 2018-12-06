package ru.oshkin.vk_feed.feed


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import ru.oshkin.vk_feed.R
import ru.oshkin.vk_feed.login.LoginActivity
import ru.oshkin.vk_feed.retrofit.Get
import ru.oshkin.vk_feed.retrofit.model.Profile
import ru.oshkin.vk_feed.tools.*


class NewsActivity : AppCompatActivity() {

    private val adapter by lazy { AdapterNews(this, ::loadData) }
    private val cacheManager by lazy { CacheManager(this) }
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var toolbar: Toolbar
    private lateinit var drawer: DrawerLayout
    private lateinit var drawerNV: NavigationView
    private lateinit var recyclerView: RecyclerView
    private var isFeed: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        Get.clear()
        initRecyclerView()
        initToolbar()
        initSwipeRefresh()
        getInfoProfile()
    }

    private fun getInfoProfile() {
        Get.getProfile {
            if (!isDestroyed) {
                if (it != null) {
                    setInfoProfile(it)
                    cacheManager.saveProfile(it)
                } else {
                    setInfoProfile(cacheManager.getProfile())
                }
            }
        }
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.feed)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout)
        drawerNV = findViewById(R.id.nvView)
        setupDrawerContent(drawerNV)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    private fun initSwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.swipe_container)
        swipeRefreshLayout.setOnRefreshListener {
            Get.clear()
            loadData()
        }
        loadData()
        swipeRefreshLayout.isRefreshing = true
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            selectDrawerItem(menuItem)
            true
        }
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.newsNav -> {
                if (!isFeed) {
                    isFeed = true
                    toolbar.title = getString(R.string.feed)
                    swipeRefreshLayout.isRefreshing = true
                    loadData()
                }
            }
            R.id.newsRecommendationsNav -> {
                if (isFeed) {
                    isFeed = false
                    toolbar.title = getString(R.string.recommendation)
                    swipeRefreshLayout.isRefreshing = true
                    loadData()
                }
            }
            R.id.profileOutNav -> {
                exitApp()
            }
        }
        recyclerView.layoutManager?.scrollToPosition(0)
        menuItem.isChecked = true
        title = menuItem.title
        drawer.closeDrawers()
    }

    private fun exitApp() {
        UserData.instance.deleteToken()
        cacheManager.deleteData()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem?) =
        when (item?.itemId) {
            android.R.id.home -> {
                drawer.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setInfoProfile(profile: Profile) {
        val name = profile.getName()
        val nameProfile = drawerNV.getHeaderView(0).findViewById<TextView>(R.id.nameProfileNav)
        val iconProfile = drawerNV.getHeaderView(0).findViewById<ImageView>(R.id.iconProfileNav)
        val backGround = drawerNV.getHeaderView(0).findViewById<ImageView>(R.id.imageNav)
        val photoProfile = profile.photo
        Picasso.get().load(photoProfile).into(iconProfile)
        Picasso.get().load(photoProfile).transform(BlurTranformation(this)).into(backGround)
        nameProfile.text = name
    }

    private fun loadData() {
        Get.getFeed(isFeed) {
            if (!isDestroyed) {
                if (swipeRefreshLayout.isRefreshing) adapter.clear()
                swipeRefreshLayout.isRefreshing = false
                if (it == null) {
                    val data = cacheManager.getFeed(isFeed)
                    if (data != null) {
                        adapter.add(data)
                        setToast(this, getString(R.string.error_download))
                    } else {
                        setToast(this, getString(R.string.error_cache))
                    }
                } else {
                    cacheManager.saveFeed(it, isFeed)
                    adapter.add(it)
                }
            }
        }
    }
}