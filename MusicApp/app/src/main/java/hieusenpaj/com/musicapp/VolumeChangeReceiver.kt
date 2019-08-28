package hieusenpaj.com.musicapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class VolumeChangeReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
            val intent = Intent("BR_SOUND")
            p0?.sendBroadcast(intent)
        }
    }
}