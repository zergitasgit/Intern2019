package hieusenpaj.com.weather.views


import android.databinding.DataBindingUtil
import android.os.Bundle
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.databinding.ActivityMainBinding
import hieusenpaj.com.weather.helper.Helper
import hieusenpaj.com.weather.viewmodels.WeatherViewModel
import hieusenpaj.com.weather.views.base.BaseActivity

class MainActivity : BaseActivity() {


    override fun bindingView() {
        val binding : ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.setViewModel(WeatherViewModel(this, binding))
        binding.rl.layoutParams.height = Helper.getHeightScreen(this)
        binding.executePendingBindings()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

    }





}
