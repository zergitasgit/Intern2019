package com.weather.forecastweather.adapter

import android.app.Activity
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weather.forecastweather.databinding.ItemViewPagerBinding
import com.weather.forecastweather.helper.Helper
import com.weather.forecastweather.models.City
import com.weather.forecastweather.viewmodels.WeatherViewModel

class ViewPagerAdapter(val activity: Activity, val arr: ArrayList<City>, val search:Boolean) : PagerAdapter() {
    var weatherViewModel: WeatherViewModel? = null
    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun getCount(): Int = arr.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding: ItemViewPagerBinding = ItemViewPagerBinding.inflate(LayoutInflater.from(activity), container, false)
        weatherViewModel = WeatherViewModel(activity, binding, arr, arr[position].lat, arr[position].lon, position,search)
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

    fun changeKey(checkKey:Boolean) {
        weatherViewModel!!.changeKey(checkKey)
    }

    fun destroy() {
        weatherViewModel!!.destroy()

    }



}