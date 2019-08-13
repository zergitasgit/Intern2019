package com.example.controlcenter.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.Settings
import android.content.ContentResolver
import android.accounts.Account
import android.accounts.AccountManager
import android.bluetooth.BluetoothAdapter
import android.content.res.Configuration
import androidx.core.content.contentValuesOf
import android.media.AudioManager
import androidx.core.content.ContextCompat.getSystemService


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

    fun checkRotate(context: Context): Int {
        if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            return 1
        } else (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        return 0
    }

    fun checkAudio(context: Context): Int {
        var audioManager: AudioManager
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT) {
            return 1
        }

        return 0


    }

}


