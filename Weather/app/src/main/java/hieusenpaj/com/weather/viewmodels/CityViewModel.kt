package hieusenpaj.com.weather.viewmodels

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import hieusenpaj.com.weather.adapter.SearchCityAdapter
import hieusenpaj.com.weather.adapter.ViewPagerAdapter
import hieusenpaj.com.weather.api.ApiUtils
import hieusenpaj.com.weather.data.DataCity
import hieusenpaj.com.weather.databinding.ActivitySearchBinding
import hieusenpaj.com.weather.db.DBHistory
import hieusenpaj.com.weather.helper.Helper
import hieusenpaj.com.weather.models.City
import hieusenpaj.com.weather.models.current.CurrentWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CityViewModel(private var activity: Activity, private var binding: ActivitySearchBinding
) : Observable() {
    private var adapter: SearchCityAdapter? = null
    private var listAdd: ArrayList<City> = ArrayList()
    private val dbHistory = DBHistory(activity, null)
    private var apiServices = ApiUtils.getApiService()

    init {

        listAdd = DataCity.getCityHistory(activity)
        setAdapter()
    }

    fun search(string: String) {
        if (!string.isEmpty()) {
            listAdd.clear()
            listAdd = DataCity.getListCity(activity, string)
            setAdapter()
        } else {
            listAdd.clear()
            listAdd = DataCity.getCityHistory(activity)
            setAdapter()

        }
    }

    private fun setAdapter() {
        adapter = SearchCityAdapter(activity, listAdd, object : SearchCityAdapter.ItemListener {
            override fun onClick(pos: Int, city: String, country: String, lat: Double, lon: Double, temp: String, status: String) {

                apiServices.getCurrentWeather(lat, lon, ApiUtils.KEY).enqueue(object : Callback<CurrentWeather> {
                    override fun onFailure(call: Call<CurrentWeather>?, t: Throwable?) {

                    }

                    override fun onResponse(call: Call<CurrentWeather>?, response: Response<CurrentWeather>?) {
                        val currentWeather = response!!.body()
                        val temp = (currentWeather.data[0].temp.toInt()).toString()
                        val status = currentWeather.data[0].weather.icon


//                    viewPagerAdapter = ViewPagerAdapter(activity, arrayList)
                        val intent = Intent("SEARCH")
                        if (!DataCity.checkCitySearch(activity, city)) {
                            intent.putExtra("city", " ")
                            DataCity.insertHistory(activity, city, country, lat.toString(), lon.toString(), temp, status, System.currentTimeMillis())
                        } else {
                            DataCity.updateHistory(activity, city, System.currentTimeMillis())
                            intent.putExtra("city", city)
                        }
                        activity.onBackPressed()

                        intent.putExtra("lat", lat)
                        intent.putExtra("lon", lon)

                        activity.sendBroadcast(intent)
                    }
                })

//                dbHistory.insertHistory(city,country,lat.toString(),lon.toString(),System.currentTimeMillis())
                Log.e("TAG", DataCity.checkCitySearch(activity, city).toString())


            }

        })
        binding.rv.layoutManager = LinearLayoutManager(activity)
        binding.rv.setAdapter(adapter)
    }

}