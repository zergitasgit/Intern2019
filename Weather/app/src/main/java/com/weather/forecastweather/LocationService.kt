package com.weather.forecastweather

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.widget.Toast


class LocationService(private var context: Context) : Service(), LocationListener {
    override fun onLocationChanged(p0: Location?) {

    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }

    var isGPSEnabled = false
    // flag for network status
    var isNetworkEnabled = false
    // flag for GPS status
    var canGetLocation = false
    var location: Location? = null // location
    var latitude: Double = 0.toDouble() // latitude
    var longitude: Double = 0.toDouble() // longitude
    // The minimum distance to change Updates in meters
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters
    // The minimum time between updates in milliseconds
    private val MIN_TIME_BW_UPDATES = 5000 // 2 seconds
    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null
    override fun onBind(intent: Intent): IBinder? {
       return null
    }
    init {
        get()
    }
    @SuppressLint("MissingPermission")
    fun get(): Location? {
        try {
            locationManager = context
                    .getSystemService(Context.LOCATION_SERVICE) as LocationManager
            // getting GPS status
            isGPSEnabled = locationManager!!
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)
            // getting network status
            isNetworkEnabled = locationManager!!
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                 Log.e("Network-GPS", "Disable")
            } else {
                this.canGetLocation = true
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager!!.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            5000,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                    // Log.e(“Network”, “Network”);
                    if (locationManager != null) {
                        location = locationManager!!
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                } else
                // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager!!.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    5000,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                            //Log.e(“GPS Enabled”, “GPS Enabled”);
                            if (locationManager != null) {
                                location = locationManager!!
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                if (location != null) {
                                    latitude = location!!.latitude
                                    longitude = location!!.longitude
                                }
                            }
                        }
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return location
    }
    fun getLat(): Double {
        if (location != null) {
            latitude = location!!.getLatitude()
        }
        return latitude
    }

    fun getLon(): Double {
        if (location != null) {
            longitude = location!!.getLongitude()
        }
        return longitude
    }

    fun canGetLocation(): Boolean {
        return this.canGetLocation
    }

    fun showDialog(){
       Toast.makeText(context,"Không có kết nối",Toast.LENGTH_SHORT).show()
    }
}
