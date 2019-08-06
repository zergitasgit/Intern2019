package com.example.testcontrol1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.os.HandlerCompat.postDelayed
import android.widget.Toast



class SlashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slash)
        //thuộc tính k thay đổi để val
        var handler: Handler = Handler()
        var intent:Intent = Intent(this,MainActivity::class.java)
        
        handler.postDelayed(object :Runnable{
            override fun run() {
                startActivity(intent)
                finish()
            }

        },300)
    }
}
