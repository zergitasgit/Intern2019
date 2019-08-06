package com.example.testcontrol1

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.net.wifi.WifiManager
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Switch
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

/**
* đặt lại tên 
* kiểm tra lại thuộc tính khi nào private/ public/protected,đặt lại tên một số thuộc tính tường minh hơn
* cmt đầu function ns rõ chức năng của function
**/
class MyService : Service() {
    private var wm: WindowManager? = null
    private var mView: MyGroupView? = null
    private var mViewControl: MyGroupView? = null
    private var mParams: WindowManager.LayoutParams? = null
    private var mControlParams: WindowManager.LayoutParams? = null
    lateinit var animUp: Animation
    lateinit var animDown: Animation
    lateinit var llcontrol: LinearLayout
    var wifiManager: WifiManager? = null
    private val PREFS_NAME = "kotlincodes"


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onDestroy() {
        wm!!.removeView(mView)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initview()
        return START_STICKY
    }

    private fun initview() {
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        createIconView()
        createViewControl()
        showIcon()
        animUp = AnimationUtils.loadAnimation(this, R.anim.anim_up)
        animDown = AnimationUtils.loadAnimation(this, R.anim.anim_down)
        wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager?



    }

    private fun showIcon() {
        try {
            wm!!.removeView(mViewControl)

        } catch (e: Exception) {
            println("co loi xay ra ")
        }

        wm!!.addView(mView, mParams)

    }

    private fun showControl() {
        try {
            wm!!.removeView(mView)

        } catch (e: Exception) {
            println("co loi xay ra ")
        }

        wm!!.addView(mViewControl, mControlParams)

    }

    private fun createViewControl() {
        mViewControl = MyGroupView(this)
        val view: View = View.inflate(this, R.layout.view_control, mViewControl)
        val btnCamera: Button = view.findViewById(R.id.btn_camera)
        val btnExit: Button = view.findViewById(R.id.btn_exit)
        val swWifi: Switch = view.findViewById(R.id.sw_wifi)
        llcontrol = view.findViewById(R.id.ll_control)
        mControlParams = WindowManager.LayoutParams()
        mControlParams!!.width = WindowManager.LayoutParams.MATCH_PARENT
        mControlParams!!.height = WindowManager.LayoutParams.MATCH_PARENT
        mControlParams!!.gravity = Gravity.BOTTOM
        mControlParams!!.format = PixelFormat.TRANSLUCENT
        mControlParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
        mControlParams!!.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        mControlParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

        btnCamera.setOnClickListener {
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
            wm!!.removeView(mViewControl)
        }

        swWifi.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true) {
                wifiManager!!.isWifiEnabled = true

            } else {
                wifiManager!!.isWifiEnabled = false


            }
        }
        btnExit.setOnClickListener {
            val intent = Intent("ACTION_PICK_WIFI_NETWORK")
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
            wm!!.removeView(mViewControl)

        }
        llcontrol.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {

                }
                MotionEvent.ACTION_UP -> {
                    Log.d("test", "control_DOWN")
                    llcontrol.animation = animDown
                    llcontrol.animation.start()
                    showIcon()

                }
            }
            return@OnTouchListener true
        })


    }


    private fun createIconView() {

        mView = MyGroupView(this)
        val view: View = View.inflate(this, R.layout.icon_layout, mView)
        var llIcon: LinearLayout = view.findViewById(R.id.ll_icon)
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        var i:Int = sharedPreference.getInt("size", 0)
        llIcon.layoutParams.width=i
        mParams = WindowManager.LayoutParams()
        mParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        mParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT
        mParams!!.gravity = Gravity.BOTTOM
        mParams!!.format = PixelFormat.TRANSLUCENT
        mParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
        mParams!!.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        mParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

        llIcon.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d("test", "ACTION_DOWN")

                }
                MotionEvent.ACTION_UP -> {
                    Log.d("test", "ACTION_UP")
                    llcontrol.animation = animUp
                    llcontrol.animation.start()
                    showControl()
                }
            }
            return@OnTouchListener true
        })

    }

}
