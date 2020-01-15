package com.lock.applock.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.lock.applock.R
import kotlinx.android.synthetic.main.activity_pin.*

class PinActivity : AppCompatActivity() {
    var sharedPreferences: SharedPreferences? = null
    var edit: SharedPreferences.Editor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_pin)
        sharedPreferences = getSharedPreferences("hieu", Context.MODE_PRIVATE)
        edit = sharedPreferences!!.edit()
        setUp()
    }

    override fun onStop() {
        super.onStop()
        if (tv_pass.text == "Creat a Pin \n (Again)") {
            edit!!.putString(
                "passwordNew",
                ""
            )
            edit!!.apply()
            tv_pass.text = "Creat a Pin"
        }
    }

    private fun setUp() {
        edit!!.putString(
            "passwordNew",
            ""
        )
        edit!!.apply()
        pin_lock_view.attachIndicatorDots(indicator_dots)
        pin_lock_view.setPinLockListener(mPinLockListener)
        pin_lock_view.pinLength = 4
        pin_lock_view.textColor = ContextCompat.getColor(this, R.color.white)

        indicator_dots.indicatorType = IndicatorDots.IndicatorType.FILL_WITH_ANIMATION
    }

    private val mPinLockListener: PinLockListener = object : PinLockListener {
        override fun onComplete(pin: String) {
            if (sharedPreferences!!.getString("passwordNew", "") ==
                pin
            ) {
                tv_pass.text = "Creat a Pin"
                edit!!.putString("pattern", pin)
                edit!!.putString("passwordNew", "")
                edit!!.putBoolean("pin", true)
                edit!!.apply()
                onBackPressed()
                val intent = Intent("CHANGE")
                intent.putExtra("pattern",false)
                sendBroadcast(intent)

            } else {
                if (sharedPreferences!!.getString("passwordNew", "") == "") {
                    edit!!.putString("passwordNew", pin)
                    edit!!.apply()
                    tv_pass.text = "Creat a Pin \n (Again)"
                } else {
                    tv_pass.visibility = View.GONE
                    tv_pass_wrong_pin.visibility = View.VISIBLE

                }
                Handler().postDelayed({
                    pin_lock_view.resetPinLockView()
                }, 500)
            }
        }

        override fun onEmpty() {
        }

        override fun onPinChange(pinLength: Int, intermediatePin: String) {
            tv_pass.visibility = View.VISIBLE
            tv_pass_wrong_pin.visibility = View.GONE
        }
    }
}
