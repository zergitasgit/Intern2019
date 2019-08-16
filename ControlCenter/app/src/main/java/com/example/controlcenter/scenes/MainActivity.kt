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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        onControl()
        changeSize()
        changePositon()
        PermissionAPI()
    }

    private fun changePositon() {
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
        val myToast = Toast.makeText(
            this,
            "Hãy tắt/mở lại nút để xem sự thay đổi",
            Toast.LENGTH_SHORT
        )
        rb_ben_trai.setOnClickListener {
            Utils.setPosition(this, 1)
            myToast.show()
        }
        rb_ben_phai.setOnClickListener {
            Utils.setPosition(this, 2)
            myToast.show()
        }
        rb_ben_duoi.setOnClickListener {
            Utils.setPosition(this, 3)
            myToast.show()
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

    private fun onControl() {
        val intent: Intent = Intent(this, ControlCenterService::class.java)
        sw_control_center.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                startService(intent)
            } else {
                stopService(intent)
            }
        }
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


