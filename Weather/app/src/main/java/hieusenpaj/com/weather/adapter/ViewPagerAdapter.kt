package hieusenpaj.com.weather.adapter

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import hieusenpaj.com.weather.databinding.ItemViewPagerBinding
import hieusenpaj.com.weather.helper.Helper
import hieusenpaj.com.weather.models.City
import hieusenpaj.com.weather.viewmodels.ForecastViewModel
import hieusenpaj.com.weather.viewmodels.WeatherViewModel

class ViewPagerAdapter(val activity: Activity, val arr: ArrayList<City>) : PagerAdapter() {
    var weatherViewModel: WeatherViewModel? = null
    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun getCount(): Int = arr.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding: ItemViewPagerBinding = ItemViewPagerBinding.inflate(LayoutInflater.from(activity), container, false)
        weatherViewModel = WeatherViewModel(activity, binding, arr, arr[position].lat, arr[position].lon, position)
        binding.setViewModel(weatherViewModel)
        binding.executePendingBindings()
        binding.rl.layoutParams.height = Helper.getHeightScreen(activity)
        container.addView(binding.root)

        return binding.root

    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as CoordinatorLayout)

    }

    fun getModel(): WeatherViewModel {
        return weatherViewModel!!
    }

    fun changeTemp() {
        weatherViewModel!!.changeTemp()
    }

    fun scroll() {

    }


}