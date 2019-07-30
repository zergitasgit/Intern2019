package com.example.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.Toast

class BoundService : Service() {

    override fun onCreate() {
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show()
        super.onCreate()

    }

    override fun onBind(p0: Intent?): IBinder? {
        Toast.makeText(this, "onBind", Toast.LENGTH_SHORT).show()
        return null


    }

    override fun onUnbind(intent: Intent?): Boolean {
        Toast.makeText(this, "onUnbind", Toast.LENGTH_SHORT).show()
        return super.onUnbind(intent)


    }

    override fun onDestroy() {
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show()
        super.onDestroy()

    }
    inner class BinderExample : Binder() {
        internal val service: BoundService
            get() = this@BoundService
    }


}