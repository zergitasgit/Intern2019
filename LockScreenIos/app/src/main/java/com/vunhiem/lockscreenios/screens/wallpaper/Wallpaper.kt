package com.vunhiem.lockscreenios.screens.wallpaper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.vunhiem.lockscreenios.model.Image
import com.vunhiem.lockscreenios.screens.wallpaper.adapter.WallpaperAdapter
import kotlinx.android.synthetic.main.activity_set_wallpaper.*
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.graphics.BitmapFactory
import android.R
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class Wallpaper : AppCompatActivity() {
    val data: ArrayList<Image> = ArrayList()
    private val RESULT_LOAD_IMAGE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.vunhiem.lockscreenios.R.layout.activity_set_wallpaper)
        onClick()
        initRecycleview()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
//            val selectedImage = data.data
//            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
//
//            val cursor = contentResolver.query(
//                selectedImage!!,
//                filePathColumn, null, null, null
//            )
//            cursor!!.moveToFirst()
//
//            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
//            val picturePath = cursor.getString(columnIndex)
//            cursor.close()
//            Log.i("tag","$picturePath")
//            val intent = Intent(this, SetWallpaper::class.java)
//            intent.putExtra("key1",picturePath)
//            startActivity(intent)
////            val imageView = findViewById<View>(R.id.imgView) as ImageView
////            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath))
//
//        }
//
//
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val intent = Intent(this, SetWallpaper::class.java)
                var uri = (result.uri).toString()
            intent.putExtra("key1",uri)
            startActivity(intent)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onClick() {

        rl_select_from.setOnClickListener {
        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this)
//            val i = Intent(
//                Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            )
//
//            startActivityForResult(i, RESULT_LOAD_IMAGE)
//
        }

    }

    private fun initRecycleview() {

        data.add(Image(com.vunhiem.lockscreenios.R.drawable.icon_background_lock))
        data.add(Image(com.vunhiem.lockscreenios.R.drawable.iphone1))
        data.add(Image(com.vunhiem.lockscreenios.R.drawable.iphone2))
        data.add(Image(com.vunhiem.lockscreenios.R.drawable.iphone3))
        data.add(Image(com.vunhiem.lockscreenios.R.drawable.iphone4))

        for (i in 1..15) {
            data.add(Image(com.vunhiem.lockscreenios.R.drawable.image + i))
        }
        rv_wallpaper.layoutManager = GridLayoutManager(this,2)
        rv_wallpaper.adapter = WallpaperAdapter(data,this)

        img_back.setOnClickListener {
            finish()
        }

    }
}
