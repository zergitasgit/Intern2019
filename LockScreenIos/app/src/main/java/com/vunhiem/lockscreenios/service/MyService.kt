package com.vunhiem.lockscreenios.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.vunhiem.lockscreenios.R
import com.vunhiem.lockscreenios.screens.main.MyGroupView
import java.text.SimpleDateFormat


class MyService : Service() {
    var x: Int = 0
    var y: Int = 0
    var touchX: Float = 0.0f
    var touchY: Float = 0.0f
    lateinit var rlCamera: RelativeLayout
    lateinit var imgCamera: ImageView
    private var touchToMove: Boolean = false
    lateinit var tvTime: TextView
    lateinit var tvDate: TextView
    private lateinit var anim: Animation
    private var wm: WindowManager? = null
    private var mView: MyGroupView? = null
    private var mViewControl: MyGroupView? = null
    private var mParams: WindowManager.LayoutParams? = null
    private var mControlParams: WindowManager.LayoutParams? = null
    private var mcontrolView: WindowManager.LayoutParams? = null
    private var mReceiver: BroadcastReceiver? = null
    private var isShowing = false
    private lateinit var linearLayout: LinearLayout
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initview()
        setTime()
        return START_STICKY
    }

    private fun initview() {
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        createIconView()
        swipe()
        anim = AnimationUtils.loadAnimation(this, R.anim.up)

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

    private fun createIconView() {

        mView = MyGroupView(this)
        val view: View = View.inflate(this, com.vunhiem.lockscreenios.R.layout.lock_layout, mView)
        linearLayout = view.findViewById(com.vunhiem.lockscreenios.R.id.ln_lock)

        rlCamera = view.findViewById(com.vunhiem.lockscreenios.R.id.rl_camera)
        tvTime = view.findViewById(com.vunhiem.lockscreenios.R.id.tv_time)
        tvDate = view.findViewById(com.vunhiem.lockscreenios.R.id.tv_date)


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            mParams = WindowManager.LayoutParams()
            mParams!!.width = WindowManager.LayoutParams.MATCH_PARENT
            mParams!!.height = WindowManager.LayoutParams.MATCH_PARENT
            mParams!!.flags = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_FULLSCREEN
            PixelFormat.TRANSLUCENT
        } else {

            mParams = WindowManager.LayoutParams()
            mParams!!.width = WindowManager.LayoutParams.MATCH_PARENT
            mParams!!.height = WindowManager.LayoutParams.MATCH_PARENT
            mParams!!.gravity = Gravity.BOTTOM
            mParams!!.format = PixelFormat.TRANSLUCENT
            mParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
//            mParams!!.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//            mParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//            mParams!!.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
//            mParams!!.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
//            mParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//            mParams!!.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
            mParams!!.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_FULLSCREEN

            mParams!!.x = 0
            mParams!!.y = 0
        }
        rlCamera.setOnClickListener {
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            wm!!.removeViewImmediate(mView)
        }


        mReceiver = LockScreenStateReceiver()
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(mReceiver, filter)


    }

    fun swipe() {
        linearLayout.setOnTouchListener(View.OnTouchListener { view, motionEvent ->


            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    y = mParams!!.y
                    touchY = motionEvent.rawY
                    touchToMove = false
                }

                MotionEvent.ACTION_MOVE -> {

                    val delY = motionEvent.rawY - touchY
                    mParams!!.y = (y - delY).toInt()
                    wm!!.updateViewLayout(mView, mParams)

                    if (delY * delY > 40000) {
                        touchToMove = true
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (touchToMove) {
                        mParams!!.y = 0
                        linearLayout.animation = anim
                        linearLayout.animation.start()
                        wm!!.removeViewImmediate(mView)
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

    inner class LockScreenStateReceiver : BroadcastReceiver() {


        override fun onReceive(context: Context, intent: Intent) {

            if (intent.action == Intent.ACTION_SCREEN_OFF) {
                Log.i("tag", "0FF")

                wm!!.addView(mView, mParams)


                isShowing = false

                if (intent.action == Intent.ACTION_SCREEN_ON) {
                    Log.i("tag", "ON")
                    if (isShowing) {
                        wm!!.addView(mView, mParams)
                        isShowing = true
                    }
//                } else if (intent.action == Intent.ACTION_USER_PRESENT) {
//
//                    if (isShowing) {
//                        wm!!.removeViewImmediate(mView)
//                        isShowing = false
//                    }
                }
            }
        }
    }

    override fun onDestroy() {
        //unregister receiver when the service is destroy
        if (mReceiver != null) {
            unregisterReceiver(mReceiver)
        }

        //remove view if it is showing and the service is destroy
//        if (isShowing) {
//
//            wm!!.removeViewImmediate(mView)
//            isShowing = false
//        }
        super.onDestroy()
    }

}