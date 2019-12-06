package com.weather.forecastweather.viewmodels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.weather.forecastweather.adapter.SearchCityAdapter
import com.weather.forecastweather.adapter.ViewPagerAdapter
import com.weather.forecastweather.api.ApiUtils
import com.weather.forecastweather.data.DataCity
import com.weather.forecastweather.databinding.FragmentSearchBinding
import com.weather.forecastweather.db.DBHistory
import com.weather.forecastweather.helper.Helper
import com.weather.forecastweather.models.City
import com.weather.forecastweather.models.current.CurrentWeather
import com.weather.forecastweather.views.ListCityActivity
import com.weather.forecastweather.views.MainActivity
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
    private val arrKey = java.util.ArrayList<String>()
    var checkkey = false
    private var sha: SharedPreferences? = null
    private var edit: SharedPreferences.Editor? = null
    init {
        sha = context.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        edit = sha!!.edit()
        (context as AppCompatActivity).setSupportActionBar(binding.toolbarSearch)
        arrKey.add(ApiUtils.KEY)
        arrKey.add(ApiUtils.KEY2)
        arrKey.add(ApiUtils.KEY1)
        arrKey.add(ApiUtils.KEY3)
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
                        for (i in arrKey) {

                            apiServices.getCurrentWeather(lat, lon, i).enqueue(object : Callback<CurrentWeather> {
                                override fun onFailure(call: Call<CurrentWeather>?, t: Throwable?) {

                                }

                                override fun onResponse(call: Call<CurrentWeather>?, response: Response<CurrentWeather>?) {
                                    if (response!!.isSuccessful) {
                                        if (!checkkey) {
                                            val currentWeather = response!!.body()
                                            val temp = (currentWeather.data[0].temp.toInt()).toString()
                                            val code = currentWeather.data[0].weather.code
                                            val timeZone = currentWeather.data[0].timezone
                                            var bg: String? = null
                                            (context as AppCompatActivity).finish()
//                    viewPagerAdapter = ViewPagerAdapter(context, arrayList)
                                            val intent = Intent("SEARCH")
                                            if (!DataCity.checkCitySearch(context, city)) {
                                                intent.putExtra("have", false)
                                                if (Helper.getCurrentTimeZone(timeZone) < 18) {
                                                    bg = DataCity.getBg(context, code)[0].imageDay
                                                } else {
                                                    bg = DataCity.getBg(context, code)[0].imageNight
                                                }
                                                DataCity.insertHistory(context, city, country, lat.toString(), lon.toString(),
                                                        temp, bg, System.currentTimeMillis(), code.toString(), timeZone)
                                            } else {
                                                DataCity.updateHistory(context, city, System.currentTimeMillis())
                                                intent.putExtra("have", true)

                                            }
                                            intent.putExtra("city", city)
                                            intent.putExtra("lat", lat)
                                            intent.putExtra("lon", lon)

                                            context.sendBroadcast(intent)
                                            checkkey = true
                                        }
                                    } else {
                                        checkkey = false

                                    }
                                }
                            })


                            Log.e("TAG", DataCity.checkCitySearch(context, city).toString())


                        }
                    }

                })


        binding.rv.layoutManager = LinearLayoutManager(context)
        binding.rv.setAdapter(adapter)
    }

}