package com.example.controlcenter.scenes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.Switch
import android.widget.Toast
import com.example.controlcenter.R
import com.example.controlcenter.services.ControlCenterService
import com.example.controlcenter.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val intent: Intent = Intent(this, ControlCenterService::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissionNotification(intent)
        changeSize(intent)
        changePositon(intent)
        PermissionAPI()
    }



    private fun checkPermissionNotification(intent: Intent) {
        sb_size.progress = Utils.getSize(this)+1
        if (Settings.Secure.getString(
                this.getContentResolver(),
                "enabled_notification_listeners"
            ).contains(getApplicationContext().getPackageName())
        ) {
            if (Utils.getCheckControl(this) == 0) {
                sw_control_center.isChecked = true
            } else {
                sw_control_center.isChecked = false
            }

            sw_control_center.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    startService(intent)
                    Utils.setCheckControl(this, 0)

                } else {
                    stopService(intent)
                    Utils.setCheckControl(this, 1)

                }
            }
        } else {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            getApplicationContext().startActivity(intent)
        }
    }

    private fun changePositon(intent: Intent) {
        var i = Utils.getPosition(this)
        if (i == 2) {
            rb_ben_phai.isChecked = true
        }
        if (i == 1) {
            rb_ben_trai.isChecked = true
        }
        if (i == 3) {
            rb_ben_duoi.isChecked = true
        }
        rb_ben_trai.setOnClickListener {
            Utils.setPosition(this, 1)
            if (Utils.getCheckControl(this) == 0) {
                stopService(intent)
                startService(intent)
            }

        }
        rb_ben_phai.setOnClickListener {
            Utils.setPosition(this, 2)
            if (Utils.getCheckControl(this) == 0) {
                stopService(intent)
                startService(intent)
            }
        }
        rb_ben_duoi.setOnClickListener {
            Utils.setPosition(this, 3)
            if (Utils.getCheckControl(this) == 0) {
                stopService(intent)
                startService(intent)
            }
        }
    }

    private fun changeSize(intent: Intent) {
        sb_size.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                Utils.SetSize(applicationContext, i)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (Utils.getCheckControl(applicationContext) == 0) {
                    stopService(intent)
                    startService(intent)
                }
            }
        })
        sb_size.progress = Utils.getSize(this)
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

}


