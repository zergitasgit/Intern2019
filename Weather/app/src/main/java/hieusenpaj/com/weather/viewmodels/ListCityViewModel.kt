package hieusenpaj.com.weather.viewmodels

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.adapter.ListCityAdapter
import hieusenpaj.com.weather.data.DataCity
import hieusenpaj.com.weather.databinding.ActivityListCityBinding
import hieusenpaj.com.weather.models.City
import java.util.*

class ListCityViewModel(private var activity: Activity, private var binding: ActivityListCityBinding
) : Observable()  {
    private var adapter: ListCityAdapter? = null
    private var listAdd: ArrayList<City> = ArrayList()
    private var check :Boolean =false
    init {
        listAdd = DataCity.getCityViewPager(activity)
        adapter = ListCityAdapter(activity, listAdd, object : ListCityAdapter.ItemListener {
            override fun onClick(pos:Int,city:String,country:String,lat: Double, lon: Double) {
                activity.onBackPressed()
                val intent = Intent("SEARCH_LIST")
                intent.putExtra("lat", lat)
                intent.putExtra("lon", lon)
                intent.putExtra("pos", pos)
                activity.sendBroadcast(intent)
//
            }

        })
        binding.rv.layoutManager = LinearLayoutManager(activity)
        binding.rv.setAdapter(adapter)

        binding.ivEdit.setOnClickListener {
            if(check) {
                adapter!!.setUpDelete()
                binding.ivEdit.setImageDrawable(activity.getResources().getDrawable(R.drawable.tick))
                check=true
            }else{

            }
        }
    }
}