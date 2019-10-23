package hieusenpaj.com.weather.views

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.databinding.ActivityListCityBinding
import hieusenpaj.com.weather.viewmodels.ListCityViewModel
import hieusenpaj.com.weather.views.base.BaseActivity

class ListCityActivity : BaseActivity() {
    override fun bindingView() {
        val binding : ActivityListCityBinding = DataBindingUtil.setContentView(this, R.layout.activity_list_city)
        val intent=intent
        binding.setViewModel(ListCityViewModel(this, binding))
        binding.executePendingBindings()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_list_city)
    }
}
