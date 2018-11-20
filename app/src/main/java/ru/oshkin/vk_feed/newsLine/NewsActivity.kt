package ru.oshkin.vk_feed.newsLine
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
import android.widget.Toast
import com.squareup.picasso.Picasso
import ru.oshkin.vk_feed.R
import ru.oshkin.vk_feed.UserData
import ru.oshkin.vk_feed.login.LoginActivity
import ru.oshkin.vk_feed.retrofit.Get
import ru.oshkin.vk_feed.retrofit.Profile
import ru.oshkin.vk_feed.tools.BlurTranformation


class NewsActivity : AppCompatActivity() {

    private val adapter by lazy { AdapterNews(this, ::loadData) }
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var toolbar: Toolbar
    private lateinit var drawer: DrawerLayout
    private lateinit var drawerNV: NavigationView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        initRecyclerView()
        initToolbar()
        initSwipeRefresh()
        getInfoProfile()

    }

    private fun getInfoProfile() {
        Get.getProfile {
            if (it != null) {
                setInfoProfile(it)
            } else {
                Log.e("NAME", "Error")
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
                Log.e("menu", "news")
            }
            R.id.newsRecommendationsNav -> Log.e("menu", "newsRecommendationsNav")
            R.id.profileOutNav -> {
                Log.e("menu", "profileOutNav")
                exitApp()
            }
        }
        menuItem.isChecked = true
        title = menuItem.title
        drawer.closeDrawers()
    }


    private fun exitApp() {
        UserData.instance.deleteToken()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    //// Действие home/up action bar'а должно открывать или закрывать drawer
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

    private fun setInfoProfile(profileList: List<Profile>) {
        val profile = profileList[profileList.size - 1]
        val name = profile.getName()
        val nameProfile = drawerNV.getHeaderView(0).findViewById<TextView>(R.id.nameProfileNav)
        val iconProfile = drawerNV.getHeaderView(0).findViewById<ImageView>(R.id.iconProfileNav)
        val backGround = drawerNV.getHeaderView(0).findViewById<ImageView>(R.id.imageNav)
        nameProfile.text = name
        Picasso.get().load(profile.photo).into(iconProfile)
        Picasso.get().load(profile.photo).transform(BlurTranformation(this)).into(backGround)

    }

    private fun loadData() {
        Get.getData {
            if (swipeRefreshLayout.isRefreshing) adapter.clear()
            swipeRefreshLayout.isRefreshing = false
            if (it == null) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            } else {
                adapter.add(it)
            }
        }
    }
}