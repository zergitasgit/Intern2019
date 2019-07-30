package com.example.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import android.content.ComponentName
import android.widget.Toast
import android.os.IBinder
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init();
    }

    private fun init() {
        btn_start.setOnClickListener {
            val intent = Intent(this, StartService::class.java)
            startService(intent)

        }
        btn_stop.setOnClickListener {
            val intent = Intent(this, StartService::class.java)
            stopService(intent)
        }
        btn_bound_start.setOnClickListener {
            val intent = Intent(this, BoundService::class.java);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        }
    }

    val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {

        }

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {

        }

    }


}



