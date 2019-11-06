package hieusenpaj.com.weather.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import hieusenpaj.com.weather.data.DataCity
import hieusenpaj.com.weather.models.Add
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class Helper {
    companion object {

        fun getHeightScreen(activity: Activity): Int {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenHeight = displayMetrics.heightPixels
            val scale = displayMetrics.density
///       screenWidth = displayMetrics.widthPixels
            return screenHeight + (56 * scale + 0.5f).toInt()

        }


        @SuppressLint("MissingPermission")
        fun getLocation(activity: Activity): Add? {
            var lo: Add? = null
            var locationNetwork: Location? = null
            val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (hasNetwork) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0F, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationNetwork = location
//                            lo = Add(location.latitude , location.longitude)


                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
                if (locationNetwork != null) {

                    lo = Add( locationNetwork!!.latitude,
                            locationNetwork!!.longitude)
                }


            } else {
                lo = null
            }

            return lo

        }


        @SuppressLint("SimpleDateFormat")
        fun getDate(dt: Long): String {


            val date = Date(dt * 1000L)
            val simpleDateFormat = SimpleDateFormat("EEEE")
            return simpleDateFormat.format(date)

        }
        fun getDay(dt: Long) :String{
            val date = Date(dt * 1000L)
            val simpleDateFormat = SimpleDateFormat("MM/dd")
            return simpleDateFormat.format(date)
        }

        @SuppressLint("SimpleDateFormat")
        fun getTime(string: String,timeZone: String): String {
            var time:String?=null
            val formatter = SimpleDateFormat("hh:mm")
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val value = formatter.parse(string)
            val dateFormatter = SimpleDateFormat("hh:mm") //this format changeable
            dateFormatter.timeZone = TimeZone.getTimeZone(timeZone)
            time = dateFormatter.format(value)
            return time




//

        }

        fun getCurrentTimeZone(): String {
            val tz = Calendar.getInstance().timeZone
//        System.out.println(tz.displayName)
            return tz.id
        }
        fun convertToPx(activity: Activity,dp: Int): Int {
            // Get the screen's density scale
            val scale = activity.resources.displayMetrics.density
            // Convert the dps to pixels, based on density scale
            return (dp * scale + 0.5f).toInt()
        }
        fun convertCtoF(value:Int):Int{
            val F = value * (9 / 5) + 32
            return F
        }
        fun convertFtoC(value:Int):Int{
            val C = ((value-5) * 5) / 9
            return C
        }
//        fun getIcon(code:Int,activity: Activity):Drawable{
//            val arr = DataCity.getIcon(activity, code)
//            val drawable = BitmapDrawable(activity.getResources(), BitmapFactory.decodeByteArray(arr[0].icDay,
//                    0, arr[0].icNight!!.size))
//            return drawable
//        }


    }


}