package com.example.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class PlaySongService : Service() {

    override fun onBind(intent: Intent): IBinder {
        return null;
    }
}
