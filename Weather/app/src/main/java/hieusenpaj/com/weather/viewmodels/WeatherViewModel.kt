package hieusenpaj.com.weather.viewmodels

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.baoyz.widget.PullRefreshLayout
import com.baoyz.widget.SmartisanDrawable
import com.bumptech.glide.Glide
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.api.ApiServices
import hieusenpaj.com.weather.api.ApiUtils
import hieusenpaj.com.weather.data.DataCity
import hieusenpaj.com.weather.databinding.ItemViewPagerBinding
import hieusenpaj.com.weather.db.DBBackground
import hieusenpaj.com.weather.helper.Helper
import hieusenpaj.com.weather.models.BackGround
import hieusenpaj.com.weather.models.City
import hieusenpaj.com.weather.models.current.CurrentWeather
import hieusenpaj.com.weather.models.forecastDay.ForecastDay
import hieusenpaj.com.weather.views.ForecastActivity
import hieusenpaj.com.weather.views.ListCityActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.util.*
import kotlin.collections.ArrayList


class WeatherViewModel(private var activity: Activity, private var binding: ItemViewPagerBinding, private var arrayList: ArrayList<City>,
                       private var lat: Double, private var lon: Double, private var pos: Int) : Observable() {


    var apiServices: ApiServices? = null
    //    var temp: String? = null
    var location: String? = null
    private var sha: SharedPreferences? = null
    private var edit: SharedPreferences.Editor? = null

    //    private val dbBg = DBBackground(activity)
    private var progressDialog: ProgressDialog? = null


    private var arrBg = ArrayList<BackGround>()


    private var temp: Int? = null
    private var desCu: String? = null
    private var visibility: Int? = null
    private var uv: Int? = null
    private var humidity: Int? = null
    private var windSpeed: Int? = null
    private var ahi: Int? = null
    private var srArr: List<String>? = null
    private var sunriseHour: Int? = null
    private var sunriseMinute: Int? = null
    private var ssArrSet: List<String>? = null
    private var sunsetHour: Int? = null
    private var sunsetMinute: Int? = null
    private var timeZone: String? = null
    private var des: String? = null
    private var des2: String? = null
    private var des1: String? = null
    private var ts1: Long? = null
    private var ts2: Long? = null
    private var max1: String? = null
    private var max2: String? = null
    private var max3: String? = null
    private var low1: String? = null
    private var low2: String? = null
    private var low3: String? = null
    private var icon1: String? = null
    private var icon2: String? = null
    private var icon3: String? = null
    private var tvSunrise: String? = null
    private var tvSunset: String? = null
    private var code: Int? = null
    private var codeF: Int? = null
    private var codeF1: Int? = null
    private var codeF2: Int? = null
    private var drawableF: Drawable? = null
    private var drawableF1: Drawable? = null
    private var drawableF2: Drawable? = null
    private var arrF = ArrayList<BackGround>()
    private var imageDay: ByteArray? = null
    private var drawable: Drawable? = null
    private val mTouchPosition: Float? = null
    private val mReleasePosition: Float? = null


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

//        val network = Helper.getLocation(activity)


        getWeatherCurrent(lat, lon, true)
        getWeatherForecast(lat, lon)
        binding.ivSetting.setOnClickListener {
            val intent = Intent(activity, ListCityActivity::class.java)
            intent.putExtra("bg", arrBg[0].imageDay)
            activity.startActivity(intent)
        }

//        scroll()
        refeshView()
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        binding.tvC.setOnClickListener {
            edit!!.putBoolean("F", false)
            edit!!.apply()
            changeTemp()
            val intent = Intent("RELOAD")
            intent.putExtra("position", pos)
            activity.sendBroadcast(intent)
        }
        binding.tvF.setOnClickListener {
            edit!!.putBoolean("F", true)
            edit!!.apply()
            changeTemp()
            val intent = Intent("RELOAD")
            intent.putExtra("position", pos)
            activity.sendBroadcast(intent)

        }
        if (sha!!.getBoolean("F", false)) {
            binding.tvC.setTextColor(Color.parseColor("#FFFFFF"))
            binding.tvF.setTextColor(Color.parseColor("#000000"))
        } else {
            binding.tvC.setTextColor(Color.parseColor("#000000"))
            binding.tvF.setTextColor(Color.parseColor("#FFFFFF"))
        }
        binding.ivMenu.setOnClickListener {
            showPopup(it)
        }

//

    }


    private fun getWeatherCurrent(lat: Double, lon: Double, isShowPo: Boolean) {
        if (isShowPo) {
            progressDialog = ProgressDialog(activity)
            progressDialog!!.setCancelable(false)
            progressDialog!!.setIndeterminate(false)
            progressDialog!!.setMessage("Loading...")
            progressDialog!!.setMax(100)
            progressDialog!!.show()
        }
        apiServices!!.getCurrentWeather(lat, lon, ApiUtils.KEY).enqueue(object : Callback<CurrentWeather> {
            @SuppressLint("SetTextI18n")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<CurrentWeather>, response: Response<CurrentWeather>) {
                Load(response, isShowPo).execute()

            }

            override fun onFailure(call: Call<CurrentWeather>, t: Throwable) {
                Log.e("TAG", t.message)

            }

        })


    }

    private fun getWeatherForecast(lat: Double, lon: Double) {
        apiServices!!.getForecastWeather(lat, lon, ApiUtils.KEY, "3").enqueue(object : Callback<ForecastDay> {
            override fun onFailure(call: Call<ForecastDay>?, t: Throwable?) {
                Log.e("TAG", t?.message)
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ForecastDay>?, response: Response<ForecastDay>?) {
                val fw = response!!.body()
                des = fw.data[0].weather.description
                des1 = fw.data[1].weather.description
                des2 = fw.data[2].weather.description
                ts1 = fw.data[1].ts.toLong()
                ts2 = fw.data[2].ts.toLong()



                max1 = fw.data[0].max_temp.toInt().toString()
                low1 = fw.data[0].low_temp.toInt().toString()
                max2 = fw.data[1].max_temp.toInt().toString()
                low2 = fw.data[1].low_temp.toInt().toString()
                max3 = fw.data[2].max_temp.toInt().toString()
                low3 = fw.data[2].low_temp.toInt().toString()


                codeF = fw.data[0].weather.code
                codeF1 = fw.data[1].weather.code
                codeF2 = fw.data[2].weather.code


                drawableF = Helper.getIcon(codeF!!, activity, fw.timezone)
                drawableF1 = Helper.getIcon(codeF1!!, activity, fw.timezone)
                drawableF2 = Helper.getIcon(codeF2!!, activity, fw.timezone)

//                icon1 = fw.data[0].weather.icon
//                icon2 = fw.data[1].weather.icon
//                icon3 = fw.data[2].weather.icon
                if(Locale.getDefault().language.equals("vi")){

                    binding.tvDayStatus.text = activity.resources.getString(R.string.today)+"/" + DataCity.getLanguage(activity,codeF.toString()).vn
                    binding.tvDayStatus1.text = Helper.getDate(ts1!!) + "/" + DataCity.getLanguage(activity,codeF1.toString()).vn
                    binding.tvDayStatus2.text = Helper.getDate(ts2!!) + "/" + DataCity.getLanguage(activity,codeF2.toString()).vn
                }else {
                    binding.tvDayStatus.text = activity.resources.getString(R.string.today) +"/"+ des
                    binding.tvDayStatus1.text = Helper.getDate(ts1!!) + "/" + des1
                    binding.tvDayStatus2.text = Helper.getDate(ts2!!) + "/" + des2
                }


                if (!sha!!.getBoolean("F", false)) {
                    binding.tvTempMinMax.text = max1 + "/" + low1
                    binding.tvTempMinMax1.text = max2 + "/" + low2
                    binding.tvTempMinMax2.text = max3 + "/" + low3
                } else {
                    binding.tvTempMinMax.text = Helper.convertCtoF(low1!!.toInt()).toString() + "/" + Helper.convertCtoF(max1!!.toInt()).toString()
                    binding.tvTempMinMax1.text = Helper.convertCtoF(low2!!.toInt()).toString() + "/" + Helper.convertCtoF(max2!!.toInt()).toString()
                    binding.tvTempMinMax2.text = Helper.convertCtoF(low3!!.toInt()).toString() + "/" + Helper.convertCtoF(max3!!.toInt()).toString()
                }



                Glide.with(activity)
                        .load(drawableF)
                        .into(binding.ivStatus)
                Glide.with(activity)
                        .load(drawableF1)
                        .into(binding.ivStatus1)
                Glide.with(activity)
                        .load(drawableF2)
                        .into(binding.ivStatus2)

            }

        })
    }


//    private fun refreshSSV(timeZone: String, sunriseHour: Int, sunriseMinute: Int, sunsetHour: Int, sunsetMinute: Int) {
//        binding.ssv.setSunriseTime(Time(sunriseHour, sunriseMinute))
//        binding.ssv.setSunsetTime(Time(sunsetHour, sunsetMinute))
//        binding.ssv.startAnimate(timeZone)
//    }

    private fun refeshView() {
        binding.swipeRefreshLayout.setOnRefreshListener(PullRefreshLayout.OnRefreshListener {
            //            var lat = Helper.getLocation(activity)?.lat
//            var lon = Helper.getLocation(activity)?.lon

            getWeatherCurrent(lat, lon, false)
            getWeatherForecast(lat, lon)


        })
//        binding.swipeRefreshLayout.setColorSchemeColors(Color.GRAY)
        binding.swipeRefreshLayout.setRefreshDrawable(SmartisanDrawable(activity, binding.swipeRefreshLayout))
        binding.swipeRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL)
    }


    @SuppressLint("SetTextI18n")
    fun changeTemp() {
        if (sha!!.getBoolean("F", false)) {
            binding.tvTemp.text = Helper.convertCtoF(temp!!).toString()
            binding.tvTempSmall.text = "°F"
            binding.tvTempMinMax.text = Helper.convertCtoF(low1!!.toInt()).toString() + "/" + Helper.convertCtoF(max1!!.toInt()).toString()
            binding.tvTempMinMax1.text = Helper.convertCtoF(low2!!.toInt()).toString() + "/" + Helper.convertCtoF(max2!!.toInt()).toString()
            binding.tvTempMinMax2.text = Helper.convertCtoF(low3!!.toInt()).toString() + "/" + Helper.convertCtoF(max3!!.toInt()).toString()

            binding.tvC.setTextColor(Color.parseColor("#FFFFFF"))
            binding.tvF.setTextColor(Color.parseColor("#000000"))

        } else {
            binding.tvTemp.text = temp.toString()
            binding.tvTempSmall.text = "°C"
            binding.tvTempMinMax.text = max1 + "/" + low1
            binding.tvTempMinMax1.text = max2 + "/" + low2
            binding.tvTempMinMax2.text = max3 + "/" + low3
            binding.tvC.setTextColor(Color.parseColor("#000000"))
            binding.tvF.setTextColor(Color.parseColor("#FFFFFF"))
        }
    }


    inner class Load(private var response: Response<CurrentWeather>, private var isShowPo: Boolean) : AsyncTask<Void, Int, Void>() {

        override fun doInBackground(vararg void: Void): Void? {
            //copy file to new folder
            val currentWeather = response.body()
            temp = (currentWeather.data[0].temp.toInt())
            location = currentWeather.data[0].city_name
            desCu = currentWeather.data[0].weather.description
            visibility = currentWeather.data[0].vis.toInt()
            uv = currentWeather.data[0].uv.toInt()
            humidity = currentWeather.data[0].rh
            windSpeed = currentWeather.data[0].wind_spd.toInt()
            ahi = currentWeather.data[0].aqi
            code = currentWeather.data[0].weather.code
            timeZone = currentWeather.data[0].timezone



            arrBg = DataCity.getBg(activity, code!!)
            if (Helper.getCurrentTimeZone(timeZone!!) < 18) {
                drawable = Drawable.createFromStream(activity.assets.open("bg/" + arrBg[0].imageDay + ".jpg"), null)
            } else {
                drawable = Drawable.createFromStream(activity.assets.open("bg/" + arrBg[0].imageNight + ".jpg"),
                        null)
            }

            ssArrSet = Helper.getTime(currentWeather.data[0].sunset, currentWeather.data[0].timezone).split(":")
            sunsetHour = Integer.valueOf(ssArrSet!![0]) + 12
            sunsetMinute = Integer.valueOf(ssArrSet!![1])

            tvSunrise = Helper.getTime(currentWeather.data[0].sunrise, timeZone!!)
            tvSunset = sunsetHour.toString() + ":" + sunsetMinute.toString()




            return null


        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: Void?) {
            //
            // Hide ProgressDialog here
            if (progressDialog != null && progressDialog!!.isShowing() && progressDialog!!.isIndeterminate()) {
                progressDialog!!.dismiss()

            }
            if (isShowPo) {

            } else {
                binding.swipeRefreshLayout.setRefreshing(false)
            }

            if (!sha!!.getBoolean("F", false)) {
                binding.tvTemp.text = temp.toString()
                binding.tvTempSmall.text = "°C"
            } else {
                binding.tvTemp.text = Helper.convertCtoF(temp!!).toString()
                binding.tvTempSmall.text = "°F"
            }
//                binding.tvLocal.text = location
            binding.tvLocal.text = location
            binding.tvHumidityContent.text = humidity.toString() + "%"
            binding.tvVisibilityContent.text = visibility.toString() + "Km"
            binding.tvUvContent.text = uv.toString()
            binding.tvWindSpeedContent.text = windSpeed.toString() + "m/s"
            binding.tvAhi.text = "Aqi : " + ahi
            if(Locale.getDefault().language.equals("vi")){
                binding.tvStatus.text = DataCity.getLanguage(activity,code.toString()).vn

            }else{
                binding.tvStatus.text = desCu.toString()

            }
            if (Helper.getCurrentTimeZone(timeZone!!) < 18) {
                binding.llForecast.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_view))
                binding.llContent.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_view))
                binding.llSun.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_view))

            }else{
                binding.llForecast.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_view_night))
                binding.llContent.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_view_night))
                binding.llSun.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_view_night))
            }

            binding.rootView.setBackground(drawable)

//                    binding.tvWindSpeed.text = getTime(currentWeather.data[0].sunset)

//            refreshSSV(timeZone!!, sunriseHour!!, sunriseMinute!!, sunsetHour!!, sunsetMinute!!)

            binding.tvSunrise.text = tvSunrise
            binding.tvSunset.text = tvSunset
            //

            binding.tvForecast.setOnClickListener {
                val intent = Intent(activity, ForecastActivity::class.java)
                intent.putExtra("lat", lat)
                intent.putExtra("lon", lon)
                intent.putExtra("bg", arrBg[0].imageDay)
                activity.startActivity(intent)
            }
        }

        override fun onPreExecute() {

        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            if (isShowPo) {
                progressDialog!!.setProgress(values[0]!!.inv())
            }
        }


    }

    private fun showPopup(view: View) {
        var popup: PopupMenu? = null;
        popup = PopupMenu(activity, view)
        popup.inflate(R.menu.menu_main)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.share -> {

                }
                R.id.rate -> {

                }

            }

            true
        })

        popup.show()
    }
}