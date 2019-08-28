package com.os13lockscreen.ilockscreenos13.screens.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.os13lockscreen.ilockscreenos13.R
import com.os13lockscreen.ilockscreenos13.screens.main.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
//        Glide.with(this).load(R.drawable.splash).into(img_splash)
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
