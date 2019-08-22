package com.vunhiem.lockscreenios.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ibikenavigationkotlin.utils.AppConfig
import com.vunhiem.lockscreenios.R
import com.vunhiem.lockscreenios.screens.main.GroupViewPassword
import com.vunhiem.lockscreenios.screens.main.MyGroupView
import com.vunhiem.lockscreenios.screens.notification.SwipeToDeleteCallback
import com.vunhiem.lockscreenios.screens.notification.adapter.NotificationAdaper
import org.apache.commons.lang3.StringUtils
import java.text.SimpleDateFormat


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
class NotificationService : NotificationListenerService() {

    internal lateinit var context: Context

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate() {

        super.onCreate()
        context = applicationContext

        mReceiver = LockScreenStateReceiver()
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
//        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(mReceiver, filter)
        LocalBroadcastManager.getInstance(context).registerReceiver(onNotice, IntentFilter("Msg"))

        var switchMainStatus = AppConfig.getLock(applicationContext)
        Log.i("hoho", "command$switchMainStatus")
        if (switchMainStatus == true) {
            initview()
            setTime()
        }

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1) {
            createNotificationChannel()
            val CHANNEL_ID = "1"
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_pin60)
                .setContentTitle("IosLock active")
                .setOnlyAlertOnce(true)
            notification = builder.build()
            with(NotificationManagerCompat.from(context)) { notify(NOTIFICATION_ID, notification!!) }
            startForeground(NOTIFICATION_ID, notification)
            Log.d("chan", "Start the foreground")
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initview() {
        createLockScreen()
        swipeUpToUnlock()


    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")


    fun getNameTelecom() {
        val manager: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var carrierName: String = manager.getNetworkOperatorName()
        Log.i("mang", "hi")
        Log.i("mang", "$carrierName")
        if (carrierName != null) {
            tvTelecom.text = StringUtils.capitalize(carrierName.toLowerCase().trim())
        } else {
            tvTelecom.text = "No Sim"
        }
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createLockScreen() {
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager?

        Log.i("covao", "có vào")
        mView = MyGroupView(applicationContext)
        val view: View = View.inflate(applicationContext, com.vunhiem.lockscreenios.R.layout.lock_layout, mView)
        linearLayout = view.findViewById(com.vunhiem.lockscreenios.R.id.ln_lock)

        rlCamera = view.findViewById(com.vunhiem.lockscreenios.R.id.rl_camera)
        rlFlash = view.findViewById(R.id.rl_flash)
        tvTime = view.findViewById(com.vunhiem.lockscreenios.R.id.tv_time)
        tvDate = view.findViewById(com.vunhiem.lockscreenios.R.id.tv_date)
        tvPin = view.findViewById(R.id.tv_pin)
        imgPin = view.findViewById(R.id.img_pin)
        rvNotification = view.findViewById(R.id.rv_notifi)
        imgClear = view.findViewById(R.id.img_clear)
        ll_frame = view.findViewById(R.id.ll_frame)
        viewBottom = view.findViewById(R.id.viewBottom)
        tvTelecom = view.findViewById(R.id.tv_telecom)
        imgBackgroundLock = view.findViewById(R.id.img_background_main)
        tvOpenCamera = view.findViewById(R.id.tv_opencamera)
        tvOpen = view.findViewById(R.id.tv_open)
        setFullScreen()
        getNameTelecom()




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
            var xx: Boolean = AppConfig.getStatusPassword(applicationContext)!!
            if (xx == true) {
                val anim: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.up2)
                tvOpenCamera.startAnimation(anim)
            }else{
                wm!!.removeView(mView)
            }


        }
        mCamera()

        imgClear!!.setOnClickListener {
            listNotification.clear()
            adapter.notifyDataSetChanged()
            imgClear!!.setVisibility(View.INVISIBLE)
        }
        CreateNotifiInScreenLock()

//        LocalBroadcastManager.getInstance(context).registerReceiver(onNotice, IntentFilter("Msg"))
        registerBroadReciver()

    }


    private fun registerBroadReciver() {
        mReceiver = LockScreenStateReceiver()
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(mReceiver, filter)
    }

    @SuppressLint("WrongConstant")
    private fun CreateNotifiInScreenLock() {
        rvNotification.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, true)
        adapter =
            NotificationAdaper(
                applicationContext,
                listNotification,
                object : NotificationAdaper.ItemNotiListener {
                    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            val name = "floating_window_noti_channel"
            val descriptionText = "A cool channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply { description = descriptionText }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun swipeUpToUnlock() {


        linearLayout.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            val xxxx: Int = applicationContext.getResources().getDisplayMetrics().heightPixels

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    y = mParams!!.y
                    touchY = motionEvent.rawY
                    Log.i("tag", "yy$touchY")
                    touchToMove = false
                }

                MotionEvent.ACTION_MOVE -> {

                    if (touchY > (xxxx - 50)) {
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


                }
                MotionEvent.ACTION_UP -> {
                    touchY = motionEvent.rawY
                    Log.i("tag", "up$touchY")
                    if (touchToMove && touchY < xxxx - 150) {

                        mParams!!.y = 0
//                        val animUp: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.unlock)
//                        ll_frame.startAnimation(animUp)
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            val handler = Handler()
                            val animUp: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.unlock)
                        ll_frame.startAnimation(animUp)
                            handler.postDelayed({
                                wm!!.removeView(mView)
                                var xx: Boolean = AppConfig.getStatusPassword(applicationContext)!!
                                Log.i("tag", "onaddPass1")
                                if (xx == true) {
                                    Log.i("tag", "onaddPass2")
                                    createPasswordScreen()
                                    val anim: Animation = AnimationUtils.loadAnimation(this, R.anim.up)
                                    layouPass.startAnimation(anim)
                                }
                            }, 500)

                        } else {
                            wm!!.removeView(mView)
                            var xx: Boolean = AppConfig.getStatusPassword(applicationContext)!!
                            Log.i("tag", "onaddPass1")
                            if (xx == true) {
                                Log.i("tag", "onaddPass2")
                                createPasswordScreen()
                                val anim: Animation = AnimationUtils.loadAnimation(this, R.anim.up)
                                layouPass.startAnimation(anim)
                            }
                        }
//                            wm!!.removeViewImmediate(mView)

//                        val animUp: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.up)
//                        ll_frame.startAnimation(animUp)


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

    private fun disableImportPass() {
        if (countPass >= 5) {
            btn0.isEnabled = false
            btn1.isEnabled = false
            btn2.isEnabled = false
            btn3.isEnabled = false
            btn4.isEnabled = false
            btn5.isEnabled = false
            btn6.isEnabled = false
            btn7.isEnabled = false
            btn8.isEnabled = false
            btn9.isEnabled = false
            tvNotifiPass.text = "Error code 5 time, Try after $cowdown second"
        } else {
            tvNotifiPass.text = "Touch ID or Password"
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
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingPermission")
    private fun createPasswordScreen() {
        wmpass = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        passView = GroupViewPassword(applicationContext)
        val v: View
        if (Build.VERSION.SDK_INT <Build.VERSION_CODES.LOLLIPOP){
            v = View.inflate(applicationContext, com.vunhiem.lockscreenios.R.layout.layout_password2, passView)
        }else{
        v = View.inflate(applicationContext, com.vunhiem.lockscreenios.R.layout.layout_password, passView)}

        tvCanclePass = v.findViewById(R.id.tv_cancle_pass)
        tvCall = v.findViewById(R.id.tv_call)
        tvPin = v.findViewById(R.id.tv_pin)
        imgPin = v.findViewById(R.id.img_pin)
        ll_circle_pass = v.findViewById(R.id.ln_circle_pass)
        tvTelecom = v.findViewById(R.id.tv_telecom)
        pass1 = v.findViewById(R.id.pass1)
        pass2 = v.findViewById(R.id.pass2)
        pass3 = v.findViewById(R.id.pass3)
        pass4 = v.findViewById(R.id.pass4)
        pass5 = v.findViewById(R.id.pass5)
        pass6 = v.findViewById(R.id.pass6)
        layouPass = v.findViewById(R.id.ln_layoutpass)
        tvNotifiPass = v.findViewById(R.id.tv_notification)



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
        disableImportPass()
        getNameTelecom()
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


        }
        registerBroadReciver()

        listPass = ArrayList()
        setOnclickScreenPass()

    }

    private fun setOnclickScreenPass() {

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
            btn0.isEnabled = false
            btn1.isEnabled = false
            btn2.isEnabled = false
            btn3.isEnabled = false
            btn4.isEnabled = false
            btn5.isEnabled = false
            btn6.isEnabled = false
            btn7.isEnabled = false
            btn8.isEnabled = false
            btn9.isEnabled = false
        }

    }

    fun checkNotifiEmpty() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (listNotification.size == 0) {
                imgClear!!.setVisibility(View.INVISIBLE)
            } else {
                imgClear!!.setVisibility(View.VISIBLE)
            }

        }else{
            imgClear!!.setVisibility(View.INVISIBLE)
        }
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

            if (password == x && wmpass != null && countPass < 5) {
//                val handler = android.os.Handler()
//                handler.postDelayed({ wmpass!!.removeView(passView) }, 300)
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1) {
                    wmpass!!.removeView(passView)
                } else {
                    val handler = Handler()
                    handler.postDelayed({
                        wmpass!!.removeView(passView)
                    }, 300)
                }

            } else {
                countPass++
                Log.i("tu", "$countPass")
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
                if (countPass == 5) {
                    val handlerxxx = Handler()
                    handlerxxx.postDelayed({
                        cowdown--
                    }, 300)

                    tvNotifiPass.text = " Error code 5 time, Try after 30 second "
                    btn0.isEnabled = false
                    btn1.isEnabled = false
                    btn2.isEnabled = false
                    btn3.isEnabled = false
                    btn4.isEnabled = false
                    btn5.isEnabled = false
                    btn6.isEnabled = false
                    btn7.isEnabled = false
                    btn8.isEnabled = false
                    btn9.isEnabled = false
                }
                val handlerx = Handler()
                handlerx.postDelayed({
                    tvNotifiPass.text = "Touch ID or Password"
                    countPass = 0
                    btn0.isEnabled = true
                    btn1.isEnabled = true
                    btn2.isEnabled = true
                    btn3.isEnabled = true
                    btn4.isEnabled = true
                    btn5.isEnabled = true
                    btn6.isEnabled = true
                    btn7.isEnabled = true
                    btn8.isEnabled = true
                    btn9.isEnabled = true
                }, 30000)

            }
        }
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
        if(mView!=null) {
            mView!!.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar

                        or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar

                        or View.SYSTEM_UI_FLAG_IMMERSIVE
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun mCamera() {
        isFlashOn = false
        objCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    mCameraId = objCameraManager!!.cameraIdList[0]}


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
//    }

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onNotificationPosted(sbn: StatusBarNotification) {

        val pack = sbn.packageName
        val extras = sbn.notification.extras
        val title: String? = extras.getString("android.title")
        val text: String? = extras.getCharSequence("android.text").toString()

//        val text="hihi"

        Log.i("Package", pack)
        Log.i("Title", title)
        Log.i("Text", text)


        val msgrcv = Intent("Msg")

        if (pack != "android" && pack != "com.android.systemui" && title != null) {
            msgrcv.putExtra("package", pack)
            msgrcv.putExtra("title", title)
            msgrcv.putExtra("text", text)
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv)


    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.i("Msg", "Notification Removed")

    }

    private val onNotice = object : BroadcastReceiver() {

        @SuppressLint("ResourceAsColor", "WrongConstant")
        override fun onReceive(context: Context, intent: Intent) {

            val pack = intent.getStringExtra("package")
            val title = intent.getStringExtra("title")
            val text = intent.getStringExtra("text")
            if (pack != null) {
                listNotification.add(com.vunhiem.lockscreenios.model.Notification(pack, title, text))
                checkNotifiEmpty()
                rvNotification.scrollToPosition(listNotification.size - 1)
                adapter.notifyItemInserted(listNotification.size - 1)
            }

        }
    }


    inner class LockScreenStateReceiver : BroadcastReceiver() {


        override fun onReceive(context: Context, intent: Intent) {

            if (intent.action == Intent.ACTION_SCREEN_OFF) {
                var switchMainStatus = AppConfig.getLock(applicationContext)
                Log.i("hoho", "$switchMainStatus")
                if (switchMainStatus == true) {
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
                        val uri: Uri = Uri.parse(AppConfig.getIdWallPaperUri(applicationContext))
                        imgBackgroundLock.setImageURI(uri)

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

            }
            if (intent.action == Intent.ACTION_SCREEN_ON){

                val anim: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.textview2)
                tvOpen.startAnimation(anim)

            }
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                if (tvPin!=null) {
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
    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        unregisterReceiver(onNotice)
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
    var listNotification: ArrayList<com.vunhiem.lockscreenios.model.Notification> = ArrayList()
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
    lateinit var tvCall: TextView
    lateinit var adapter: NotificationAdaper
    private var imgClear: ImageView? = null
    lateinit var viewBottom: Button
    lateinit var tvTelecom: TextView
    lateinit var layouPass: LinearLayout
    var countPass: Int = 0
    lateinit var tvNotifiPass: TextView
    var cowdown: Int = 30
    private val NOTIFICATION_ID = 144
    private var notification: Notification? = null
    val CHANNEL_ID = "1"
    lateinit var tvOpenCamera:TextView
    lateinit var tvOpen:TextView
}