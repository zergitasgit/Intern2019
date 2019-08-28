package com.os13lockscreen.ilockscreenos13.screens.main

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.ibikenavigationkotlin.utils.AppConfig
import com.os13lockscreen.ilockscreenos13.screens.password.NewPassword
import com.os13lockscreen.ilockscreenos13.screens.password.PasswordAuthentic
import com.os13lockscreen.ilockscreenos13.screens.privacy.PrivacyActivity
import com.os13lockscreen.ilockscreenos13.screens.wallpaper.Wallpaper
import com.os13lockscreen.ilockscreenos13.service.NotificationService
import com.os13lockscreen.ilockscreenos13.utils.Ads
import kotlinx.android.synthetic.main.activity_main.*

import java.util.*

import com.znitenda.ZAndroidSDK


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(com.os13lockscreen.ilockscreenos13.R.layout.activity_main)

        checkAndRequestPermissions()

        val intent = Intent(this@MainActivity, NotificationService::class.java)
        startService(intent)
        loadData()
        oncClick()
        switch()
        checkNotifi()

        Ads.loadBannerAds(this, layout_ads)

        ZAndroidSDK.init(this)
    }

    private fun checkNotifi() {

        val h = Handler()
        h.post(
            object : Runnable {
                override fun run() {

                    val cn = ComponentName(this@MainActivity, NotificationService::class.java)
                    val flat = Settings.Secure.getString(
                        this@MainActivity.contentResolver,
                        "enabled_notification_listeners"
                    )
                    val enabled = flat != null && flat.contains(cn.flattenToString())
                    if (enabled) {
                img_swith_noti.setImageResource(com.os13lockscreen.ilockscreenos13.R.drawable.ic_ic_on_2)
            } else {
                img_swith_noti.setImageResource(com.os13lockscreen.ilockscreenos13.R.drawable.ic_ic_off_2)
            }
                    h.postDelayed(this, 300)
                }
            })

//            val cn = ComponentName(this, NotificationService::class.java)
//            val flat = Settings.Secure.getString(
//                this.getContentResolver(),
//                "enabled_notification_listeners"
////            )
//            Log.i("notixxx", "hihi$flat")
//        val handler = Handler()
//        handler.postDelayed({
//            val enabled = flat != null && flat.contains(cn.flattenToString())
//            if (enabled == true) {
//                img_swith_noti.setImageResource(com.vunhiem.lockscreenios.R.drawable.ic_ic_on_2)
//            } else {
//                img_swith_noti.setImageResource(com.vunhiem.lockscreenios.R.drawable.ic_ic_off_2)
//            }
//        },500)

    }

    private fun loadData() {
        Glide.with(this).load(com.os13lockscreen.ilockscreenos13.R.drawable.backgroundlock_1080)
            .into(img_main)

    }


    private fun oncClick() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this@MainActivity)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 1234)
            }
        }
        rl_password.setOnClickListener {
            val x = AppConfig.getPassord(this@MainActivity)
            if (x == null) {
                val intent = Intent(this, NewPassword::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, PasswordAuthentic::class.java)
                startActivity(intent)
            }
        }
        rl_wallpaer.setOnClickListener {
            val intent = Intent(this, Wallpaper::class.java)
            startActivity(intent)
        }
        rl_noti.setOnClickListener {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
            Toast.makeText(this, "Please change notification access for iLockScreen OS 13",Toast.LENGTH_LONG).show()
        }
        rl_feedback.setOnClickListener {
            val uri = Uri.parse("market://details?id=" + applicationContext.packageName)
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + applicationContext.packageName)))
            }
        }
        rl_share.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                Uri.parse("https://play.google.com/store/apps/details?id=" + applicationContext.packageName))
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
        rl_privacy.setOnClickListener {
            val intent = Intent(this, PrivacyActivity::class.java)
            startActivity(intent)
        }
        rl_cancle_lock.setOnClickListener {
            val intent = Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD)
            startActivity(intent)
            Toast.makeText(this, "Please change screen lock type to 'None'",Toast.LENGTH_LONG).show()
        }
    }

    private fun switch() {
        val switchMainStatus: Boolean? = AppConfig.getLock(this)
        btn_main.isChecked = switchMainStatus!!
        btn_main.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppConfig.setLock(isChecked, this@MainActivity)

            } else {
                AppConfig.setLock(isChecked, this@MainActivity)
            }
        }

    }


    private fun checkAndRequestPermissions() {

        val permissions = arrayOf(
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE


        )
        val listPermissionsNeeded = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionsNeeded.add(permission)
            }
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), 1)
        }
    }

}
