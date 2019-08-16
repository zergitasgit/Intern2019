package com.example.controlcenter.services

import abak.tr.com.boxedverticalseekbar.BoxedVertical
import android.app.Service
import android.app.admin.DevicePolicyManager
import android.bluetooth.BluetoothAdapter
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PixelFormat
import android.hardware.Camera
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.IBinder
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.controlcenter.scenes.ControlCenterGroupView
import com.example.controlcenter.utils.Utils
import android.widget.Toast
import com.example.controlcenter.R


class ControlCenterService : Service() {
    private var windowManager: WindowManager? = null
    private var viewBottom: ControlCenterGroupView? = null
    private var viewControl: ControlCenterGroupView? = null
    private var viewTimeOut: ControlCenterGroupView? = null
    private var bottomParams: WindowManager.LayoutParams? = null
    private var controlParams: WindowManager.LayoutParams? = null
    private var timeoutParams: WindowManager.LayoutParams? = null
    private lateinit var rlControl: RelativeLayout
    private lateinit var lnBottom: LinearLayout
    private lateinit var lnTimeOut: LinearLayout
    private lateinit var animUp: Animation
    private lateinit var animLeft: Animation
    private lateinit var animRight: Animation
    private lateinit var tbWifi: ToggleButton
    private lateinit var tbPlane: ToggleButton
    private lateinit var tbSync: ToggleButton
    private lateinit var tbRotate: ToggleButton
    private lateinit var tbBluetooth: ToggleButton
    private lateinit var tbMute: ToggleButton
    private lateinit var btnTimeOut: Button
    private lateinit var sbLight: BoxedVertical
    private lateinit var sbVolume: BoxedVertical
    private lateinit var tbFlashLight: ToggleButton
    private lateinit var btnClock: Button
    private lateinit var btnCalculator: Button
    private lateinit var btnCamera: Button
    private lateinit var btnMusic: Button
    private lateinit var btnSetting: Button
    private lateinit var tbHotspot: ToggleButton
    private lateinit var btnLocation: Button
    private var y: Int = 0
    private var touchY: Float = 0.0f
    private var x: Int = 0
    private var touchX: Float = 0.0f
    private var touchToMove: Boolean = false
    private var wifiManager: WifiManager? = null
    private lateinit var camera: Camera

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initView()
        return START_STICKY
    }

    private fun initView() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createIconView()
        createControlView()
        createTimeOut()
        showIcon()
        initAnimation()
    }

    // khởi tạo Animation
    private fun initAnimation() {
        animUp = AnimationUtils.loadAnimation(this, R.anim.anim_up)
        animLeft = AnimationUtils.loadAnimation(this, R.anim.anim_left)
        animRight = AnimationUtils.loadAnimation(this, R.anim.anim_right)
    }

    // hiển thị thanh nhỏ nhỏ ở bottom
    private fun showIcon() {
        try {
            windowManager!!.removeView(viewControl)
        } catch (e: Exception) {
            println("Bugs")
        }
        windowManager!!.addView(viewBottom, bottomParams)


    }

    // hiển thị bảng Control
    private fun showControl() {
        try {
            windowManager!!.removeView(viewBottom)

        } catch (e: Exception) {
            println("Bugs")
        }

        windowManager!!.addView(viewControl, controlParams)

    }

    // hiển thị bảng chọn thời gian tắt màn hình
    private fun showTimeOut() {
        try {
            windowManager!!.removeView(viewControl)

        } catch (e: Exception) {
            println("Bugs")
        }
        windowManager!!.addView(viewTimeOut, timeoutParams)
    }

    // tạo các widget trong phần control
    private fun createControlView() {
        viewControl = ControlCenterGroupView(this)
        val view: View = View.inflate(this, R.layout.control_layout, viewControl)
        controlParams = WindowManager.LayoutParams()
        controlParams!!.width = WindowManager.LayoutParams.MATCH_PARENT
        controlParams!!.height = WindowManager.LayoutParams.MATCH_PARENT
        controlParams!!.gravity = Gravity.BOTTOM
        controlParams!!.format = PixelFormat.TRANSLUCENT
        controlParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
        viewControl!!.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar

                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar

                    or View.SYSTEM_UI_FLAG_IMMERSIVE
        )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            bottomParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            PixelFormat.TRANSLUCENT
        } else {
            bottomParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            PixelFormat.TRANSLUCENT
        }

        //-------------- Ánh xạ các view trong control view

        rlControl = view.findViewById(R.id.rl_control)
        tbWifi = view.findViewById(R.id.tb_wifi)
        tbPlane = view.findViewById(R.id.tb_plane)
        tbSync = view.findViewById(R.id.tb_sync)
        tbBluetooth = view.findViewById(R.id.tb_bluetooth)
        tbRotate = view.findViewById(R.id.tb_rotate)
        tbMute = view.findViewById(R.id.tb_mute)
        btnTimeOut = view.findViewById(R.id.btn_time_out)
        sbLight = view.findViewById(R.id.sb_light)
        sbVolume = view.findViewById(R.id.sb_volume)
        tbFlashLight = view.findViewById(R.id.tb_flash_light)
        btnCalculator = view.findViewById(R.id.btn_calculator)
        btnCamera = view.findViewById(R.id.btn_camera)
        btnClock = view.findViewById(R.id.btn_clock)
        btnMusic = view.findViewById(R.id.btn_music)
        btnSetting = view.findViewById(R.id.btn_setting)
        tbHotspot = view.findViewById(R.id.tb_hotspot)
        btnLocation = view.findViewById(R.id.btn_location)

    }

    // khởi tạo window manager của phần time out
    private fun createTimeOut() {
        viewTimeOut = ControlCenterGroupView(this)
        val view: View = View.inflate(this, R.layout.time_out_layout, viewTimeOut)
        timeoutParams = WindowManager.LayoutParams()
        timeoutParams!!.width = WindowManager.LayoutParams.MATCH_PARENT
        timeoutParams!!.height = WindowManager.LayoutParams.MATCH_PARENT
        timeoutParams!!.gravity = Gravity.BOTTOM
        timeoutParams!!.format = PixelFormat.TRANSLUCENT
        timeoutParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
        timeoutParams!!.flags =
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        viewTimeOut!!.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar

                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar

                    or View.SYSTEM_UI_FLAG_IMMERSIVE
        )
        lnTimeOut = view.findViewById(R.id.ln_time_out)
        lnTimeOut.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {

                }
                MotionEvent.ACTION_UP -> {
                    Log.d("test", "control_DOWN")
                    windowManager!!.removeView(viewTimeOut)
                    showControl()

                }
            }
            return@OnTouchListener true
        })
        var btn15s: Button = view.findViewById(R.id.btn_15s)
        var btn30s: Button = view.findViewById(R.id.btn_30s)
        var btn1p: Button = view.findViewById(R.id.btn_1p)
        var btn2p: Button = view.findViewById(R.id.btn_2p)
        var btn10p: Button = view.findViewById(R.id.btn_10p)
        var btn30p: Button = view.findViewById(R.id.btn_30p)
        val textColorRed = Color.RED
        val textColorWhite = Color.WHITE
        // sử lý sự kiện check time our rồi đổi màu của button set time
        if (getTimeOut() == 15000) {
            btn15s.setTextColor(textColorRed)
        }
        if (getTimeOut() == 30000) {
            btn30s.setTextColor(textColorRed)
        }
        if (getTimeOut() == 60000) {
            btn1p.setTextColor(textColorRed)
        }
        if (getTimeOut() == 120000) {
            btn2p.setTextColor(textColorRed)
        }
        if (getTimeOut() == 600000) {
            btn10p.setTextColor(textColorRed)
        }
        if (getTimeOut() == 1800000) {
            btn30p.setTextColor(textColorRed)
        }
        // đổi màu khi người dùng click vào button thời gian nào đó
        btn15s.setOnClickListener {
            setTimeOut(15000)
            Toast.makeText(this, "thời gian khóa màn hình là 15s,", Toast.LENGTH_SHORT).show()
            btn15s.setTextColor(textColorRed)
            btn30s.setTextColor(textColorWhite)
            btn1p.setTextColor(textColorWhite)
            btn2p.setTextColor(textColorWhite)
            btn10p.setTextColor(textColorWhite)
            btn30p.setTextColor(textColorWhite)
        }
        btn30s.setOnClickListener {
            setTimeOut(30000)
            Toast.makeText(this, "thời gian khóa màn hình là 30s,", Toast.LENGTH_SHORT).show()
            btn30s.setTextColor(textColorRed)
            btn15s.setTextColor(textColorWhite)
            btn1p.setTextColor(textColorWhite)
            btn2p.setTextColor(textColorWhite)
            btn10p.setTextColor(textColorWhite)
            btn30p.setTextColor(textColorWhite)
        }
        btn1p.setOnClickListener {
            setTimeOut(60000)
            Toast.makeText(this, "thời gian khóa màn hình là 1p,", Toast.LENGTH_SHORT).show()
            btn1p.setTextColor(textColorRed)
            btn15s.setTextColor(textColorWhite)
            btn30s.setTextColor(textColorWhite)
            btn2p.setTextColor(textColorWhite)
            btn10p.setTextColor(textColorWhite)
            btn30p.setTextColor(textColorWhite)
        }
        btn2p.setOnClickListener {
            setTimeOut(120000)
            Toast.makeText(this, "thời gian khóa màn hình là 2p,", Toast.LENGTH_SHORT).show()
            btn2p.setTextColor(textColorRed)
            btn15s.setTextColor(textColorWhite)
            btn30s.setTextColor(textColorWhite)
            btn1p.setTextColor(textColorWhite)
            btn10p.setTextColor(textColorWhite)
            btn30p.setTextColor(textColorWhite)
        }
        btn10p.setOnClickListener {
            setTimeOut(600000)
            Toast.makeText(this, "thời gian khóa màn hình là 10p,", Toast.LENGTH_SHORT).show()
            btn15s.setTextColor(textColorWhite)
            btn30s.setTextColor(textColorWhite)
            btn1p.setTextColor(textColorWhite)
            btn2p.setTextColor(textColorWhite)
            btn30p.setTextColor(textColorWhite)
            btn10p.setTextColor(textColorRed)
        }
        btn30p.setOnClickListener {
            setTimeOut(1800000)
            Toast.makeText(this, "thời gian khóa màn hình là 30p,", Toast.LENGTH_SHORT).show()
            btn15s.setTextColor(textColorWhite)
            btn30s.setTextColor(textColorWhite)
            btn1p.setTextColor(textColorWhite)
            btn2p.setTextColor(textColorWhite)
            btn10p.setTextColor(textColorWhite)
            btn30p.setTextColor(textColorRed)
        }


    }

    // fun set thời gian chờ của màn hình
    fun setTimeOut(miliseconds: Int) {
        android.provider.Settings.System.putInt(
            contentResolver,
            Settings.System.SCREEN_OFF_TIMEOUT,
            miliseconds
        )
    }

    // fun lấy thời gian chờ của màn hình
    fun getTimeOut(): Int {
        var i: Int = android.provider.Settings.System.getInt(
            contentResolver,
            Settings.System.SCREEN_OFF_TIMEOUT
        )
        return i
    }


    //kiểm tra các trạng thái của hệ thống và sử lý các sự kiện của trạng thái đó
    private fun setState() {
        checkWifi()
        checkPlane()
        checkSync()
        checkBluetooth()
        checkRotateScreens()
        checkAudioSystem()
        timeOut()
        setLight()
        setVolume()
        flashLight()
        clock()
        caculator()
        openCamera()
        openMusicSetting()
        touchOutControl()
        settingSystem()
        hotSpot()
        location()

    }

    private fun checkWifi() {
        // check xem wifi on hay off rồi set vào switch
        wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        if (Utils.CheckWifi(this) == true) {
            println("Đã bật wifi")
            tbWifi.isChecked = true
        } else {
            println("Chưa bật wifi")
            tbWifi.isChecked = false
        }
        // sự kiện  khi nhấn wifi
        tbWifi.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true) {
                wifiManager!!.isWifiEnabled = true
            } else {
                wifiManager!!.isWifiEnabled = false
            }
        }
    }

    private fun checkPlane() {
        // check xem chế độ máy bay on hay off rồi set vào switch
        if (Utils.CheckPlane(this) == true) {
            tbPlane.isChecked = true
            println("đang bật chế độ máy bay")
        } else {
            tbPlane.isChecked = false
            println("Chưa bật chế độ máy bay")
        }
        // sự kiện  khi nhấn vào máy bay
        tbPlane.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true) {
                var intent: Intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                showIcon()

            } else {
                var intent: Intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                showIcon()
            }
        }
    }

    private fun checkSync() {
        if (Utils.CheckSync(this) == true) {
            tbSync.isChecked = true
        } else {
            tbSync.isChecked = false
        }
        // check xem đồng bộ on hay off rồi set trạng thái
        tbSync.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true) {
                ContentResolver.setMasterSyncAutomatically(true)
                println("sync on")
            } else {
                ContentResolver.setMasterSyncAutomatically(false)
                println("sync off")
            }
        }
    }

    private fun checkBluetooth() {
        // check xem bluetooth on hay off rồi set trạng thái
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

        } else {
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
        }

    }

    private fun openMusicSetting() {
        btnMusic.setOnClickListener {
            //Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
            //startActivity(intent);
            val intent: Intent = Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            showIcon()
            startActivity(intent)
        }
    }

    private fun settingSystem() {
        btnSetting.setOnClickListener {
            val intent: Intent = Intent(android.provider.Settings.ACTION_SETTINGS)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
            showIcon()
        }


    }

    private fun hotSpot() {
        println(Utils.checkHotspot(this))


        tbHotspot.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true) {

                Utils.turnOnHotSpot(this)

            } else {
                Utils.turnOffHotSpot(this)
            }
        }

    }

    private fun location() {
        btnLocation.setOnClickListener {
            val intent: Intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            showIcon()
        }
    }

    private fun checkRotateScreens() {
        if (Utils.checkRotate(this) == 1) {
            tbRotate.isChecked = true
        } else {
            tbRotate.isChecked = false
        }
        tbRotate.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked == true) {
                android.provider.Settings.System.putInt(
                    getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION,
                    1
                )

            } else {

                android.provider.Settings.System.putInt(
                    getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION,
                    0
                )

            }
        }

    }

    private fun checkAudioSystem() {
        // check và set state của chế độ rung Vibrate
        if (Utils.checkAudio(this) == 1) {
            tbMute.isChecked = true
        } else {
            tbMute.isChecked = false
        }
        val audioManager: AudioManager
        audioManager = baseContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        tbMute.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked == true) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT)

            } else {

                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
            }
        }
    }

    private fun setLight() {
        var data = Utils.getLight(this@ControlCenterService)
        sbLight.value = data


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // su ly doi voi android 8.0
        } else {
            sbLight.setOnBoxedPointsChangeListener(object : BoxedVertical.OnValuesChangeListener {
                override fun onPointsChanged(boxedPoints: BoxedVertical, value: Int) {
                    var brightness = value
                    Settings.System.putInt(
                        contentResolver,
                        android.provider.Settings.System.SCREEN_BRIGHTNESS,
                        value
                    )

                }

                override fun onStartTrackingTouch(boxedPoints: BoxedVertical) {

                }

                override fun onStopTrackingTouch(boxedPoints: BoxedVertical) {

                }
            })
        }
    }

    private fun setVolume() {
        var audioManager: AudioManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        sbVolume.value = Utils.getVolume(this)
        sbVolume.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        sbVolume.setOnBoxedPointsChangeListener(object : BoxedVertical.OnValuesChangeListener {
            override fun onPointsChanged(boxedPoints: BoxedVertical, value: Int) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    value, 0
                )
            }

            override fun onStartTrackingTouch(boxedPoints: BoxedVertical) {

            }

            override fun onStopTrackingTouch(boxedPoints: BoxedVertical) {

            }
        })

    }

    private fun flashLight() {
        // sử lý sự kiện khi nhấn vào button Flash Light
        tbFlashLight.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true) {
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    camera = Camera.open()
                    var p = camera!!.getParameters()
                    p!!.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                    camera!!.setParameters(p)
                    camera!!.startPreview()
                }
            } else {
                camera.stopPreview()
                camera.release()
                println("FlashLight off")
            }
        }
    }

    private fun clock() {
        // sử lý sự kiện khi nhấn vào button đồng hồ
        btnClock.setOnClickListener {
            Toast.makeText(this, "Chưa được phát triển", Toast.LENGTH_SHORT).show()
        }
    }

    private fun caculator() {
        // sử lý sự kiện khi nhấn vào button máy tính
        btnCalculator.setOnClickListener {
            val intent: Intent = Intent()
            intent.setClassName("com.android.calculator2", "com.android.calculator2.Calculator")
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            windowManager!!.removeView(viewControl)
            showIcon()

        }
    }

    private fun openCamera() {
        // sử lý sự kiện khi nhấn vào button Camera
        btnCamera.setOnClickListener {
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            windowManager!!.removeView(viewControl)
            showIcon()

        }
    }

    private fun timeOut() {
        // sử lý sự kiện set time out - cài đặt thời gian chờ màn hình
        btnTimeOut.setOnClickListener {
            showTimeOut()
        }
    }

    private fun touchOutControl() {
        // sử lý sự kiện khi nhấn vào phần control để out ra khỏi nó
        rlControl.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {

                }
                MotionEvent.ACTION_UP -> {
                    Log.d("test", "control_DOWN")
                    rlControl.animation = animUp
                    rlControl.animation.start()
                    showIcon()

                }
            }
            return@OnTouchListener true
        })
    }

    // tạo các widget trong phần icon bottom -- cái thanh dài dài nhỏ nhỏ ý :>>
    private fun createIconView() {
        viewBottom = ControlCenterGroupView(this)
        var view: View
        bottomParams = WindowManager.LayoutParams()
        bottomParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        bottomParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT

        bottomParams!!.format = PixelFormat.TRANSLUCENT
        bottomParams!!.type = WindowManager.LayoutParams.TYPE_PHONE

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            bottomParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            PixelFormat.TRANSLUCENT
        } else {
            bottomParams!!.flags = WindowManager.LayoutParams.TYPE_PHONE
            PixelFormat.TRANSLUCENT
        }
        //--------------

        if (Utils.getPosition(this) == 1) {
            view = View.inflate(this, R.layout.left_layout, viewBottom)
            bottomParams!!.gravity = Gravity.LEFT
            lnBottom = view.findViewById(R.id.ln_Bottom)
            lnBottom.layoutParams.width = 50
            lnBottom.layoutParams.height = Utils.getSize(this)
            moveControlLeft()

        }
        if (Utils.getPosition(this) == 2) {
            view = View.inflate(this, R.layout.left_layout, viewBottom)
            bottomParams!!.gravity = Gravity.RIGHT
            lnBottom = view.findViewById(R.id.ln_Bottom)
            lnBottom.layoutParams.width = 50
            lnBottom.layoutParams.height = Utils.getSize(this)
            moveControlLeft()

        }
        if (Utils.getPosition(this) == 3) {
            view = View.inflate(this, R.layout.bottom_layout, viewBottom)
            bottomParams!!.gravity = Gravity.BOTTOM
            lnBottom = view.findViewById(R.id.ln_Bottom)
            lnBottom.layoutParams.width = Utils.getSize(this)
            moveControlUP()
        }


    }

    private fun moveControlLeft() {
        lnBottom.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = bottomParams!!.x
                    touchX = motionEvent.rawX
                    touchToMove = false
                }
                MotionEvent.ACTION_MOVE -> {
                    val delX = motionEvent.rawX - touchY
                    bottomParams!!.x = (x - delX).toInt()
                    windowManager!!.updateViewLayout(viewBottom, bottomParams)
                    if (delX * delX > 1) {
                        bottomParams!!.y = 0
                        windowManager!!.updateViewLayout(viewBottom, bottomParams)
                    }

                    if (delX * delX > 200) {
                        touchToMove = true
                        bottomParams!!.y = 0
                        windowManager!!.updateViewLayout(viewBottom, bottomParams)
                    }

                }
                MotionEvent.ACTION_UP -> {
                    if (touchToMove == true) {
                        rlControl.animation = animLeft
                        rlControl.animation.start()
                        showControl()
                        setState()
                    }
                }
            }
            return@OnTouchListener true
        })
    }

    // xử lý sự kiện vuốt ở thanh icon dài dài nhỏ nhỏ ý
    private fun moveControlUP() {
        lnBottom.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    y = bottomParams!!.y
                    touchY = motionEvent.rawY
                    touchToMove = false
                }
                MotionEvent.ACTION_MOVE -> {
                    val delY = motionEvent.rawY - touchY
                    bottomParams!!.y = (y - delY).toInt()
                    windowManager!!.updateViewLayout(viewBottom, bottomParams)
                    if (delY * delY > 1) {
                        bottomParams!!.y = 0
                        windowManager!!.updateViewLayout(viewBottom, bottomParams)
                    }

                    if (delY * delY > 200) {
                        touchToMove = true
                        bottomParams!!.y = 0
                        windowManager!!.updateViewLayout(viewBottom, bottomParams)
                    }

                }
                MotionEvent.ACTION_UP -> {
                    if (touchToMove == true) {
                        rlControl.animation = animUp
                        rlControl.animation.start()
                        showControl()
                        setState()
                    }
                }
            }
            return@OnTouchListener true
        })
    }

    // xử lý sự kiện destroy service
    override fun onDestroy() {
        windowManager!!.removeView(viewBottom)
    }
}
