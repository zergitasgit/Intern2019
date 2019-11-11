package hieusenpaj.com.weather.views

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.viewmodels.ManagerViewModel
import hieusenpaj.com.weather.views.base.BaseActivity

class ListCityActivity : AppCompatActivity() {
    val fragment = ManagerCityFragment()
//
//

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_city)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
//        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        if (getFragmentManager().backStackEntryCount > 0) {
            getFragmentManager().popBackStack()
        } else {
            super.onBackPressed()
            fragment.onBack()
        }

    }
}
