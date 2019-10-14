package hieusenpaj.com.weather.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
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
                  
                    lo = Add(String.format("%.2f",locationNetwork!!.latitude).toDouble() ,
                            String.format("%.2f",locationNetwork!!.longitude).toDouble())
                }


            } else {
                lo = Add(0.0, 0.0)
            }
            return lo

        }

    }


}