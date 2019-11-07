package hieusenpaj.com.weather.views


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

import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.adapter.ViewPagerAdapter
import hieusenpaj.com.weather.databinding.FragmentManagerCityBinding
import hieusenpaj.com.weather.databinding.FragmentSearchBinding
import hieusenpaj.com.weather.viewmodels.ManagerViewModel
import hieusenpaj.com.weather.viewmodels.SearchCityViewModel


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
//        context!!.registerReceiver(broadcastReceiver, IntentFilter("BACK"))

        return view
    }
    fun onBack(){
        model!!.onBack()
    }
//    private var broadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(p0: Context?, p1: Intent?) {
//            val action = p1?.action
//
//            if (action!!.equals("BACK", ignoreCase = true)) {
//                model!!.onBack()
//            }
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
//        context!!.unregisterReceiver(broadcastReceiver)
    }

}
