package com.example.controlcenter.scenes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
                startService(intent)
            } else {
                stopService(intent)
            }
        }
    }
}
