package com.weather.forecastweather.views.base

import android.os.Build
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handlePermission()


    }

    abstract fun bindingView()
    private fun handlePermission() {
        val perms = arrayOf("android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"
                , "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE")
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(perms, 3)
        } else {
            bindingView()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            3/*200*/ -> {
                if (grantResults[0] == 0) {
                    bindingView()
                }

                return
            }
            else -> return
        }
    }


}