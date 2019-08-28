package com.vunhiem.lockscreenios.screens.password

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ibikenavigationkotlin.utils.AppConfig
import com.suke.widget.SwitchButton
import com.vunhiem.lockscreenios.R
import kotlinx.android.synthetic.main.activity_new_password.*
import kotlinx.android.synthetic.main.activity_password.*
import kotlinx.android.synthetic.main.activity_password.img_back

class PasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        loadData()
        onClick()
        switch()
    }
    private fun loadData() {
        Glide.with(this).load(R.drawable.background_pass1080).into(img_main_pass)
    }

    fun switch() {
        var x: Boolean? = AppConfig.getStatusPassword(this)
        switch_pass.isChecked = x!!
        switch_pass.setOnCheckedChangeListener(object : SwitchButton.OnCheckedChangeListener {
            override fun onCheckedChanged(view: SwitchButton?, isChecked: Boolean) {
                if (isChecked) {
                    AppConfig.setStatusPassword(isChecked, this@PasswordActivity)
                } else {
                    AppConfig.setStatusPassword(isChecked, this@PasswordActivity)
                }
            }

        })

    }


    private fun onClick() {
        img_back.setOnClickListener {
            finish()
        }
        ln_change_pass.setOnClickListener {
            var x = AppConfig.getPassord(this@PasswordActivity)
            if (x == null) {
                val intent = Intent(this, NewPassword::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, ChangePassActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
