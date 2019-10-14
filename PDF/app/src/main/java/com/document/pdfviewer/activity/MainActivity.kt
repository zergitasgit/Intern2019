package com.document.pdfviewer.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.WindowManager
import android.widget.SearchView
import com.document.pdfviewer.R
import com.document.pdfviewer.adapter.TabAdapter
import com.document.pdfviewer.fragment.DislayPDFFragment
import com.document.pdfviewer.fragment.FavoriteFragment
import com.document.pdfviewer.fragment.RecentlyFragment
import kotlinx.android.synthetic.main.activity_main.*
import android.content.IntentFilter
import android.view.View
import android.widget.RelativeLayout
import com.document.pdfviewer.Ads
import com.document.pdfviewer.dialog.RateDialog
import com.document.pdfviewer.utils.Utils
import com.znitenda.A
import com.znitenda.ZAndroidSDK


class MainActivity : AppCompatActivity() {
    var arrFragment = ArrayList<Fragment>()
    var arrIcon = ArrayList<Int>()
    var pos: Int? = null
    var tabAdapter: TabAdapter? = null
    private lateinit var rateDialog: RateDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBgStatusbar()
        setContentView(R.layout.activity_main)
        handlePermission()
        registerReceiver(brHistory, IntentFilter("HISTORY"))
        setUpToolBar()

        A.f(this)
        ZAndroidSDK.init(this)
initAds()
        rateDialog = RateDialog(this, object : RateDialog.OnClickDialog {
            override fun onCancel() {
                finish()
            }

            override fun onRate() {
                Utils.rateApp(this@MainActivity)
            }
        })
    }

    override fun onBackPressed() {
        rateDialog.show()
    }

    /**
     * init ads
     */
    private fun initAds() {
        val rl = findViewById<RelativeLayout>(R.id.ads_rl)
        Ads.initBanner(this, rl, object : Ads.OnAdsListener {
            override fun onAdClose() {

            }

            override fun onError() {
                rl.visibility = View.GONE
            }

            override fun onAdLoaded() {
                rl.visibility = View.VISIBLE

            }

            override fun onAdOpened() {
                rl.visibility = View.VISIBLE

            }
        })
    }


    private fun setBgStatusbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            val background = resources.getDrawable(R.drawable.gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(android.R.color.transparent))
            window.setNavigationBarColor(resources.getColor(android.R.color.transparent))
            window.setBackgroundDrawable(background)
        }
    }


    private fun handlePermission() {
        val perms = arrayOf("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"
        )
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(perms, 3)
        } else {
            setUpViewPager()
            setTabView()
        }

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            3/*200*/ -> {
                if (grantResults[0] == 0) {
                    setUpViewPager()
                    setTabView()

                }

                return
            }
            else -> return
        }
    }

    fun setUpToolBar() {
        setSupportActionBar(toolbar)
        toolbar.setTitle("PDF")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchViewItem = menu!!.findItem(R.id.action_search)

        //getting the search view
        val searchView = searchViewItem.actionView as SearchView

        //making the searchview consume all the toolbar when open
        searchView.maxWidth = Int.MAX_VALUE

        searchView.queryHint = "Search"

//        searchViewItem?.expandActionView()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
//                    searchViewItem.collapseActionView()
//                invalidateOptionsMenu()

//                hideKeyboard()
                val intent = Intent("SEARCH")
                intent.putExtra("string", p0)
                sendBroadcast(intent)

                return false
            }

        })






        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
//        setUpSearch()
    }

    fun setUpViewPager() {

        tabAdapter = TabAdapter(this, arrFragment, arrIcon, supportFragmentManager)
        tabAdapter!!.addViewFragment(DislayPDFFragment(), R.drawable.tab_pdf)
        tabAdapter!!.addViewFragment(RecentlyFragment(), R.drawable.tab_history)
        tabAdapter!!.addViewFragment(FavoriteFragment(), R.drawable.tab_favorite)
        viewpager.offscreenPageLimit = 3
        viewpager.adapter = tabAdapter

    }

    fun setTabView() {
        sliding_tabs.setupWithViewPager(viewpager)
        for (i in 0 until sliding_tabs.getTabCount()) {
            sliding_tabs.getTabAt(i)!!.setCustomView(tabAdapter?.getTabView(i))
        }

    }

    var brHistory = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action
            if (action!!.equals("HISTORY", ignoreCase = true)) {
//                arr.clear()

                hideKeyboard()
//                Toast.makeText(this@MainActivity,"hi",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun hideKeyboard() {
//        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        invalidateOptionsMenu()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(brHistory)
    }
}
