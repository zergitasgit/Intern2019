package com.weather.forecastweather.views


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import com.weather.forecastweather.Ads
import com.weather.forecastweather.R
import com.weather.forecastweather.adapter.ViewPagerAdapter
import com.weather.forecastweather.databinding.ActivityMainBinding
import com.weather.forecastweather.models.City
import com.weather.forecastweather.viewmodels.MainViewModel
import com.weather.forecastweather.views.base.BaseActivity
import com.znitenda.A
import com.znitenda.ZAndroidSDK
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    var model: MainViewModel? = null
    var arrayList = ArrayList<City>()
    var viewPagerAdapter: ViewPagerAdapter? = null
    var binding: ActivityMainBinding? = null

    override fun bindingView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        model = MainViewModel(this, binding!!)
        binding!!.viewModel = model
        binding!!.executePendingBindings()
        initAds()
//
//

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        registerReceiver(broadcastReceiver, IntentFilter("SEARCH"))
        registerReceiver(brList, IntentFilter("SEARCH_LIST"))
        registerReceiver(brDelete, IntentFilter("DELETE"))
        registerReceiver(brTemp, IntentFilter("RELOAD"))
        registerReceiver(brBg, IntentFilter("BG"))
        ZAndroidSDK.init(this)
        A.f(this)
    }


    override fun onRestart() {
        super.onRestart()
        model!!.changeKey(false)

    }

    private fun initAds() {
        Ads.initBanner(this, rlads, object : Ads.OnAdsListener {
            override fun onError() {
                rlads.visibility = View.GONE
            }

            override fun onAdLoaded() {
                rlads.visibility = View.VISIBLE
            }

            override fun onAdOpened() {
                rlads.visibility = View.VISIBLE
            }

            override fun onAdClose() {
            }

        })
    }

    private var brList = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action
            val pos = p1?.extras!!.getInt("pos")
//
            if (action!!.equals("SEARCH_LIST", ignoreCase = true)) {
                model!!.moveHave(pos, true)

            }
//

        }
    }
    private var brDelete = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action


            if (action!!.equals("DELETE", ignoreCase = true)) {

                arrayList = model!!.getCity()
                viewPagerAdapter = ViewPagerAdapter(this@MainActivity, arrayList, true)
                binding!!.viewPager.adapter = viewPagerAdapter
                binding!!.viewPager.offscreenPageLimit = arrayList.size
                viewPagerAdapter!!.changeKey(false)
//                model!!.setLocal(arrayList, binding!!.viewPager.currentItem)
            }
        }
    }
    private var brTemp = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action

            val pos = p1?.extras?.getInt("position")
            if (action!!.equals("RELOAD", ignoreCase = true)) {
                model = MainViewModel(this@MainActivity, binding!!)
                binding!!.viewModel = model
                binding!!.executePendingBindings()

                arrayList = model!!.getCity()
                binding!!.viewPager.currentItem = pos!!
//

            }
        }
    }


    private var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action
            val lat = p1?.extras?.getDouble("lat")
            val lon = p1?.extras?.getDouble("lon")
            val pos = p1?.extras!!.getString("city")
            val have = p1.extras!!.getBoolean("have")

//            if (string!!.isEmpty() || string.length == 0) {
//
//            }

            if (action!!.equals("SEARCH", ignoreCase = true)) {
//

                val id = model!!.getIdCity(pos)
                model!!.moveHave(id - 1, have)


            }
        }

    }
    private var brBg = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action
//

            if (action!!.equals("BG", ignoreCase = true)) {
//
            }
        }

    }


    override fun onDestroy() {
        model!!.destroy()
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        unregisterReceiver(brList)
        unregisterReceiver(brDelete)
        unregisterReceiver(brTemp)
        unregisterReceiver(brBg)


    }


}
