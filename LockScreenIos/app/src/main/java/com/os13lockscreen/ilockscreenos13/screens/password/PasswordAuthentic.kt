package com.os13lockscreen.ilockscreenos13.screens.password

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ibikenavigationkotlin.utils.AppConfig
import com.os13lockscreen.ilockscreenos13.R
import com.os13lockscreen.ilockscreenos13.utils.Ads
import kotlinx.android.synthetic.main.activity_password_authentic.*
import kotlinx.android.synthetic.main.activity_password_authentic.layout_ads

class PasswordAuthentic : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_authentic)
        loadData()
        onClick()

        Ads.loadBannerAds(this, layout_ads)
    }

    private fun loadData() {
        Glide.with(this).load(R.drawable.background_pass1080).into(img_authen_pass)
    }

    private fun onClick() {
        img_back.setOnClickListener {
            finish()
        }
        btn_confirm_new.setOnClickListener {
            var y = edt_pass_new.text.toString()
            var x = AppConfig.getPassord(this)
            if (y == x) {
                val intent = Intent(this, PasswordActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                edt_pass_new.text.clear()
                Toast.makeText(this, "Password Error", Toast.LENGTH_LONG).show()
            }
        }
    }
}
