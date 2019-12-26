package com.reader.pdfreader.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.document.pdfviewer.fragment.DislayPDFFragment
import com.document.pdfviewer.fragment.FavoriteFragment
import com.document.pdfviewer.fragment.RecentlyFragment
import com.reader.pdfreader.R
import com.reader.pdfreader.adapter.TabAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var arrFragment = ArrayList<Fragment>()
    var arrIcon = ArrayList<Int>()
    var pos: Int? = null
    var tabAdapter: TabAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handlePermission()
        setUpToolBar()
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
                    btn_perm.visibility = View.GONE
                    setUpViewPager()
                    setTabView()


                }else{
                    btn_perm.visibility = View.VISIBLE
                    rl_per.visibility = View.VISIBLE
                    btn_perm.setOnClickListener {
                        handlePermission()
                    }
                }

                return
            }
            else -> return
        }
    }

    fun setUpToolBar() {
        setSupportActionBar(toolbar)
        toolbar.title = "PDF Reader"
    }
    fun setUpViewPager() {

        tabAdapter = TabAdapter(this, arrFragment, arrIcon, supportFragmentManager)
        tabAdapter!!.addViewFragment(DislayPDFFragment(),
            R.drawable.tab_pdf
        )
        tabAdapter!!.addViewFragment(RecentlyFragment(),
            R.drawable.tab_history
        )
        tabAdapter!!.addViewFragment(FavoriteFragment(),
            R.drawable.tab_favorite
        )
        viewpager.offscreenPageLimit = 3
        viewpager.adapter = tabAdapter

    }

    fun setTabView() {
        sliding_tabs.setupWithViewPager(viewpager)
        for (i in 0 until sliding_tabs.tabCount) {
            sliding_tabs.getTabAt(i)!!.customView = tabAdapter?.getTabView(i)
        }

    }
}
