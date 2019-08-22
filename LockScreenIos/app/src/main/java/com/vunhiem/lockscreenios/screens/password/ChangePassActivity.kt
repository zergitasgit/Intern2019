package com.vunhiem.lockscreenios.screens.password

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ibikenavigationkotlin.utils.AppConfig
import com.vunhiem.lockscreenios.R
import kotlinx.android.synthetic.main.activity_change.*

class ChangePassActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change)
        onClick()
    }

    private fun onClick() {
        img_back.setOnClickListener {
            finish()
        }
        btn_confirm_new.setOnClickListener {

            var mkCu = AppConfig.getPassord(this@ChangePassActivity)
            var mkOld: String = edt_pass_old.text.toString()
            var mk: String = edt_pass_new.text.toString()
            var mk2: String = edt_pass_again_new.text.toString()

            if (mkOld != mkCu) {
                edt_pass_old.text.clear()
                Toast.makeText(this, "Old password is not correct", Toast.LENGTH_LONG).show()
            } else if (mk.equals(mk2) && mk.length == 6 && mk2.length == 6 && mkOld.equals(mkCu)) {
                AppConfig.setPassword(mk, this@ChangePassActivity)
                Toast.makeText(this, "Set password success", Toast.LENGTH_LONG).show()
                edt_pass_new.text.clear()
                edt_pass_again_new.text.clear()
                val handler = android.os.Handler()
                handler.postDelayed({ finish() }, 500)

            } else if (mk.length < 6) {
                Toast.makeText(this, "New password have 6 number", Toast.LENGTH_LONG).show()
            } else if (mk.length < 6) {
                Toast.makeText(this, "New password have 6 number", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Please check again", Toast.LENGTH_LONG).show()
            }
        }
    }
}

