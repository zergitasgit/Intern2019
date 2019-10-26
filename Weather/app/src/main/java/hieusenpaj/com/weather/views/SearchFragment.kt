package hieusenpaj.com.weather.views

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.SearchView
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.adapter.SearchCityAdapter
import hieusenpaj.com.weather.databinding.FragmentSearchBinding
import hieusenpaj.com.weather.models.City
import hieusenpaj.com.weather.viewmodels.SearchCityViewModel
import hieusenpaj.com.weather.views.base.BaseActivity

class SearchFragment : Fragment() {
    var cityViewModel: SearchCityViewModel? = null


    private var list: ArrayList<City>? = null
    private var adapter: SearchCityAdapter? = null
    private var listAdd: ArrayList<City> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val binding = DataBindingUtil.inflate<FragmentSearchBinding>(inflater, R.layout.fragment_search, container
                , false)
        val view = binding.root
        cityViewModel = SearchCityViewModel(context!!, binding)
        binding.viewModel = cityViewModel
        binding.executePendingBindings()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_search, menu)
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






        super.onCreateOptionsMenu(menu, inflater)
    }

}
