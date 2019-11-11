package hieusenpaj.com.weather.viewmodels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v4.view.ViewPager
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import hieusenpaj.com.weather.LocationService
import hieusenpaj.com.weather.adapter.ViewPagerAdapter
import hieusenpaj.com.weather.api.ApiUtils
import hieusenpaj.com.weather.data.DataCity
import hieusenpaj.com.weather.databinding.ActivityMainBinding
import hieusenpaj.com.weather.helper.Helper
import hieusenpaj.com.weather.models.City
import hieusenpaj.com.weather.models.current.CurrentWeather
import hieusenpaj.com.weather.views.ListCityActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(private var activity: Activity, private var binding: ActivityMainBinding
) : Observable() {
    private var arr = ArrayList<City>()
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private var sha: SharedPreferences? = null
    private var edit: SharedPreferences.Editor? = null
    private var lon: Double? = null
    private var lat: Double? = null
    private var check = false
    var arrayList = DataCity.getCityViewPager(activity)

    init {
        sha = activity.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        edit = sha!!.edit()

        val locationService = LocationService(activity)

//        val lat = Helper.getLocation(activity)!!.lat
//        val lon = Helper.getLocation(activity)!!.lon
        val apiServices = ApiUtils.getApiService()
//        Log.e("hi",arr.size.toString())
//        Toast.makeText(activity, lat.toString() + lon.toString(), Toast.LENGTH_SHORT).show()
        if (locationService.canGetLocation()) {
            lat = locationService.getLat()
            lon = locationService.getLon()
            check = true


        } else {
            locationService.showDialog()
            check = false
        }
        if (check) {
            if (arrayList.size == 0) {
                if (apiServices != null) {
                    apiServices.getCurrentWeather(lat!!, lon!!, ApiUtils.KEY).enqueue(object : Callback<CurrentWeather> {
                        override fun onFailure(call: Call<CurrentWeather>?, t: Throwable?) {
                            Toast.makeText(activity, "hi", Toast.LENGTH_SHORT).show()

                        }

                        override fun onResponse(call: Call<CurrentWeather>?, response: Response<CurrentWeather>?) {
                            Toast.makeText(activity, "hieu", Toast.LENGTH_SHORT).show()
                            val currentWeather = response!!.body()
                            val location = currentWeather.data[0].city_name
                            val temp = (currentWeather.data[0].temp.toInt()).toString()
                            val country = currentWeather.data[0].country_code
                            val code = currentWeather.data[0].weather.code
                            val timeZone = currentWeather.data[0].timezone
                            var bg: String? = null

                            arrayList.clear()
                            if (Helper.getCurrentTimeZone(timeZone) < 18) {
                                bg = DataCity.getBg(activity, code)[0].imageDay
                            } else {
                                bg = DataCity.getBg(activity, code)[0].imageNight
                            }
                            arrayList.add(City(location, country, lat!!, lon!!, temp, bg, code.toString(),
                                    timeZone, false))

                            DataCity.insertHistory(activity, location, currentWeather.data[0].country_code,
                                    Helper.getLocation(activity)?.lat.toString(),
                                    Helper.getLocation(activity)?.lon.toString(),
                                    temp, bg, System.currentTimeMillis(), code.toString(), timeZone)
//                    viewPagerAdapter = ViewPagerAdapter(activity, arrayList)
                            viewPagerAdapter = ViewPagerAdapter(activity, arrayList)
                            binding.viewPager.adapter = viewPagerAdapter
                            binding.viewPager.offscreenPageLimit = 1

//                            setLocal(arrayList, binding.viewPager.currentItem)

                        }
                    })
                }
            } else {
                if (!lat!!.equals(arrayList[0].lat) || !lon!!.equals(arrayList[0].lon)) {
                    apiServices.getCurrentWeather(lat!!, lon!!, ApiUtils.KEY).enqueue(object : Callback<CurrentWeather> {
                        override fun onFailure(call: Call<CurrentWeather>?, t: Throwable?) {
                        }

                        override fun onResponse(call: Call<CurrentWeather>?, response: Response<CurrentWeather>?) {
                            val currentWeather = response!!.body()
                            val location = currentWeather.data[0].city_name
                            val temp = (currentWeather.data[0].temp.toInt()).toString()
                            val country = currentWeather.data[0].country_code
                            val code = currentWeather.data[0].weather.code
                            val timeZone = currentWeather.data[0].timezone
                            if (Helper.getCurrentTimeZone(timeZone) < 18) {
                                DataCity.updateLocal(activity, location, country, lat.toString(), lon.toString(), temp,
                                        DataCity.getBg(activity, code)[0].imageDay, code.toString(), timeZone, 0)
                            } else {
                                DataCity.updateLocal(activity, location, country, lat.toString(), lon.toString(), temp,
                                        DataCity.getBg(activity, code)[0].imageNight, code.toString(), timeZone, 0)
                            }


//                    viewPagerAdapter = ViewPagerAdapter(activity, arrayList)
                            var arr = DataCity.getCityViewPager(activity)
                            binding.viewPager.offscreenPageLimit = arr.size
                            viewPagerAdapter = ViewPagerAdapter(activity, arr)
                            binding.viewPager.adapter = viewPagerAdapter
//                            setLocal(arr, binding.viewPager.currentItem)
                            setUpViewPager(binding)
                        }
                    })
                } else {

                    viewPagerAdapter = ViewPagerAdapter(activity, arrayList)
                    binding.viewPager.adapter = viewPagerAdapter
                    binding.viewPager.offscreenPageLimit = arrayList.size
//                    setLocal(arrayList, binding.viewPager.currentItem)
                    setUpViewPager(binding)
                }
            }
        }

//

    }

    fun moveHave(pos: Int, have: Boolean) {
        if (have) {
            binding.viewPager.currentItem = pos
//           setLocal(arrayList, binding.viewPager.currentItem)
        } else {
            arrayList = getCity()
            viewPagerAdapter = ViewPagerAdapter(activity, arrayList)
            binding.viewPager.adapter = viewPagerAdapter
            binding.viewPager.offscreenPageLimit = arrayList.size
//           setLocal(arrayList, binding.viewPager.currentItem)
            setUpViewPager(binding)
            binding.viewPager.currentItem = arrayList.size - 1

        }
    }


    fun change(pos: Int) {
        arrayList = getCity()
        viewPagerAdapter!!.notifyDataSetChanged()
        binding.viewPager.currentItem = pos
//        setLocal(arrayList, binding.viewPager.currentItem)

    }

    fun getCity(): ArrayList<City> {
        var arr = DataCity.getCityViewPager(activity)
        return arr
    }

    fun getIdCity(name: String): Int {
        var id = DataCity.getIdCity(activity, name)
        return id
    }

//

    fun setUpViewPager(binding: ActivityMainBinding) {
        binding.viewPager.addOnPageChangeListener(
                object : ViewPager.OnPageChangeListener {
                    override fun onPageSelected(p0: Int) {
                        // no-op

                    }

                    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                        // no-op
                    }

                    override fun onPageScrollStateChanged(p0: Int) {
                        when (p0) {
                            ViewPager.SCROLL_STATE_SETTLING -> {

                            }
                            ViewPager.SCROLL_STATE_IDLE -> {

                            }
                            else -> {
                                // no-op
                            }
                        }
                    }
                }
        )
    }


}