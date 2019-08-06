package com.vunhiem.lockscreenios.screens.main

import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.vunhiem.lockscreenios.service.MyService
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.vunhiem.lockscreenios.R.layout.activity_main)
        switch()
        disableLockSreenSystem()

    }

    fun switch() {
        switch_main.setOnCheckedChangeListener(this)

    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        if (p1) {
            val intent = Intent(this, MyService::class.java)
            startService(intent)
        } else {
            val intent = Intent(this, MyService::class.java)
            stopService(intent)
        }
    }

    fun disableLockSreenSystem() {
        rl_cancle_lock.setOnClickListener {
            val intent = Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD)
            startActivity(intent)
        }
    }

}
