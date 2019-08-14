package com.example.controlcenter.scenes

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.example.controlcenter.R
import com.example.controlcenter.services.ControlCenterService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PermissionAPI()
        swControlCenter()
    }

    private fun PermissionAPI() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this@MainActivity)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 1234)
            }
        }
    }

    private fun swControlCenter() {
        val intent: Intent = Intent(this, ControlCenterService::class.java)
        sw_control_center.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                startService(intent)
            } else {
                stopService(intent)
            }
        }
    }
}

