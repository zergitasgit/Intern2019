package hieusenpaj.com.weather.viewmodels

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.Toast
import com.baoyz.widget.PullRefreshLayout
import com.baoyz.widget.SmartisanDrawable
import com.bumptech.glide.Glide
import com.github.tianma8023.model.Time
import hieusenpaj.com.weather.api.ApiServices
import hieusenpaj.com.weather.api.ApiUtils
import hieusenpaj.com.weather.data.DataCity
import hieusenpaj.com.weather.databinding.ItemViewPagerBinding
import hieusenpaj.com.weather.helper.Helper
import hieusenpaj.com.weather.models.City
import hieusenpaj.com.weather.models.current.CurrentWeather
import hieusenpaj.com.weather.models.forecastDay.ForecastDay
import hieusenpaj.com.weather.views.ForecastActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.util.*
import kotlin.collections.ArrayList


class WeatherViewModel(private var activity: Activity, private var binding: ItemViewPagerBinding,private var arrayList: ArrayList<City>,
                       private var lat: Double,private var lon: Double) : Observable() {


    var apiServices: ApiServices? = null
//    var temp: String? = null
    var location: String? = null
    private var sha: SharedPreferences? = null
    private var edit: SharedPreferences.Editor? = null
    private  var temp :Int?=null



    init {
        sha = activity.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        edit = sha!!.edit()
        apiServices = ApiUtils.getApiService()
//        var lat = Helper.getLocation(activity)?.lat
//        Helper.getLocation(activity)?.lat
//        if(arrayList.size==0){
//            lat = Helper.getLocation(activity)!!.lat
//            lon = Helper.getLocation(activity)!!.lon
//        }

        val network = Helper.getLocation(activity)

        if (!network!!.lat.equals(0.0) || !network.lat.equals(null)) {
            getWeatherCurrent(lat, lon)
            getWeatherForecast(lat, lon)
        } else {
            Toast.makeText(activity, "Check lại network", Toast.LENGTH_SHORT).show()
        }
        refeshView()
//

    }

    private fun getWeatherCurrent(lat: Double, lon: Double) {
        apiServices!!.getCurrentWeather(lat, lon, ApiUtils.KEY).enqueue(object : Callback<CurrentWeather> {
            @SuppressLint("SetTextI18n")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<CurrentWeather>, response: Response<CurrentWeather>) {
                val currentWeather = response.body()
                 temp = (currentWeather.data[0].temp.toInt())
                val location = currentWeather.data[0].city_name
                val status = currentWeather.data[0].weather.description
                val visibility = currentWeather.data[0].vis.toInt()
                val uv = currentWeather.data[0].uv.toInt()
                val humidity = currentWeather.data[0].rh
                val windSpeed = currentWeather.data[0].wind_spd.toInt()
                val ahi = currentWeather.data[0].aqi


                if(!sha!!.getBoolean("F",false)) {
                    binding.tvTemp.text = temp.toString()
                }else{
                    binding.tvTemp.text = Helper.convertCtoF(temp!!).toString()

                }
//                binding.tvLocal.text = location
                binding.tvStatus.text = status
                binding.tvHumidity.text = humidity.toString() + "%" + "\n" + "Humidity"
                binding.tvVisibility.text = visibility.toString() + "Km" + "\n" + "Visibility"
                binding.tvUv.text = uv.toString() + "\n" + "UV"
                binding.tvWindSpeed.text = windSpeed.toString() + "m/s" + "\n" + "Wind speed"
                binding.tvAhi.text = "Aqi : " + ahi


//                    binding.tvWindSpeed.text = getTime(currentWeather.data[0].sunset)
                val srArr = Helper.getTime(currentWeather.data[0].sunrise,currentWeather.data[0].timezone).split(":")
                val sunriseHour = Integer.valueOf(srArr[0])
                val sunriseMinute = Integer.valueOf(srArr[1])
                val ssArrSet = Helper.getTime(currentWeather.data[0].sunset,currentWeather.data[0].timezone).split(":")
                val sunsetHour = Integer.valueOf(ssArrSet[0])+12
                val sunsetMinute = Integer.valueOf(ssArrSet[1])
                refreshSSV(currentWeather.data[0].timezone,sunriseHour, sunriseMinute, sunsetHour, sunsetMinute)


            }

            override fun onFailure(call: Call<CurrentWeather>, t: Throwable) {
                Log.e("TAG", t.message)

            }
        })

        binding.tvForecast.setOnClickListener {
            val intent = Intent(activity, ForecastActivity::class.java)
            intent.putExtra("lat",lat)
            intent.putExtra("lon",lon)
            activity.startActivity(intent)
        }

    }

    private fun getWeatherForecast(lat: Double, lon: Double) {
        apiServices!!.getForecastWeather(lat, lon, ApiUtils.KEY, "3").enqueue(object : Callback<ForecastDay> {
            override fun onFailure(call: Call<ForecastDay>?, t: Throwable?) {
                Log.e("TAG", t?.message)
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ForecastDay>?, response: Response<ForecastDay>?) {
                val fw = response!!.body()


                binding.tvDayStatus.text = "Today/" + fw.data[0].weather.description
                binding.tvDayStatus1.text = Helper.getDate(fw.data[1].ts.toLong()) + "/" + fw.data[1].weather.description
                binding.tvDayStatus2.text = Helper.getDate(fw.data[2].ts.toLong()) + "/" + fw.data[2].weather.description


                binding.tvTempMinMax.text = fw.data[0].max_temp.toInt().toString() + "/" + fw.data[0].low_temp.toInt().toString()
                binding.tvTempMinMax1.text = fw.data[1].max_temp.toInt().toString() + "/" + fw.data[1].low_temp.toInt().toString()
                binding.tvTempMinMax2.text = fw.data[2].max_temp.toInt().toString() + "/" + fw.data[2].low_temp.toInt().toString()


                Glide.with(activity)
                        .load(ApiUtils.ICON + fw.data[0].weather.icon + ".png")
                        .into(binding.ivStatus)
                Glide.with(activity)
                        .load(ApiUtils.ICON + fw.data[1].weather.icon + ".png")
                        .into(binding.ivStatus1)
                Glide.with(activity)
                        .load(ApiUtils.ICON + fw.data[2].weather.icon + ".png")
                        .into(binding.ivStatus2)

            }

        })
    }


    private fun refreshSSV(timeZone: String,sunriseHour: Int, sunriseMinute: Int, sunsetHour: Int, sunsetMinute: Int) {
        binding.ssv.setSunriseTime(Time(sunriseHour, sunriseMinute))
        binding.ssv.setSunsetTime(Time(sunsetHour, sunsetMinute))
        binding.ssv.startAnimate(timeZone)
    }

    private fun refeshView() {
        binding.swipeRefreshLayout.setOnRefreshListener(PullRefreshLayout.OnRefreshListener {
//            var lat = Helper.getLocation(activity)?.lat
//            var lon = Helper.getLocation(activity)?.lon
            var network = Helper.getLocation(activity)

            if (!network!!.lat.equals(0.0) || !network.lat.equals(null)) {
                getWeatherCurrent(lat, lon)
                getWeatherForecast(lat, lon)
            } else {
                Toast.makeText(activity, "Check lại network", Toast.LENGTH_SHORT).show()
            }
            binding.swipeRefreshLayout.postDelayed(Runnable {
                binding.swipeRefreshLayout.setRefreshing(false)

//                Toast.makeText(activity, "Check lại network", Toast.LENGTH_SHORT).show()
            }, 3000)
        })
//        binding.swipeRefreshLayout.setColorSchemeColors(Color.GRAY)
        binding.swipeRefreshLayout.setRefreshDrawable(SmartisanDrawable(activity, binding.swipeRefreshLayout))
        binding.swipeRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL)
    }
    fun getWeatherSearch(lat: Double,lon: Double){
        getWeatherCurrent(lat,lon)
    }
    fun changeTemp(){
        if(sha!!.getBoolean("F",false)){
            binding.tvTemp.text = Helper.convertCtoF(temp!!).toString()
        }else{
            binding.tvTemp.text = temp.toString()
        }
    }
}