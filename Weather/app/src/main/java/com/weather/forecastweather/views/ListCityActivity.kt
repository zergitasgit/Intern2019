package com.weather.forecastweather.views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.weather.forecastweather.Ads
import com.weather.forecastweather.R
import kotlinx.android.synthetic.main.activity_list_city.*

class ListCityActivity : AppCompatActivity() {
    val fragment = ManagerCityFragment()
//
//

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_city)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()

        initAds()
    }

    override fun onBackPressed() {
        if (getFragmentManager().backStackEntryCount > 0) {
            getFragmentManager().popBackStack()
        } else {
            super.onBackPressed()
            fragment.onBack()
        }

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
}
