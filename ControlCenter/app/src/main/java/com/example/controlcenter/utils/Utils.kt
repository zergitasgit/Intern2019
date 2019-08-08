package com.example.controlcenter.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.Settings
import android.content.ContentResolver
import android.accounts.Account
import android.accounts.AccountManager
import android.bluetooth.BluetoothAdapter


object Utils {
    fun CheckWifi(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        return isConnected
    }

    fun CheckPlane(context: Context): Boolean {
        return Settings.System.getInt(
            context.getContentResolver(),
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) != 0
    }

    fun CheckSync(context: Context): Boolean {

        return true
    }

    fun CheckBluetooth(context: Context): Boolean {
        var mBtAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        var isOn: Boolean = mBtAdapter.isEnabled
        return isOn
    }
}