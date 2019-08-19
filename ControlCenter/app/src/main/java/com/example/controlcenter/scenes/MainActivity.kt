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
        changeSize()
        changePositon(intent)
        PermissionAPI()
    }
    private fun checkPermissionNotification(intent: Intent) {
        if (Settings.Secure.getString(
                this.getContentResolver(),
                "enabled_notification_listeners"
            ).contains(getApplicationContext().getPackageName())
        ) {
            sw_control_center.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    startService(intent)
                } else {
                    stopService(intent)
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


        }
        rb_ben_phai.setOnClickListener {
            Utils.setPosition(this, 2)


        }
        rb_ben_duoi.setOnClickListener {
            Utils.setPosition(this, 3)


        }

    }

    private fun changeSize() {
        sb_size.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                Utils.SetSize(applicationContext, i)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val myToast = Toast.makeText(
                    applicationContext,
                    "Hãy tắt/mở lại nút để xem sự thay đổi",
                    Toast.LENGTH_SHORT
                )
                myToast.show()
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


