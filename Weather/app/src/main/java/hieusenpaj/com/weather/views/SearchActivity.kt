package hieusenpaj.com.weather.views

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.adapter.SearchCityAdapter
import hieusenpaj.com.weather.databinding.ActivitySearchBinding
import hieusenpaj.com.weather.models.City
import hieusenpaj.com.weather.viewmodels.CityViewModel
import hieusenpaj.com.weather.views.base.BaseActivity

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
    private var adapter: SearchCityAdapter? = null
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
