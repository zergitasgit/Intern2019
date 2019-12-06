package com.weather.forecastweather.views

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.weather.forecastweather.R
import com.weather.forecastweather.R.id.line_char
import com.weather.forecastweather.databinding.ActivityForecastBinding
import com.weather.forecastweather.databinding.ActivityMainBinding
import com.weather.forecastweather.helper.Helper
import com.weather.forecastweather.viewmodels.ForecastViewModel
import com.weather.forecastweather.viewmodels.WeatherViewModel
import com.weather.forecastweather.views.base.BaseActivity

class ForecastActivity : BaseActivity() {
    override fun bindingView() {
        val binding : ActivityForecastBinding = DataBindingUtil.setContentView(this, R.layout.activity_forecast)
        val intent=intent
        binding.setViewModel(ForecastViewModel(this, binding,intent.getDoubleExtra("lat",0.0),
                intent.getDoubleExtra("lon",0.0),intent.getStringExtra("bg")))
        binding.executePendingBindings()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_forecast)

    }
}
