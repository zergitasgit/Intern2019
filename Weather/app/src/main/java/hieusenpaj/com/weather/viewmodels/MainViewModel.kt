package hieusenpaj.com.weather.viewmodels

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.support.v4.view.ViewPager
import hieusenpaj.com.weather.LocationService
import hieusenpaj.com.weather.adapter.ViewPagerAdapter
import hieusenpaj.com.weather.api.ApiUtils
import hieusenpaj.com.weather.data.DataCity
import hieusenpaj.com.weather.databinding.ActivityMainBinding
import hieusenpaj.com.weather.helper.Helper
import hieusenpaj.com.weather.models.City
import hieusenpaj.com.weather.models.current.CurrentWeather
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
    private val arrKey = ArrayList<String>()


    var checkkeyM = false


    var arrayList = DataCity.getCityViewPager(activity)

    init {
        sha = activity.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        edit = sha!!.edit()

        val locationService = LocationService(activity)
        arrKey.add(ApiUtils.KEY)
        arrKey.add(ApiUtils.KEY2)
        arrKey.add(ApiUtils.KEY1)
        arrKey.add(ApiUtils.KEY3)
        val apiServices = ApiUtils.getApiService()
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
                    for (i in arrKey) {

                        apiServices.getCurrentWeather(lat!!, lon!!, i).enqueue(object : Callback<CurrentWeather> {
                            override fun onFailure(call: Call<CurrentWeather>?, t: Throwable?) {

                            }

                            override fun onResponse(call: Call<CurrentWeather>?, response: Response<CurrentWeather>?) {
                                if (response!!.isSuccessful) {
                                    if (!checkkeyM) {
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
                                        viewPagerAdapter = ViewPagerAdapter(activity, arrayList,false)
                                        binding.viewPager.adapter = viewPagerAdapter
                                        binding.viewPager.offscreenPageLimit = 1

//                            setLocal(arrayList, binding.viewPager.currentItem)
                                        checkkeyM = true
                                    }
                                } else {
                                    checkkeyM = false
                                }

                            }
                        })

                    }

                }
            } else {
                if (!lat!!.equals(arrayList[0].lat) || !lon!!.equals(arrayList[0].lon)) {
                    for (i in arrKey) {

                        apiServices.getCurrentWeather(lat!!, lon!!, i).enqueue(object : Callback<CurrentWeather> {
                            override fun onFailure(call: Call<CurrentWeather>?, t: Throwable?) {
                            }

                            override fun onResponse(call: Call<CurrentWeather>?, response: Response<CurrentWeather>?) {

                                if (response!!.isSuccessful) {
                                    if (!checkkeyM) {
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
                                        viewPagerAdapter = ViewPagerAdapter(activity, arr,false)
                                        binding.viewPager.adapter = viewPagerAdapter
//                            setLocal(arr, binding.viewPager.currentItem)
                                        setUpViewPager(binding)
                                        checkkeyM = true
                                    }
                                } else {
                                    checkkeyM = false
                                }
                            }

                        })

                    }
                } else {

                    viewPagerAdapter = ViewPagerAdapter(activity, arrayList,false)
                    binding.viewPager.adapter = viewPagerAdapter
                    binding.viewPager.offscreenPageLimit = arrayList.size
//                    setLocal(arrayList, binding.viewPager.currentItem)
                    setUpViewPager(binding)
                }
            }
        }


//

    }

    fun destroy() {
        viewPagerAdapter!!.destroy()
    }
    fun changeKey(checkKey:Boolean){
        viewPagerAdapter!!.changeKey(checkKey)
    }

    fun moveHave(pos: Int, have: Boolean) {
        if (have) {
            binding.viewPager.currentItem = pos
//           setLocal(arrayList, binding.viewPager.currentItem)
        } else {
//            WeatherViewModel.checkkey = false
//            WeatherViewModel.checkkey1 = false
            arrayList = getCity()
            viewPagerAdapter = ViewPagerAdapter(activity, arrayList,true)
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