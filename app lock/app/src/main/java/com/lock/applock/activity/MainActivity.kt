package com.lock.applock.activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.lock.applock.R
import com.lock.applock.adapter.TabAdapter
import com.reader.pdfreader.fragment.DislayAppFragment
import com.reader.pdfreader.fragment.AppLockedFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var arrFragment = ArrayList<Fragment>()
    var arrIcon = ArrayList<String>()
    var tabAdapter: TabAdapter? = null
    var sharedPreferences: SharedPreferences? = null
    var edit: SharedPreferences.Editor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpViewPager()
        setTabView()
    }
    private fun setUpViewPager() {
        setSupportActionBar(toolbar)
        tabAdapter = TabAdapter(this, arrFragment, arrIcon, supportFragmentManager)
        tabAdapter!!.addViewFragment(
            DislayAppFragment(),
            "App"
        )
        tabAdapter!!.addViewFragment(
            AppLockedFragment(),
            "App locked"
        )

        viewpager.offscreenPageLimit = 2
        viewpager.adapter = tabAdapter

    }

    private fun setTabView() {
        sliding_tabs.setupWithViewPager(viewpager)
        for (i in 0 until sliding_tabs.tabCount) {
            sliding_tabs.getTabAt(i)!!.customView = tabAdapter?.getTabView(i)
        }

    }


}
