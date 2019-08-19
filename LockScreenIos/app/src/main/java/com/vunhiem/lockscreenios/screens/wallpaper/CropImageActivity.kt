package com.vunhiem.lockscreenios.screens.wallpaper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.theartofdev.edmodo.cropper.CropImage
import android.content.Intent

import android.app.Activity
import com.theartofdev.edmodo.cropper.CropImageView


class CropImageActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.vunhiem.lockscreenios.R.layout.activity_crop_image)

    }
   fun getData(){
       val bundle = intent.extras
       var image = bundle!!.getInt("key")

   }
}
