package com.vunhiem.lockscreenios.screens.password

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ibikenavigationkotlin.utils.AppConfig
import com.vunhiem.lockscreenios.R
import kotlinx.android.synthetic.main.activity_new_password.*

class NewPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)
        onClick()
    }

    private fun onClick() {

        img_back.setOnClickListener {
            finish()
        }
        btn_confirm_new.setOnClickListener {

            var mk: String = edt_pass_new.text.toString()
            var mk2: String = edt_pass_again_new.text.toString()


            if (mk.equals(mk2) && mk.length == 6 && mk2.length == 6) {
                AppConfig.setPassword(mk, this@NewPassword)
                Toast.makeText(this, "Set password success", Toast.LENGTH_LONG).show()
                edt_pass_new.text.clear()
                edt_pass_again_new.text.clear()
                val handler = android.os.Handler()
                handler.postDelayed({
                    val intent = Intent(this, PasswordActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 500)
            } else if (mk.length < 6) {
                Toast.makeText(this, "Password have 6 number ", Toast.LENGTH_LONG).show()
            } else if (mk2.length < 6) {
                Toast.makeText(this, "Password have 6 number ", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Please check again", Toast.LENGTH_LONG).show()
            }

            img_back.setOnClickListener {
                finish()
            }
        }
    }
}


