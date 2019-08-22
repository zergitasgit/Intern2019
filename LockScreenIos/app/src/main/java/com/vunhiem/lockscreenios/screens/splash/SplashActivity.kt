package com.vunhiem.lockscreenios.screens.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vunhiem.lockscreenios.R
import com.vunhiem.lockscreenios.screens.main.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        splashScreen()
    }

    fun splashScreen() {
        val handler = android.os.Handler()
        handler.postDelayed({ screenRouter() }, 1000)
    }

    private fun screenRouter() {
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
