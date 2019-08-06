package com.example.testcontrol1

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.content.SharedPreferences
import android.widget.SeekBar
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    private val PREFS_NAME = "kotlincodes"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var intent: Intent = Intent(this, MyService::class.java)


        seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                //tạo một class quản lý việc lưu và ghi SharedPreferences
                val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                var editor = sharedPreference.edit()
                editor.putInt("size", i)
                editor.commit()
                text_view.text = "Progress : $i"
                var i:Int = sharedPreference.getInt("size", 0)


            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                startService(intent)
            }
        })

        sw_control.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true) {
                startService(intent)
            } else if (isChecked == false) {
                Toast.makeText(this, "da tat service", Toast.LENGTH_SHORT).show()
                stopService(intent)

            }
        }

    }
}
