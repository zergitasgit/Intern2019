package com.example.filepermission

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_read_and_write.*
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader


class ReadAndWriteActivity : AppCompatActivity() {
    val FILE_NAME: String = "example.txt"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_and_write)
        init()
    }

    private fun init() {
        btn_save_data.setOnClickListener {
            saveData()
        }
        btn_load_data.setOnClickListener {
            loadData()
        }
    }

    private fun loadData() {
        var fileInputStream: FileInputStream = openFileInput("example.txt")
        val inputStreamReader:InputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader : BufferedReader = BufferedReader(inputStreamReader)
        val stringBuffer : StringBuffer = StringBuffer()
        var line: String? = null;
        while ({ line = bufferedReader.readLine(); line }() != null) {
            stringBuffer.append(line+"\n")

        }
        var data :String = stringBuffer.toString()
        tv_data.text=data


    }

    private fun saveData() {
        val charset = Charsets.UTF_8
        var edtText: String = edt_data.text.toString()
        val text = edtText.toByteArray(charset)
        var fileOutputStream: FileOutputStream = openFileOutput("example.txt", Context.MODE_PRIVATE)
        fileOutputStream.write(text)
        fileOutputStream.close()
        Toast.makeText(this, "File đã được lưu", Toast.LENGTH_SHORT).show()
        edt_data.text = null


    }

}
