package com.weather.forecastweather.views


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.weather.forecastweather.R
import com.weather.forecastweather.adapter.ViewPagerAdapter
import com.weather.forecastweather.databinding.FragmentManagerCityBinding
import com.weather.forecastweather.databinding.FragmentSearchBinding
import com.weather.forecastweather.viewmodels.ManagerViewModel
import com.weather.forecastweather.viewmodels.SearchCityViewModel


class ManagerCityFragment : Fragment() {
    var model : ManagerViewModel?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding= DataBindingUtil.inflate<FragmentManagerCityBinding>(inflater, R.layout.fragment_manager_city, container
                , false)
        val view = binding.root
        model = ManagerViewModel(activity!!, binding)
        binding.viewModel = model
        binding.executePendingBindings()

        return view
    }
    fun onBack(){
        model!!.onBack()
    }

}
