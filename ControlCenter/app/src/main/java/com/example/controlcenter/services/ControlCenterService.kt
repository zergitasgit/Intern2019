package com.example.controlcenter.services

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Camera
import android.graphics.PixelFormat
import android.hardware.camera2.CameraManager
import android.net.wifi.WifiManager
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.ToggleButton
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.controlcenter.R
import com.example.controlcenter.scenes.ControlCenterGroupView
import com.example.controlcenter.utils.Utils
import java.lang.reflect.Parameter

class ControlCenterService : Service() {
    private var windowManager: WindowManager? = null
    private var viewBottom: ControlCenterGroupView? = null
    private var viewControl: ControlCenterGroupView? = null
    private var bottomParams: WindowManager.LayoutParams? = null
    private var controlParams: WindowManager.LayoutParams? = null
    private lateinit var lncontrol: LinearLayout
    private lateinit var lnBottom: LinearLayout
    private lateinit var animUp: Animation
    private lateinit var tbWifi: ToggleButton
    private lateinit var tbPlane: ToggleButton
    private lateinit var tbSync: ToggleButton
    private lateinit var tbBluetooth: ToggleButton
    private lateinit var tbFlashLight: ToggleButton

    var wifiManager: WifiManager? = null


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initView()
        return START_STICKY
    }

    private fun initView() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createIconView()
        createControlView()
        showIcon()
        initAnimation()


    }


    private fun initAnimation() {
        animUp = AnimationUtils.loadAnimation(this, R.anim.anim_up)
    }

    private fun showIcon() {
        try {
            windowManager!!.removeView(viewControl)
        } catch (e: Exception) {
            println("Bugs")
        }

        windowManager!!.addView(viewBottom, bottomParams)
    }

    private fun showControl() {
        try {
            windowManager!!.removeView(viewBottom)

        } catch (e: Exception) {
            println("Bugs")
        }

        windowManager!!.addView(viewControl, controlParams)

    }

    private fun createControlView() {
        viewControl = ControlCenterGroupView(this)
        val view: View = View.inflate(this, R.layout.control_layout, viewControl)
        controlParams = WindowManager.LayoutParams()
        controlParams!!.width = WindowManager.LayoutParams.MATCH_PARENT
        controlParams!!.height = WindowManager.LayoutParams.MATCH_PARENT
        controlParams!!.gravity = Gravity.BOTTOM
        controlParams!!.format = PixelFormat.TRANSLUCENT
        controlParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
        controlParams!!.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        controlParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        //-------------- Ánh xạ các view trong control view
        lncontrol = view.findViewById(R.id.ln_control)
        tbWifi = view.findViewById(R.id.tb_wifi)
        tbPlane = view.findViewById(R.id.tb_plane)
        tbSync = view.findViewById(R.id.tb_sync)
        tbBluetooth = view.findViewById(R.id.tb_bluetooth)
        tbFlashLight = view.findViewById(R.id.tb_flash_light)


        //--------
        lncontrol.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {

                }
                MotionEvent.ACTION_UP -> {
                    Log.d("test", "control_DOWN")
                    lncontrol.animation = animUp
                    lncontrol.animation.start()
                    showIcon()

                }
            }
            return@OnTouchListener true
        })


    }

    private fun setState() {

        // check xem wifi on hay off rồi set vào switch
        wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        if (Utils.CheckWifi(this) == true) {
            println("Đã bật wifi")
            tbWifi.isChecked = true
        } else {
            println("Chưa bật wifi")
            tbWifi.isChecked = false
        }
        // sự kiện khi switch wifi
        tbWifi.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true) {
                wifiManager!!.isWifiEnabled = true
            } else {
                wifiManager!!.isWifiEnabled = false
            }
        }
        // check xem chế độ máy bay on hay off rồi set vào switch
        if (Utils.CheckPlane(this) == true) {
            tbPlane.isChecked = true
            println("đang bật chế độ máy bay")
        } else {
            tbPlane.isChecked = false
            println("Chưa bật chế độ máy bay")
        }
        tbPlane.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true) {
                var intent: Intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent)


            } else {
                var intent: Intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent)
            }
            windowManager!!.removeView(viewControl)
            windowManager!!.addView(viewBottom, bottomParams)

        }

        // check xem đồng bộ on hay off rồi set vào switch

        tbSync.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true) {
                ContentResolver.setMasterSyncAutomatically(true)
                println("sync on")
            } else {
                ContentResolver.setMasterSyncAutomatically(false)
                println("sync off")
            }
        }

        // check xem bluetooth on hay off rồi set vào switch
        var mBtAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (Utils.CheckBluetooth(this)) {
            tbBluetooth.isChecked = true
            println("bluetooth on")
        } else {
            tbBluetooth.isChecked = false
            println("bluetooth off")
        }
        tbBluetooth.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked == true) {
                mBtAdapter.enable()

            } else {
                mBtAdapter.disable()
            }
        }
        // check flashLight
        tbFlashLight.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked == true) {
                println("FlashLight on")

            } else {
                println("FlashLight off")

            }
        }


    }

    private fun createIconView() {
        viewBottom = ControlCenterGroupView(this)
        val view: View = View.inflate(this, R.layout.bottom_layout, viewBottom)
        bottomParams = WindowManager.LayoutParams()
        bottomParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        bottomParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT
        bottomParams!!.gravity = Gravity.BOTTOM
        bottomParams!!.format = PixelFormat.TRANSLUCENT
        bottomParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            bottomParams!!.flags = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            PixelFormat.TRANSLUCENT

        } else {
            bottomParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            PixelFormat.TRANSLUCENT
        }


        //--------------
        lnBottom = view.findViewById(R.id.ln_Bottom)
        lnBottom.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d("test", "ACTION_DOWN")

                }
                MotionEvent.ACTION_UP -> {
                    Log.d("test", "ACTION_UP")
                    lncontrol.animation = animUp
                    lncontrol.animation.start()
                    showControl()
                    setState()
                }
            }
            return@OnTouchListener true
        })
    }

    override fun onDestroy() {
        windowManager!!.removeView(viewBottom)
    }
}
