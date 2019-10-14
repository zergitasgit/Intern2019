package hieusenpaj.com.weather.viewmodels

import android.app.Activity
import android.databinding.BaseObservable
import android.util.Log
import android.widget.Toast
import hieusenpaj.com.weather.api.ApiServices
import hieusenpaj.com.weather.api.ApiUtils
import hieusenpaj.com.weather.databinding.ActivityMainBinding
import hieusenpaj.com.weather.helper.Helper
import hieusenpaj.com.weather.models.current.CurrentWeather
import hieusenpaj.com.weather.models.forecastDay.ForecastDay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*




class WeatherViewModel(private var activity: Activity, private var binding: ActivityMainBinding) : BaseObservable() {


    var apiServices: ApiServices? = null
    var temp: String? = null
    var location: String? = null

    init {
        
        apiServices = ApiUtils.getApiService()
        var lat = Helper.getLocation(activity)?.lat
        var lon = Helper.getLocation(activity)?.lon
        var network = Helper.getLocation(activity)

        if (!network!!.lat.equals(0.0) || !network.lat.equals(null)) {
            getWeatherCurrent(lat!!, lon!!)
            getWeatherForecast(lat,lon)
        } else {
            Toast.makeText(activity, "Check lại network", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getWeatherCurrent(lat: Double, lon: Double) {
        apiServices!!.getCurrentWeather(lat, lon, ApiUtils.KEY).enqueue(object : Callback<CurrentWeather> {
            override fun onResponse(call: Call<CurrentWeather>, response: Response<CurrentWeather>) {
                val currentWeather = response.body()
                val temp = (currentWeather.data[0].temp.toInt()).toString()
                val location = currentWeather.data[0].city_name
                val status = currentWeather.data[0].weather.description


                binding.tvTemp.text = temp
                binding.tvLocal.text = location
                binding.tvStatus.text = status


            }

            override fun onFailure(call: Call<CurrentWeather>, t: Throwable) {
                Log.e("TAG",t.message)

            }
        })

    }
    private fun getWeatherForecast(lat: Double,lon: Double){
        apiServices!!.getForecastWeather(lat,lon,ApiUtils.KEY,"3").enqueue(object :Callback<ForecastDay>{
            override fun onFailure(call: Call<ForecastDay>?, t: Throwable?) {
                Log.e("TAG",t?.message)
            }

            override fun onResponse(call: Call<ForecastDay>?, response: Response<ForecastDay>?) {
                val fw = response!!.body()
                binding.tvDayStatus.text = "Today/"+ fw.data[0].weather.description
                binding.tvDayStatus1.text = getDate(fw.data[1].ts.toLong()) +"/" + fw.data[1].weather.description
                binding.tvDayStatus2.text = getDate(fw.data[2].ts.toLong()) +"/" + fw.data[2].weather.description


                binding.tvTempMinMax.text = fw.data[0].max_temp.toInt().toString()+ "/"+fw.data[0].low_temp.toInt().toString()
                binding.tvTempMinMax1.text = fw.data[1].max_temp.toInt().toString()+ "/"+fw.data[1].low_temp.toInt().toString()
                binding.tvTempMinMax2.text = fw.data[2].max_temp.toInt().toString()+ "/"+fw.data[2].low_temp.toInt().toString()

                Log.e("TAG","hi")

            }

        })
    }


    fun getDate(dt:Long):String{



        var date = Date(dt*1000L)
        var simpleDateFormat = SimpleDateFormat("EEEE")
        return  simpleDateFormat.format(date)

    }
}