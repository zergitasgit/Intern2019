package hieusenpaj.com.weather.views

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.R.id.line_char
import hieusenpaj.com.weather.databinding.ActivityForecastBinding
import hieusenpaj.com.weather.databinding.ActivityMainBinding
import hieusenpaj.com.weather.helper.Helper
import hieusenpaj.com.weather.viewmodels.ForecastViewModel
import hieusenpaj.com.weather.viewmodels.WeatherViewModel
import hieusenpaj.com.weather.views.base.BaseActivity

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
