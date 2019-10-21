package hieusenpaj.com.weather.views

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.adapter.CityAdapter
import hieusenpaj.com.weather.data.DataCity
import hieusenpaj.com.weather.databinding.ActivityMainBinding
import hieusenpaj.com.weather.databinding.ActivitySearchBinding
import hieusenpaj.com.weather.helper.Helper
import hieusenpaj.com.weather.models.City
import hieusenpaj.com.weather.viewmodels.CityViewModel
import hieusenpaj.com.weather.viewmodels.WeatherViewModel
import hieusenpaj.com.weather.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : BaseActivity(){
    var cityViewModel:CityViewModel?=null
    override fun bindingView() {
        val binding : ActivitySearchBinding  = DataBindingUtil.setContentView(this@SearchActivity, R.layout.activity_search)
        cityViewModel= CityViewModel(this@SearchActivity, binding)
        binding.setViewModel(cityViewModel)
        binding.executePendingBindings()
        setSupportActionBar(binding.toolbarSearch)
    }

    private var list: ArrayList<City>? = null
    private var adapter: CityAdapter? = null
    private var listAdd :ArrayList<City> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_search)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchViewItem = menu!!.findItem(R.id.action_search)

        //getting the search view
        val searchView = searchViewItem.actionView as SearchView

        //making the searchview consume all the toolbar when open
        searchView.maxWidth = Int.MAX_VALUE

        searchView.queryHint = "Search"
        searchView.setFocusable(true)
        searchView.setIconified(false)
        searchView.clearFocus()
        searchView.requestFocusFromTouch()

//        searchViewItem?.expandActionView()



//
//
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                cityViewModel!!.search(p0!!)
                return false
            }

        })






        return super.onCreateOptionsMenu(menu)
    }
}
