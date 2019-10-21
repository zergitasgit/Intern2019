package hieusenpaj.com.weather.viewmodels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import hieusenpaj.com.weather.adapter.CityAdapter
import hieusenpaj.com.weather.data.DataCity
import hieusenpaj.com.weather.databinding.ActivitySearchBinding
import hieusenpaj.com.weather.db.DBHistory
import hieusenpaj.com.weather.models.City
import java.util.*
import kotlin.collections.ArrayList

class CityViewModel(private var activity: Activity, private var binding: ActivitySearchBinding
) : Observable() {
    private var adapter: CityAdapter? = null
    private var listAdd: ArrayList<City> = ArrayList()
    private val dbHistory = DBHistory(activity,null)

    init {
        listAdd = dbHistory.getCity()
        setAdapter()
    }

    fun search(string: String) {
        if (!string.isEmpty()) {
            listAdd.clear()
            listAdd = DataCity.getListCity(activity, string)
            setAdapter()
        }else{
            listAdd.clear()
            listAdd = dbHistory.getCity()
            setAdapter()

        }
    }
    private fun setAdapter(){
        adapter = CityAdapter(activity, listAdd, object : CityAdapter.ItemListener {
            override fun onClick(city:String,country:String,lat: Double, lon: Double) {
                activity.onBackPressed()
                val intent = Intent("SEARCH")
                intent.putExtra("lat", lat)
                intent.putExtra("lon", lon)
                activity.sendBroadcast(intent)
                dbHistory.insertHistory(city,country,lat.toString(),lon.toString(),System.currentTimeMillis())

            }

        })
        binding.rv.layoutManager = LinearLayoutManager(activity)
        binding.rv.setAdapter(adapter)
    }

}