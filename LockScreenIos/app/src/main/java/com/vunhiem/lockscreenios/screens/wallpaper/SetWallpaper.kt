package com.vunhiem.lockscreenios.screens.wallpaper

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ibikenavigationkotlin.utils.AppConfig
import com.squareup.picasso.Picasso
import com.vunhiem.lockscreenios.R
import com.vunhiem.lockscreenios.screens.main.MyGroupView
import com.vunhiem.lockscreenios.service.MyService
import kotlinx.android.synthetic.main.activity_set_wallpaper2.*
import java.io.File


class SetWallpaper : AppCompatActivity() {
    private var mView: MyGroupView? = null
    var image: Int = 0
    lateinit var service: MyService
    var imgID: String?=null


    lateinit var imgBg: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_set_wallpaper2)
        getData()
        onClick()

    }

    private fun onClick() {

        btn_cancle.setOnClickListener {
            finish()
        }
    }

    private fun getData() {
        val bundle = intent.extras
        var image = bundle!!.getInt("key")


        if (image != 0) {
            Picasso.with(this)
                .load(image)
                .fit().centerCrop()
                .into(img_background)
        } else {
            imgID = intent.getStringExtra("key1")
            val uri:Uri= Uri.parse(imgID)
            img_background.setImageURI(uri)

        }

        btn_select.setOnClickListener {
            var x: String? = AppConfig.getIdWallPaper(this)
            var y: String? = AppConfig.getIdWallPaperUri(this)

            if (x == null && y == null && image != 0 && imgID == null) {

                AppConfig.setIdWallPaper("$image", this)
            }
            if (x == null && y == null && image == 0 && imgID != null) {

                AppConfig.setIdWallPaperUri(imgID, this)
            }
            if (x != null && y == null && image != 0 && imgID == null) {

                AppConfig.setIdWallPaper("$image", this)
            }
            if (x != null && y == null && image == 0 && imgID != null) {

                AppConfig.setIdWallPaperUri(imgID, this)
                AppConfig.setIdWallPaper(null, this)
            }
            if (x == null && y != null && image == 0 && imgID != null) {

                AppConfig.setIdWallPaperUri(imgID, this)

            }
            if (x == null && y != null && image != 0 && imgID == null) {

                AppConfig.setIdWallPaperUri(null, this)
                AppConfig.setIdWallPaper("$image", this)

            }


            Toast.makeText(this, "Set Wallpaper Success", Toast.LENGTH_LONG).show()
            val handler = android.os.Handler()
            handler.postDelayed({ finish() }, 500)
        }
    }


}
