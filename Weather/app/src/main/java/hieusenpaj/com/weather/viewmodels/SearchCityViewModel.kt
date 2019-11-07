package hieusenpaj.com.weather.viewmodels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import hieusenpaj.com.weather.adapter.SearchCityAdapter
import hieusenpaj.com.weather.adapter.ViewPagerAdapter
import hieusenpaj.com.weather.api.ApiUtils
import hieusenpaj.com.weather.data.DataCity
import hieusenpaj.com.weather.databinding.FragmentSearchBinding
import hieusenpaj.com.weather.db.DBHistory
import hieusenpaj.com.weather.helper.Helper
import hieusenpaj.com.weather.models.City
import hieusenpaj.com.weather.models.current.CurrentWeather
import hieusenpaj.com.weather.views.ListCityActivity
import hieusenpaj.com.weather.views.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class SearchCityViewModel(private var context: Context, private var binding: FragmentSearchBinding
) : Observable() {
    private var adapter: SearchCityAdapter? = null
    private var listAdd: ArrayList<City> = ArrayList()
    private val dbHistory = DBHistory(context, null)
    private var apiServices = ApiUtils.getApiService()

    init {
        (context as AppCompatActivity).setSupportActionBar(binding.toolbarSearch)

        listAdd = DataCity.getCityHistory(context)
        setAdapter()
    }

    fun search(string: String) {
        if (!string.isEmpty()) {
            listAdd.clear()
            listAdd = DataCity.getListCity(context, string)
            setAdapter()
        } else {
            listAdd.clear()
            listAdd = DataCity.getCityHistory(context)
            setAdapter()

        }
    }

    private fun setAdapter() {
        adapter = SearchCityAdapter(context, listAdd, object : SearchCityAdapter.ItemListener {
            override fun onClick(pos: Int, city: String, country: String, lat: Double, lon: Double, temp: String, status: String) {

                apiServices.getCurrentWeather(lat, lon, ApiUtils.KEY).enqueue(object : Callback<CurrentWeather> {
                    override fun onFailure(call: Call<CurrentWeather>?, t: Throwable?) {

                    }

                    override fun onResponse(call: Call<CurrentWeather>?, response: Response<CurrentWeather>?) {
                        val currentWeather = response!!.body()
                        val temp = (currentWeather.data[0].temp.toInt()).toString()
                        val code = currentWeather.data[0].weather.code
                        val timeZone = currentWeather.data[0].timezone
                        var bg:String?=null
                        (context as AppCompatActivity).finish()
//                    viewPagerAdapter = ViewPagerAdapter(context, arrayList)
                        val intent = Intent("SEARCH")
                        if (!DataCity.checkCitySearch(context, city)) {
                            intent.putExtra("have",false)
                            if (Helper.getCurrentTimeZone(timeZone) < 18) {
                                bg =DataCity.getBg(context,code)[0].imageDay
                            }else{
                                bg = DataCity.getBg(context,code)[0].imageNight
                            }
                            DataCity.insertHistory(context, city, country, lat.toString(), lon.toString(),
                                    temp,bg , System.currentTimeMillis(),code.toString(),timeZone)
                        } else {
                            DataCity.updateHistory(context, city, System.currentTimeMillis())
                            intent.putExtra("have",true)

                        }

//                        val intentBack = Intent(context, MainActivity::class.java)
//                        context.startActivity(intentBack)
                        intent.putExtra("city", city)
                        intent.putExtra("lat", lat)
                        intent.putExtra("lon", lon)

                        context.sendBroadcast(intent)
                    }
                })

//                dbHistory.insertHistory(city,country,lat.toString(),lon.toString(),System.currentTimeMillis())
                Log.e("TAG", DataCity.checkCitySearch(context, city).toString())


            }

        })
        binding.rv.layoutManager = LinearLayoutManager(context)
        binding.rv.setAdapter(adapter)
    }

}