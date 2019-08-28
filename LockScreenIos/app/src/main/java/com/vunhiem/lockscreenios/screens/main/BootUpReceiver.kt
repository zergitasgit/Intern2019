package com.vunhiem.lockscreenios.screens.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootUpReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, MainActivity::class.java)  //MyActivity can be anything which you want to start on bootup...
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    }

}