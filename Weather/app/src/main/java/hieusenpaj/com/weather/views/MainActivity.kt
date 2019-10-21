package hieusenpaj.com.weather.views


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Bundle
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.databinding.ActivityMainBinding
import hieusenpaj.com.weather.helper.Helper
import hieusenpaj.com.weather.viewmodels.WeatherViewModel
import hieusenpaj.com.weather.views.base.BaseActivity

class MainActivity : BaseActivity() {
    var viewModel: WeatherViewModel? = null

    override fun bindingView() {
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = WeatherViewModel(this, binding)
        binding.setViewModel(viewModel)
        binding.rl.layoutParams.height = Helper.getHeightScreen(this)
        binding.executePendingBindings()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        registerReceiver(broadcastReceiver, IntentFilter("SEARCH"))

    }

    private var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action
            var lat = p1?.extras?.getDouble("lat")
            var lon = p1?.extras?.getDouble("lon")

//            if (string!!.isEmpty() || string.length == 0) {
//
//            }

            if (action!!.equals("SEARCH", ignoreCase = true)) {
                viewModel!!.getWeatherSearch(lat!!,lon!!)
            }
        }

    }


    override fun onRestart() {
        super.onRestart()


    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }


}
