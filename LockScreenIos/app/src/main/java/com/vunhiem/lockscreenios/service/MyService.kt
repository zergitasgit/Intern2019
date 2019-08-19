package com.vunhiem.lockscreenios.service

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PixelFormat
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ibikenavigationkotlin.utils.AppConfig
import com.squareup.picasso.Picasso
import com.vunhiem.lockscreenios.R
import com.vunhiem.lockscreenios.model.Notification
import com.vunhiem.lockscreenios.notification.SwipeToDeleteCallback
import com.vunhiem.lockscreenios.notification.adapter.NotificationAdaper
import com.vunhiem.lockscreenios.screens.main.GroupViewPassword
import com.vunhiem.lockscreenios.screens.main.MyGroupView
import java.io.File
import java.text.SimpleDateFormat


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class MyService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onCreate() {
        super.onCreate()
        context = applicationContext

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initview()
        setTime()

        return START_STICKY
    }

    private fun initview() {
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        createLockScreen()
        swipeUpToUnlock()


    }


    fun setTime() {
        val h = Handler()
        h.post(
            object : Runnable {
                override fun run() {
                    val date = System.currentTimeMillis()
                    val sdf = SimpleDateFormat("HH:mm")
                    val timedate = SimpleDateFormat("E, d/M/yyyy")
                    val dateString = sdf.format(date)
                    val datex = timedate.format(date)
                    tvDate.text = datex
                    tvTime.setText(dateString)
                    h.postDelayed(this, 300)
                }
            })
    }


    fun setFullScreen() {
        mView!!.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar

                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar

                    or View.SYSTEM_UI_FLAG_IMMERSIVE
        )
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun mCamera() {
        isFlashOn = false
        objCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager


        mCameraId = objCameraManager!!.cameraIdList[0]


        rlFlash.setOnClickListener {
            try {
                if (isFlashOn == false) {
                    turnOnFlash()
                    isFlashOn = true
                } else {
                    turnOffFlash()
                    isFlashOn = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            true
        }
    }

    private fun turnOnFlash() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            objCameraManager!!.setTorchMode(mCameraId!!, true)
        } else {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                if (camera == null)
                    Log.i("tag", "flahON")
                camera = Camera.open()
                var p = camera!!.getParameters()
                p!!.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                camera!!.setParameters(p)
                camera!!.startPreview()
            }
        }

    }

    private fun turnOffFlash() {
//        try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            objCameraManager!!.setTorchMode(mCameraId!!, false)
        } else {
            Log.i("tag", "flahof")
            if (camera != null) {
                camera!!.stopPreview()
                camera!!.release()
                camera = null
            }

        }

    }

    @SuppressLint("MissingPermission")
    private fun createPasswordScreen() {
        wmpass = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        passView = GroupViewPassword(applicationContext)
        val v: View = View.inflate(applicationContext, com.vunhiem.lockscreenios.R.layout.layout_password, passView)
        tvCanclePass = v.findViewById(R.id.tv_cancle_pass)
        tvCall = v.findViewById(R.id.tv_call)
        tvPin = v.findViewById(R.id.tv_pin)
        imgPin = v.findViewById(R.id.img_pin)
        ll_circle_pass = v.findViewById(R.id.ln_circle_pass)

        pass1 = v.findViewById(R.id.pass1)
        pass2 = v.findViewById(R.id.pass2)
        pass3 = v.findViewById(R.id.pass3)
        pass4 = v.findViewById(R.id.pass4)
        pass5 = v.findViewById(R.id.pass5)
        pass6 = v.findViewById(R.id.pass6)



        btn0 = v.findViewById(R.id.btn_0)
        btn1 = v.findViewById(R.id.btn_1)
        btn2 = v.findViewById(R.id.btn_2)
        btn3 = v.findViewById(R.id.btn_3)
        btn5 = v.findViewById(R.id.btn_5)
        btn4 = v.findViewById(R.id.btn_4)
        btn6 = v.findViewById(R.id.btn_6)
        btn7 = v.findViewById(R.id.btn_7)
        btn8 = v.findViewById(R.id.btn_8)
        btn9 = v.findViewById(R.id.btn_9)


        passView!!.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar

                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar

                    or View.SYSTEM_UI_FLAG_IMMERSIVE
        )


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mParamsPass = WindowManager.LayoutParams()
            mParamsPass!!.width = WindowManager.LayoutParams.MATCH_PARENT
            mParamsPass!!.height = WindowManager.LayoutParams.MATCH_PARENT
            mParamsPass!!.flags =
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_FULLSCREEN
            mParamsPass!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            PixelFormat.TRANSLUCENT

            mParams!!.x = 0
            mParams!!.y = 0

            wmpass!!.addView(passView, mParamsPass)
            Log.i("tag", "onaddPass")
            isshowPass = true

            mReceiver = LockScreenStateReceiver()
            val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
            filter.addAction(Intent.ACTION_BATTERY_CHANGED)
            filter.addAction(Intent.ACTION_USER_PRESENT)
            filter.addAction(Intent.ACTION_SCREEN_OFF)
            registerReceiver(mReceiver, filter)
        } else {

            mParamsPass = WindowManager.LayoutParams()
            mParamsPass!!.width = WindowManager.LayoutParams.MATCH_PARENT
            mParamsPass!!.height = WindowManager.LayoutParams.MATCH_PARENT
            mParamsPass!!.gravity = Gravity.BOTTOM
            mParamsPass!!.format = PixelFormat.TRANSLUCENT
            mParamsPass!!.type = WindowManager.LayoutParams.TYPE_PHONE
            mParamsPass!!.flags = (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_DIM_BEHIND
                    or WindowManager.LayoutParams.FLAG_FULLSCREEN)

            mParams!!.x = 0
            mParams!!.y = 0


            wmpass!!.addView(passView, mParamsPass)
            Log.i("tag", "onaddPass")
            isshowPass = true

            mReceiver = LockScreenStateReceiver()
            val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
            filter.addAction(Intent.ACTION_BATTERY_CHANGED)
            filter.addAction(Intent.ACTION_USER_PRESENT)
            filter.addAction(Intent.ACTION_SCREEN_OFF)
            registerReceiver(mReceiver, filter)


        }

        listPass = ArrayList()


        tvCanclePass.setOnClickListener {
            if (listPass.size == 0) {
                wmpass!!.removeView(passView)
                checkNotifiEmpty()
                wm!!.addView(mView, mParams)
            } else {
                listPass.clear()
                pass1.setImageResource(R.drawable.circle_password)
                pass2.setImageResource(R.drawable.circle_password)
                pass3.setImageResource(R.drawable.circle_password)
                pass4.setImageResource(R.drawable.circle_password)
                pass5.setImageResource(R.drawable.circle_password)
                pass6.setImageResource(R.drawable.circle_password)
            }
        }
        tvCall.setOnClickListener {
            val callIntent = Intent("com.android.phone.EmergencyDialer.DIAL")
            callIntent.setData(Uri.parse("tel:" + "113"))
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(callIntent)
        }
        btn0.setOnClickListener {
            var size: Int = listPass.size
            if (size < 6)
                listPass.add(0)
            password()
            Log.i("tag", "size " + size)
            Log.i("tag", "haha" + listPass)
        }
        btn1.setOnClickListener {
            var size: Int = listPass.size
            if (size < 6)
                listPass.add(1)
            password()
            Log.i("tag", "haha" + listPass)
        }
        btn2.setOnClickListener {
            var size: Int = listPass.size
            if (size < 6)
                listPass.add(2)
            password()
            Log.i("tag", "" + listPass)
        }
        btn3.setOnClickListener {
            var size: Int = listPass.size
            if (size < 6)
                listPass.add(3)
            password()
            Log.i("tag", "" + listPass)
        }
        btn4.setOnClickListener {
            var size: Int = listPass.size
            if (size < 6)
                listPass.add(4)
            password()
            Log.i("tag", "" + listPass)
        }
        btn5.setOnClickListener {
            var size: Int = listPass.size
            if (size < 6)
                listPass.add(5)
            password()
            Log.i("tag", "" + listPass)
        }
        btn6.setOnClickListener {
            var size: Int = listPass.size
            if (size < 6)
                listPass.add(6)
            password()
            Log.i("tag", "" + listPass)
        }
        btn7.setOnClickListener {
            var size: Int = listPass.size
            if (size < 6)
                listPass.add(7)
            password()
            Log.i("tag", "" + listPass)
        }
        btn8.setOnClickListener {
            var size: Int = listPass.size
            if (size < 6)
                listPass.add(8)
            password()
            Log.i("tag", "" + listPass)
        }
        btn9.setOnClickListener {
            var size: Int = listPass.size
            if (size < 6)
                listPass.add(9)
            password()
            Log.i("tag", "" + listPass)

        }
        if (listPass.size == 6) {
            btn0.isEnabled
            btn1.isEnabled
            btn2.isEnabled
            btn3.isEnabled
            btn4.isEnabled
            btn5.isEnabled
            btn6.isEnabled
            btn7.isEnabled
            btn8.isEnabled
            btn9.isEnabled
        }

//}else{
//    btn0.isEnabled
//    btn1.isEnabled
//    btn2.isEnabled
//    btn3.isEnabled
//    btn4.isEnabled
//    btn5.isEnabled
//    btn6.isEnabled
//    btn7.isEnabled
//    btn8.isEnabled
//    btn9.isEnabled


    }

    fun password() {
        if (listPass.size == 1) {
            pass1.setImageResource(R.drawable.circle_password2)
        }
        if (listPass.size == 2) {
            pass2.setImageResource(R.drawable.circle_password2)
        }
        if (listPass.size == 3) {
            pass3.setImageResource(R.drawable.circle_password2)
        }
        if (listPass.size == 4) {
            pass4.setImageResource(R.drawable.circle_password2)
        }
        if (listPass.size == 5) {
            pass5.setImageResource(R.drawable.circle_password2)
        }
        if (listPass.size == 6) {
            pass6.setImageResource(R.drawable.circle_password2)
        }
        if (listPass.size == 6) {
            var pas: Int = listPass[0]
            var pas1: Int = listPass[1]
            var pas2: Int = listPass[2]
            var pas3: Int = listPass[3]
            var pas4: Int = listPass[4]
            var pas5: Int = listPass[5]

            var password: String = "$pas$pas1$pas2$pas3$pas4$pas5"
            var x = AppConfig.getPassord(applicationContext)

            if (password == x && wmpass != null) {
                val handler = android.os.Handler()
                handler.postDelayed({ wmpass!!.removeView(passView) }, 300)

            } else {
                val animShake: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.shake)
                ll_circle_pass.startAnimation(animShake)
                val handler = android.os.Handler()
                handler.postDelayed({

                    pass1.setImageResource(R.drawable.circle_password)
                    pass2.setImageResource(R.drawable.circle_password)
                    pass3.setImageResource(R.drawable.circle_password)
                    pass4.setImageResource(R.drawable.circle_password)
                    pass5.setImageResource(R.drawable.circle_password)
                    pass6.setImageResource(R.drawable.circle_password)
                }, 100)

                listPass.clear()

            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun createLockScreen() {

        mView = MyGroupView(applicationContext)
        val view: View = View.inflate(applicationContext, com.vunhiem.lockscreenios.R.layout.lock_layout, mView)
        linearLayout = view.findViewById(com.vunhiem.lockscreenios.R.id.ln_lock)
        setFullScreen()
        rlCamera = view.findViewById(com.vunhiem.lockscreenios.R.id.rl_camera)
        rlFlash = view.findViewById(R.id.rl_flash)
        tvTime = view.findViewById(com.vunhiem.lockscreenios.R.id.tv_time)
        tvDate = view.findViewById(com.vunhiem.lockscreenios.R.id.tv_date)
        tvPin = view.findViewById(R.id.tv_pin)
        imgPin = view.findViewById(R.id.img_pin)
        rvNotification = view.findViewById(R.id.rv_notifi)
        imgClear = view.findViewById(R.id.img_clear)
        ll_frame = view.findViewById(R.id.ll_frame)

        imgBackgroundLock = view.findViewById(R.id.img_background_main)




        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mParams = WindowManager.LayoutParams()
            mParams!!.width = WindowManager.LayoutParams.MATCH_PARENT
            mParams!!.height = WindowManager.LayoutParams.MATCH_PARENT
            mParams!!.flags = (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_DIM_BEHIND
                    or WindowManager.LayoutParams.FLAG_FULLSCREEN)
            mParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            PixelFormat.TRANSLUCENT
        } else {

            mParams = WindowManager.LayoutParams()
            mParams!!.width = WindowManager.LayoutParams.MATCH_PARENT
            mParams!!.height = WindowManager.LayoutParams.MATCH_PARENT
            mParams!!.gravity = Gravity.BOTTOM
            mParams!!.format = PixelFormat.TRANSLUCENT
            mParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
            mParams!!.flags = (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_DIM_BEHIND
                    or WindowManager.LayoutParams.FLAG_FULLSCREEN)

            mParams!!.x = 0
            mParams!!.y = 0
        }
        rlCamera.setOnClickListener {
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            wm!!.removeView(view)

        }
        mCamera()

        imgClear.setOnClickListener {
            listNotification.clear()
            adapter.notifyDataSetChanged()
            imgClear.setVisibility(View.INVISIBLE)
        }
        rvNotification.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, true)
        adapter =
            NotificationAdaper(applicationContext, listNotification, object : NotificationAdaper.ItemNotiListener {
                override fun onClick(pos: Int) {
                    wm!!.removeView(mView)
                    var xx: Boolean = AppConfig.getStatusPassword(applicationContext)!!
                    Log.i("tag", "onaddPass1")
                    if (xx == true) {
                        Log.i("tag", "onaddPass2")
                        createPasswordScreen()
                    }
                }
            })

        rvNotification.adapter = adapter
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rvNotification.adapter as NotificationAdaper
                adapter.removeAt(viewHolder.adapterPosition)
                checkNotifiEmpty()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(rvNotification)




        LocalBroadcastManager.getInstance(context).registerReceiver(onNotice, IntentFilter("Msg"))


        mReceiver = LockScreenStateReceiver()
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(mReceiver, filter)

    }

    fun checkNotifiEmpty() {

        if (listNotification.size == 0) {
            imgClear.setVisibility(View.INVISIBLE)
        } else {
            imgClear.setVisibility(View.VISIBLE)
        }

    }

    fun swipeUpToUnlock() {
        linearLayout.setOnTouchListener(View.OnTouchListener { view, motionEvent ->


            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    y = mParams!!.y
                    touchY = motionEvent.rawY
                    touchToMove = false
                }

                MotionEvent.ACTION_MOVE -> {
                    val delY = motionEvent.rawY - touchY
                    Log.i("hi", "$delY")
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                        mParams!!.y = (y + delY).toInt()

                        if (mParams!!.y <= 0) {
                            Log.i("hi", "${(y + delY).toInt()}")
                            wm!!.updateViewLayout(mView, mParams)

                        }
                        if (delY * delY > 40000 && mParams!!.y <= 0) {
                            touchToMove = true
                        }
                    } else {

                        mParams!!.y = (y - delY).toInt()

                        if (mParams!!.y >= 0) {
                            wm!!.updateViewLayout(mView, mParams)
                        }
                        if (delY * delY > 40000 && mParams!!.y >= 0) {
                            touchToMove = true
                        }
                    }


                }
                MotionEvent.ACTION_UP -> {
                    if (touchToMove) {
                        mParams!!.y = 0
                        wm!!.removeViewImmediate(mView)

//                        val animUp: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.up)
//                        ll_frame.startAnimation(animUp)

                        var xx: Boolean = AppConfig.getStatusPassword(applicationContext)!!
                        Log.i("tag", "onaddPass1")
                        if (xx == true) {
                            Log.i("tag", "onaddPass2")

                            createPasswordScreen()
                        }


                    } else {
                        mParams!!.y = 0
                        wm!!.updateViewLayout(mView, mParams)
//
//                        Log.i("tag","komo ${mParams!!.y}")
//                        for (i in mParams!!.y downTo 0) {
//                            val handler = Handler()
//                            handler.post(
//                                object : Runnable {
//                                    override fun run() {
//                                        Log.i("tag","giamdan ${mParams!!.y}")
//                                        handler.postDelayed(this, 300)
//                                        mParams!!.y = i
//                                        wm!!.updateViewLayout(mView, mParams)
//                                        if(i==0){
//                                            handler.removeCallbacksAndMessages(null)
//                                        }
//
//                                    }
//                                })
//
//
//                        }
                    }

                }

            }
            return@OnTouchListener true
        })
    }

//    private fun checkAudioSystem() {
//        // check và set state của chế độ rung Vibrate
//        if (AppConfig.checkAudio(applicationContext) == 1) {
//            tbMute.isChecked = true
//        } else {
//            tbMute.isChecked = false
//        }
//        val audioManager: AudioManager
//        audioManager = baseContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//
//        tbMute.setOnCheckedChangeListener { buttonView, isChecked ->
//
//            if (isChecked == true) {
//                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT)
//
//            } else {
//
//                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
//            }
//        }
//    }


    private val onNotice = object : BroadcastReceiver() {

        @SuppressLint("ResourceAsColor", "WrongConstant")
        override fun onReceive(context: Context, intent: Intent) {
            val pack = intent.getStringExtra("package")
            val title = intent.getStringExtra("title")
            val text = intent.getStringExtra("text")

            listNotification.add(Notification(pack, title, text))
            checkNotifiEmpty()
            rvNotification.scrollToPosition(listNotification.size - 1)
            adapter.notifyItemInserted(listNotification.size - 1)


        }
    }


    inner class LockScreenStateReceiver : BroadcastReceiver() {


        override fun onReceive(context: Context, intent: Intent) {

            if (intent.action == Intent.ACTION_SCREEN_OFF) {
                checkNotifiEmpty()
                setFullScreen()
                Log.i("tag", "OFF")
                if (wm != null) {
                    try {
                        wm!!.removeView(mView)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                var idWallpaper = AppConfig.getIdWallPaper(applicationContext)
                var uriWallpaper = AppConfig.getIdWallPaperUri(applicationContext)

                if (idWallpaper != null) {
                    imgBackgroundLock.setImageResource(AppConfig.getIdWallPaper(applicationContext)!!.toInt())
                }
                if (AppConfig.getIdWallPaperUri(applicationContext) != null) {
                    Picasso.with(applicationContext).load(File(uriWallpaper)).into(imgBackgroundLock)
                }

                wm!!.addView(mView, mParams)

                if (wmpass != null) {
                    try {
                        Log.i("tag", "onaddPass")
                        wmpass!!.removeView(passView)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


            }
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                Log.i("tag", "on Pin")
                setFullScreen()
                var level: Int
                level = intent.getIntExtra("level", 0)
                tvPin.text = (Integer.toString(level) + "%")
                if (level > 50) {
                    imgPin.setImageResource(R.drawable.icon_pin60)
                } else {
                    imgPin.setImageResource(R.drawable.icon_pin30)

                }

            }


        }
    }


    override fun onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver)
        }

        super.onDestroy()
    }

    private var objCameraManager: CameraManager? = null
    private var camera: Camera? = null
    private var mCameraId: String? = null
    var isFlashOn: Boolean? = null
    var y: Int = 0
    var touchY: Float = 0.0f
    lateinit var rlCamera: RelativeLayout
    lateinit var rlFlash: RelativeLayout
    private var touchToMove: Boolean = false
    lateinit var tvTime: TextView
    lateinit var tvDate: TextView
    private var wm: WindowManager? = null
    private var wmpass: WindowManager? = null
    private var mView: MyGroupView? = null
    private var passView: GroupViewPassword? = null
    private var mParams: WindowManager.LayoutParams? = null
    private var mParamsPass: WindowManager.LayoutParams? = null
    private var mReceiver: BroadcastReceiver? = null
    private var isshowPass: Boolean = false
    private lateinit var linearLayout: LinearLayout
    lateinit var tvPin: TextView
    lateinit var tvCanclePass: TextView
    lateinit var imgPin: ImageView
    lateinit var listPass: ArrayList<Int>
    var listNotification: ArrayList<Notification> = ArrayList()
    lateinit var rvNotification: RecyclerView
    lateinit var imgBackgroundLock: ImageView
    lateinit var pass1: ImageView
    lateinit var pass2: ImageView
    lateinit var pass3: ImageView
    lateinit var pass4: ImageView
    lateinit var pass5: ImageView
    lateinit var pass6: ImageView
    lateinit var ll_circle_pass: LinearLayout
    lateinit var ll_frame: FrameLayout
    lateinit var btn0: Button
    lateinit var btn1: Button
    lateinit var btn2: Button
    lateinit var btn3: Button
    lateinit var btn4: Button
    lateinit var btn5: Button
    lateinit var btn6: Button
    lateinit var btn7: Button
    lateinit var btn8: Button
    lateinit var btn9: Button
    internal lateinit var context: Context
    lateinit var tvCall: TextView
    lateinit var adapter: NotificationAdaper
    lateinit var imgClear: ImageView
    lateinit var wrapper: FrameLayout
    var cowdown:Int=30


}

