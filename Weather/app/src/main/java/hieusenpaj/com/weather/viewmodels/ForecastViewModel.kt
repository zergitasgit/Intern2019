package hieusenpaj.com.weather.viewmodels

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.api.ApiServices
import hieusenpaj.com.weather.api.ApiUtils
import hieusenpaj.com.weather.data.DataCity
import hieusenpaj.com.weather.databinding.ActivityForecastBinding
import hieusenpaj.com.weather.helper.Helper
import hieusenpaj.com.weather.models.forecastDay.ForecastDay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ForecastViewModel(private var activity: Activity, private var binding: ActivityForecastBinding,
                        private var lat: Double, private var lon: Double, bg: String) : Observable() {
    var apiServices: ApiServices? = null
    var temp: String? = null
    var timeZone: String? = null
    private var code: Int? = null
    private var code1: Int? = null
    private var code2: Int? = null
    private var code3: Int? = null
    private var code4: Int? = null
    private var sha: SharedPreferences? = null
    private var edit: SharedPreferences.Editor? = null
    private val arrKey = ArrayList<String>()
    var checkkey = false

    init {
        sha = activity.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        edit = sha!!.edit()
        apiServices = ApiUtils.getApiService()
        arrKey.add(ApiUtils.KEY)
        arrKey.add(ApiUtils.KEY2)
        arrKey.add(ApiUtils.KEY1)
        arrKey.add(ApiUtils.KEY3)
//        var lat = Helper.getLocation(activity)?.lat
//        var lon = Helper.getLocation(activity)?.lon
        val drawable = Drawable.createFromStream(activity.assets.open("bg/" + bg + ".jpg"), null)
        binding.llForecast.setBackground(drawable)
        getWeatherForecast(lat, lon)

        binding.ivBack.setOnClickListener {
            activity.onBackPressed()
        }

    }

    private fun getWeatherForecast(lat: Double, lon: Double) {
        for (i in arrKey) {

            apiServices!!.getForecastWeather(lat, lon, i, "5").enqueue(object : Callback<ForecastDay> {
                override fun onFailure(call: Call<ForecastDay>?, t: Throwable?) {
                    Log.e("TAG", t?.message)
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<ForecastDay>?, response: Response<ForecastDay>?) {
                    if (response!!.isSuccessful) {
                        if (!checkkey) {
                            val fw = response!!.body()
                            code = fw.data[0].weather.code
                            code1 = fw.data[1].weather.code
                            code2 = fw.data[2].weather.code
                            code3 = fw.data[3].weather.code
                            code4 = fw.data[4].weather.code
                            timeZone = fw.timezone

                            binding.tvEe.text = activity.resources.getString(R.string.today)
                            binding.tvEe2.text = Helper.getDate(fw.data[1].ts.toLong())
                            binding.tvEe3.text = Helper.getDate(fw.data[2].ts.toLong())
                            binding.tvEe4.text = Helper.getDate(fw.data[3].ts.toLong())
                            binding.tvEe5.text = Helper.getDate(fw.data[4].ts.toLong())

                            binding.tvMmDd.text = Helper.getDay(fw.data[0].ts.toLong())
                            binding.tvMmDd2.text = Helper.getDay(fw.data[1].ts.toLong())
                            binding.tvMmDd3.text = Helper.getDay(fw.data[2].ts.toLong())
                            binding.tvMmDd4.text = Helper.getDay(fw.data[3].ts.toLong())
                            binding.tvMmDd5.text = Helper.getDay(fw.data[4].ts.toLong())

                            if (Locale.getDefault().language.equals("vi")) {
                                binding.tvDes.text = DataCity.getLanguage(activity, code.toString()).vn
                                binding.tvDes2.text = DataCity.getLanguage(activity, code1.toString()).vn
                                binding.tvDes3.text = DataCity.getLanguage(activity, code2.toString()).vn
                                binding.tvDes4.text = DataCity.getLanguage(activity, code3.toString()).vn
                                binding.tvDes5.text = DataCity.getLanguage(activity, code4.toString()).vn
                            } else {
                                binding.tvDes.text = fw.data[0].weather.description
                                binding.tvDes2.text = fw.data[1].weather.description
                                binding.tvDes3.text = fw.data[2].weather.description
                                binding.tvDes4.text = fw.data[3].weather.description
                                binding.tvDes5.text = fw.data[4].weather.description
                            }



                            Glide.with(activity)
                                    .load(Helper.getIcon(code!!, activity, timeZone!!))
                                    .into(binding.ivDes)
                            Glide.with(activity)
                                    .load(Helper.getIcon(code1!!, activity, timeZone!!))
                                    .into(binding.ivDes2)
                            Glide.with(activity)
                                    .load(Helper.getIcon(code2!!, activity, timeZone!!))
                                    .into(binding.ivDes3)
                            Glide.with(activity)
                                    .load(Helper.getIcon(code3!!, activity, timeZone!!))
                                    .into(binding.ivDes4)
                            Glide.with(activity)
                                    .load(Helper.getIcon(code4!!, activity, timeZone!!))
                                    .into(binding.ivDes5)
                            var arr: IntArray? = null
                            var arrNight: IntArray? = null
                            if (sha!!.getBoolean("F", false)) {
                                arr = intArrayOf(Helper.convertCtoF(fw.data[0].max_temp.toInt()), Helper.convertCtoF(fw.data[1].max_temp.toInt())
                                        , Helper.convertCtoF(fw.data[2].max_temp.toInt())
                                        , Helper.convertCtoF(fw.data[3].max_temp.toInt()), Helper.convertCtoF(fw.data[4].max_temp.toInt()))

                                arrNight = intArrayOf(Helper.convertCtoF(fw.data[0].min_temp.toInt()), Helper.convertCtoF(fw.data[1].min_temp.toInt()),
                                        Helper.convertCtoF(fw.data[2].min_temp.toInt())
                                        , Helper.convertCtoF(fw.data[3].min_temp.toInt()), Helper.convertCtoF(fw.data[4].min_temp.toInt()))
                            } else {
                                arr = intArrayOf(fw.data[0].max_temp.toInt(), fw.data[1].max_temp.toInt(), fw.data[2].max_temp.toInt()
                                        , fw.data[3].max_temp.toInt(), fw.data[4].max_temp.toInt())
                                arrNight = intArrayOf(fw.data[0].min_temp.toInt(), fw.data[1].min_temp.toInt(), fw.data[2].min_temp.toInt()
                                        , fw.data[3].min_temp.toInt(), fw.data[4].min_temp.toInt())
                            }

                            binding.lineChar.setTempDay(arr)
                            binding.lineChar.invalidate()

                            binding.lineCharNight.setTempDay(arrNight)
                            binding.lineCharNight.invalidate()


//                binding.tvTempMinMax.text = fw.data[0].max_temp.toInt().toString() + "/" + fw.data[0].low_temp.toInt().toString()
//                binding.tvTempMinMax1.text = fw.data[1].max_temp.toInt().toString() + "/" + fw.data[1].low_temp.toInt().toString()
//                binding.tvTempMinMax2.text = fw.data[2].max_temp.toInt().toString() + "/" + fw.data[2].low_temp.toInt().toString()

                            Log.e("TAG", "hi")
                            checkkey = true
                        }
                    } else {
                        checkkey = false
                    }
                }

            })

        }
    }

}