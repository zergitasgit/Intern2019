package hieusenpaj.com.weather.viewmodels

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.design.internal.NavigationMenu
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.adapter.ViewPagerAdapter
import hieusenpaj.com.weather.api.ApiUtils
import hieusenpaj.com.weather.data.DataCity
import hieusenpaj.com.weather.databinding.ActivityMainBinding
import hieusenpaj.com.weather.databinding.ActivitySearchBinding
import hieusenpaj.com.weather.db.DBCity
import hieusenpaj.com.weather.db.DBHistory
import hieusenpaj.com.weather.helper.Helper
import hieusenpaj.com.weather.models.City
import hieusenpaj.com.weather.models.current.CurrentWeather
import hieusenpaj.com.weather.views.ListCityActivity
import hieusenpaj.com.weather.views.SearchActivity
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter
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

    init {
        sha = activity.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        edit = sha!!.edit()
        val arrayList = DataCity.getCityViewPager(activity)
        val lat = Helper.getLocation(activity)!!.lat
        val lon = Helper.getLocation(activity)!!.lon
        val apiServices = ApiUtils.getApiService()
//        Log.e("hi",arr.size.toString())
//        Toast.makeText(activity,arr.size.toString(),Toast.LENGTH_SHORT).show()

        if (arrayList.size == 0) {

            apiServices.getCurrentWeather(lat, lon, ApiUtils.KEY).enqueue(object : Callback<CurrentWeather> {
                override fun onFailure(call: Call<CurrentWeather>?, t: Throwable?) {

                }

                override fun onResponse(call: Call<CurrentWeather>?, response: Response<CurrentWeather>?) {
                    val currentWeather = response!!.body()
                    val location = currentWeather.data[0].city_name
                    val temp = (currentWeather.data[0].temp.toInt()).toString()
                    val country = currentWeather.data[0].country_code
                    val status = currentWeather.data[0].weather.icon

                    arrayList.clear()
                    arrayList.add(City(location, country, lat, lon, temp, status, false))
                    DataCity.insertHistory(activity, location, currentWeather.data[0].country_code, Helper.getLocation(activity)?.lat.toString(),
                            Helper.getLocation(activity)?.lon.toString(),
                            temp, status, System.currentTimeMillis())
//                    viewPagerAdapter = ViewPagerAdapter(activity, arrayList)
                    binding.viewPager.offscreenPageLimit = 1
                    viewPagerAdapter = ViewPagerAdapter(activity, arrayList)
                    binding.viewPager.adapter = viewPagerAdapter
                    setLocal(arrayList, binding.viewPager.currentItem)

                }
            })
        } else {
            if (!lat.equals(arrayList[0].lat) || !lon.equals(arrayList[0].lon)) {
                apiServices.getCurrentWeather(lat, lon, ApiUtils.KEY).enqueue(object : Callback<CurrentWeather> {
                    override fun onFailure(call: Call<CurrentWeather>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<CurrentWeather>?, response: Response<CurrentWeather>?) {
                        val currentWeather = response!!.body()
                        val location = currentWeather.data[0].city_name
                        val temp = (currentWeather.data[0].temp.toInt()).toString()
                        val country = currentWeather.data[0].country_code
                        val status = currentWeather.data[0].weather.icon
                        DataCity.updateLocal(activity, location, country, lat.toString(), lon.toString(), temp, status)


//                    viewPagerAdapter = ViewPagerAdapter(activity, arrayList)
                        var arr = DataCity.getCityViewPager(activity)
                        binding.viewPager.offscreenPageLimit = 1
                        viewPagerAdapter = ViewPagerAdapter(activity, arr)
                        binding.viewPager.adapter = viewPagerAdapter
                        setLocal(arr, binding.viewPager.currentItem)
                        setUpViewPager(binding)
                    }
                })
            } else {
                binding.viewPager.offscreenPageLimit = 1
                viewPagerAdapter = ViewPagerAdapter(activity, arrayList)
                binding.viewPager.adapter = viewPagerAdapter
                setLocal(arrayList, binding.viewPager.currentItem)
                setUpViewPager(binding)
            }
        }

        binding.ivSearch.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            activity.startActivity(intent)
        }
        binding.ivSetting.setOnClickListener {
            val intent = Intent(activity, ListCityActivity::class.java)
            activity.startActivity(intent)
        }
        itemMenuSelect()

    }
    fun brChangTemp(){
        viewPagerAdapter!!.changeTemp()
    }

    //    fun getArr(): Arr {
//        var arr = DataCity.getCityViewPager(activity)
//        return arr
//    }
    fun getCity(): ArrayList<City> {
        var arr = DataCity.getCityViewPager(activity)
        return arr
    }

    fun getIdCity(name: String): Int {
        var id = DataCity.getIdCity(activity, name)
        return id
    }

    fun itemMenuSelect() {

        binding.fabSpeed.setMenuListener(object : SimpleMenuListenerAdapter() {
            override fun onMenuItemSelected(menuItem: MenuItem?): Boolean {

                when (menuItem!!.itemId) {
                    R.id.action_call -> changeTemp(menuItem);
                }



                return true
            }


            @SuppressLint("RestrictedApi")
            override fun onPrepareMenu(navigationMenu: NavigationMenu?): Boolean {
                var menuItem = navigationMenu!!.findItem(R.id.action_call)
                setUpTemp(menuItem)
                return true
            }


        })

    }

    fun setUpTemp(menuItem: MenuItem) {
        if (sha!!.getBoolean("F", false)) {
            menuItem.setTitle("°F")
        } else {
            menuItem.setTitle("°C")
        }
    }

    fun changeTemp(menuItem: MenuItem) {
        if (!sha!!.getBoolean("F", false)) {
            edit!!.putBoolean("F", true)
            edit!!.apply()


        } else {
            edit!!.putBoolean("F", false)
            edit!!.apply()

        }
        val intent = Intent("CHANGE_TEMP")
        activity.sendBroadcast(intent)
    }


    fun setLocal(arr: ArrayList<City>, pos: Int) {
        binding.tvLocal.text = arr[pos].city
    }

    fun setUpViewPager(binding: ActivityMainBinding) {
        binding.viewPager.addOnPageChangeListener(
                object : ViewPager.OnPageChangeListener {
                    override fun onPageSelected(p0: Int) {
                        // no-op
                        setLocal(getCity(), binding.viewPager.currentItem)
//                        Toast.makeText(this@MainActivity,p0.toString(),Toast.LENGTH_SHORT).show()
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