package com.example.controlcenter.scenes

import android.content.Intent
import android.net.Uri
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
        swControlCenter()
    }

    private fun swControlCenter() {
        val intent: Intent = Intent(this, ControlCenterService::class.java)
        sw_control_center.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    val intent: Intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)

                }
                startService(intent)
            } else {
                stopService(intent)
            }
        }
    }
}
