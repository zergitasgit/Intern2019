package com.lock.applock.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lock.applock.R
import kotlinx.android.synthetic.main.activity_change_lock.*

class ChangeLockActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_lock)
        rl_pin.setOnClickListener {
            val intent = Intent(this,PinActivity::class.java)
            startActivity(intent)
        }
        rl_pattern.setOnClickListener {
            val intent = Intent(this,PatternActivity::class.java)
            startActivity(intent)
        }
    }
}
