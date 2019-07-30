package com.example.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import android.R
import android.app.Notification
import android.app.PendingIntent


class StartService : Service() {
    private var notBuilder: NotificationCompat.Builder? = null
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    private val NOTIFICATION_ID:Int = 1

    private fun showForegroundNotification(contentText: String) {


        val showTaskIntent = Intent(applicationContext, MainActivity::class.java)
        showTaskIntent.action = Intent.ACTION_MAIN
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val contentIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            showTaskIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = Notification.Builder(applicationContext)
            .setContentTitle("Music Pro")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_menu_upload_you_tube)
            .setWhen(System.currentTimeMillis())
            .setContentIntent(contentIntent)
            .build()
        startForeground(NOTIFICATION_ID, notification)

    }

    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show()



    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "onStartCommand: ", Toast.LENGTH_SHORT).show()
        showForegroundNotification("Hay Trao Cho Anh")
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show()
    }


}