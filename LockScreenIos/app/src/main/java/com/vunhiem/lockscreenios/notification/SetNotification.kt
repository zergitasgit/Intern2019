package com.vunhiem.lockscreenios.notification

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.vunhiem.lockscreenios.R
import com.vunhiem.lockscreenios.model.Notification
import kotlinx.android.synthetic.main.activity_set_notification.*

class SetNotification : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_notification)
        onClick()

    }

    private fun onClick() {
        click.setOnClickListener {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }
        img_back.setOnClickListener {
            finish()
        }
    }


}
