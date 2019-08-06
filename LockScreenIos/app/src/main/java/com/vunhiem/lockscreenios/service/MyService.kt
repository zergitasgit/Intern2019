package com.vunhiem.lockscreenios.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log

import com.vunhiem.lockscreenios.screens.main.MyGroupView
import android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD

import android.os.Build
import android.R
import android.os.Handler
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import java.text.SimpleDateFormat


class MyService : Service() {
    lateinit var tvTime:TextView
    lateinit var tvDate:TextView
    private lateinit var anim:Animation
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

    }
    fun setTime() {
        val h = Handler()
        h.post(
            object : Runnable {
                override fun run() {
                    val date = System.currentTimeMillis()
                    val sdf = SimpleDateFormat("HH:mm")
                    val timedate= SimpleDateFormat("E, d/M/yyyy")
                    val dateString = sdf.format(date)
                    val datex = timedate.format(date)
                    tvDate.text=datex
                    tvTime.setText(dateString)
                    h.postDelayed(this, 1000)
                }
            })
    }
    private fun createIconView() {

        mView = MyGroupView(this)
        val view: View = View.inflate(this, com.vunhiem.lockscreenios.R.layout.lock_layout, mView)
       linearLayout = view.findViewById(com.vunhiem.lockscreenios.R.id.ln_lock)
       tvTime = view.findViewById(com.vunhiem.lockscreenios.R.id.tv_time)
        tvDate = view.findViewById(com.vunhiem.lockscreenios.R.id.tv_date)
        mParams = WindowManager.LayoutParams()
        mParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        mParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT
        mParams!!.gravity = Gravity.BOTTOM
        mParams!!.format = PixelFormat.TRANSLUCENT
        mParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
        mParams!!.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        mParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        mParams!!.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        mParams!!.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        mParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        mParams!!.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
        mParams!!.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        mParams!!.x = 0
        mParams!!.y = 0

        mReceiver = LockScreenStateReceiver()
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(mReceiver, filter)


    }

    inner class LockScreenStateReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_SCREEN_OFF) {
                Log.i("tag", "0FF")
                if (!isShowing) {
                    wm!!.addView(mView, mParams)
                  linearLayout.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
                        when (motionEvent.action){
                            MotionEvent.ACTION_UP -> {
                                anim = AnimationUtils.loadAnimation(baseContext, com.vunhiem.lockscreenios.R.anim.up)
                                linearLayout.animation = anim
                                linearLayout.animation.start()
                                wm!!.removeViewImmediate(mView)

                            }
                        }
                        return@OnTouchListener true
                    })
                    isShowing = false
                }
                else if (intent.action == Intent.ACTION_SCREEN_ON) {
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
        if (isShowing) {

            wm!!.removeViewImmediate(mView)
            isShowing = false
        }
        super.onDestroy()
    }

}