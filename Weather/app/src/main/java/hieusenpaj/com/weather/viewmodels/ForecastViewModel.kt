package hieusenpaj.com.weather.viewmodels

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import hieusenpaj.com.weather.api.ApiServices
import hieusenpaj.com.weather.api.ApiUtils
import hieusenpaj.com.weather.databinding.ActivityForecastBinding
import hieusenpaj.com.weather.helper.Helper
import hieusenpaj.com.weather.models.forecastDay.ForecastDay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ForecastViewModel(private var activity: Activity, private var binding: ActivityForecastBinding) :Observable() {
    var apiServices: ApiServices? = null
    var temp: String? = null
    var location: String? = null
    init {
        apiServices = ApiUtils.getApiService()
        var lat = Helper.getLocation(activity)?.lat
        var lon = Helper.getLocation(activity)?.lon
        var network = Helper.getLocation(activity)

        if (!network!!.lat.equals(0.0) || !network.lat.equals(null)) {
            getWeatherForecast(lat!!, lon!!)
        } else {
            Toast.makeText(activity, "Check lại network", Toast.LENGTH_SHORT).show()
        }

        binding.ivBack.setOnClickListener {
            activity.onBackPressed()
        }

    }

    private fun getWeatherForecast(lat: Double, lon: Double) {
        apiServices!!.getForecastWeather(lat, lon, ApiUtils.KEY, "5").enqueue(object : Callback<ForecastDay> {
            override fun onFailure(call: Call<ForecastDay>?, t: Throwable?) {
                Log.e("TAG", t?.message)
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ForecastDay>?, response: Response<ForecastDay>?) {
                val fw = response!!.body()


                binding.tvEe.text = "Today"
                binding.tvEe2.text = Helper.getDate(fw.data[1].ts.toLong())
                binding.tvEe3.text = Helper.getDate(fw.data[2].ts.toLong())
                binding.tvEe4.text = Helper.getDate(fw.data[3].ts.toLong())
                binding.tvEe5.text = Helper.getDate(fw.data[4].ts.toLong())

                binding.tvMmDd.text = Helper.getDay(fw.data[0].ts.toLong())
                binding.tvMmDd2.text = Helper.getDay(fw.data[1].ts.toLong())
                binding.tvMmDd3.text = Helper.getDay(fw.data[2].ts.toLong())
                binding.tvMmDd4.text = Helper.getDay(fw.data[3].ts.toLong())
                binding.tvMmDd5.text = Helper.getDay(fw.data[4].ts.toLong())


                binding.tvDes.text=fw.data[0].weather.description
                binding.tvDes2.text=fw.data[1].weather.description
                binding.tvDes3.text=fw.data[2].weather.description
                binding.tvDes4.text=fw.data[3].weather.description
                binding.tvDes5.text=fw.data[4].weather.description


                Glide.with(activity)
                        .load(ApiUtils.ICON+fw.data[0].weather.icon+".png")
                        .into(binding.ivDes)
                Glide.with(activity)
                        .load(ApiUtils.ICON+fw.data[1].weather.icon+".png")
                        .into(binding.ivDes2)
                Glide.with(activity)
                        .load(ApiUtils.ICON+fw.data[2].weather.icon+".png")
                        .into(binding.ivDes3)
                Glide.with(activity)
                        .load(ApiUtils.ICON+fw.data[2].weather.icon+".png")
                        .into(binding.ivDes4)
                Glide.with(activity)
                        .load(ApiUtils.ICON+fw.data[2].weather.icon+".png")
                        .into(binding.ivDes5)


                val arr: IntArray = intArrayOf(fw.data[0].max_temp.toInt(), fw.data[1].max_temp.toInt(), fw.data[2].max_temp.toInt()
                        , fw.data[3].max_temp.toInt(), fw.data[4].max_temp.toInt())
                binding.lineChar.setTempDay(arr)
                binding.lineChar.invalidate()

                val arrNight: IntArray = intArrayOf(fw.data[0].min_temp.toInt(), fw.data[1].min_temp.toInt(), fw.data[2].min_temp.toInt()
                        , fw.data[3].min_temp.toInt(), fw.data[4].min_temp.toInt())
                binding.lineCharNight.setTempDay(arrNight)
                binding.lineCharNight.invalidate()






//                binding.tvTempMinMax.text = fw.data[0].max_temp.toInt().toString() + "/" + fw.data[0].low_temp.toInt().toString()
//                binding.tvTempMinMax1.text = fw.data[1].max_temp.toInt().toString() + "/" + fw.data[1].low_temp.toInt().toString()
//                binding.tvTempMinMax2.text = fw.data[2].max_temp.toInt().toString() + "/" + fw.data[2].low_temp.toInt().toString()

                Log.e("TAG", "hi")

            }

        })
    }

}