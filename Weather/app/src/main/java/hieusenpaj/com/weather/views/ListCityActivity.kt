package hieusenpaj.com.weather.views

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.databinding.ActivityListCityBinding
import hieusenpaj.com.weather.viewmodels.ListCityViewModel
import hieusenpaj.com.weather.views.base.BaseActivity

class ListCityActivity : BaseActivity() {
    private var model:ListCityViewModel?=null
    override fun bindingView() {
        val binding : ActivityListCityBinding = DataBindingUtil.setContentView(this, R.layout.activity_list_city)
        val intent=intent
        model = ListCityViewModel(this,binding)
        binding.setViewModel(model)
        binding.executePendingBindings()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_list_city)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        model!!.onBack()
    }
}
