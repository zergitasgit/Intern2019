package hieusenpaj.com.xbar.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import hieusenpaj.com.xbar.R
import hieusenpaj.com.xbar.activity.MainActivity
import kotlinx.android.synthetic.main.item_action.view.*
import kotlinx.android.synthetic.main.window_manager.view.*
import android.widget.LinearLayout




class WindownService : AccessibilityService(), View.OnTouchListener {

    var downX: Float = 0.toFloat()
    var downY: Float = 0.toFloat()
    var upX: Float = 0.toFloat()
    var upY: Float = 0.toFloat()
    val min_distance = 50
    var windowManager: WindowManager? = null
    var params: WindowManager.LayoutParams? = null
    var popupView: View? = null
    var sharedPreferences: SharedPreferences? = null
    var edit: SharedPreferences.Editor? = null
    var i = 0

    companion object {
        val EVENT_TYPE_ACTION_WINDOW = 32
        val ACCESSIBILITY_REQUEST_CODE = 1867
        val PACKAGE_NAME = "hieusenpaj.com.xbar"
        val ACCESSIBILITY_ID = "$PACKAGE_NAME/.service.WindownService"
        val ACTION_DISABLE_FLOATING_VIDEO = "Disable Overlay"
        val ACTION_ENABLE_FLOATING_VIDEO = "Enable Overlay"
    }


    override fun onInterrupt() {

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        val eventType = p0!!.getEventType()
        if (eventType == EVENT_TYPE_ACTION_WINDOW) {
            try {
                val packageName = p0.packageName
                val className = p0.className
                if (packageName != PACKAGE_NAME) {
                    if (packageName == "com.google.android.packageinstaller"
                        || packageName == "com.android.packageinstaller"
                        || packageName == "com.android.backupconfirm"
                        || packageName == "com.android.settings.cyanogenmod.superuser.MultitaskSuRequestActivity"
                        || packageName == "com.android.systemui" && className == "com.android.systemui.media.MediaProjectionPermissionActivity"
                        || Build.VERSION.SDK_INT < 24 && packageName == "com.android.systemui" && className == "android.app.AlertDialog"
                        || packageName == "com.android.settings" && className == "android.app.AlertDialog"
                    ) {


                        val intent = Intent(ACTION_DISABLE_FLOATING_VIDEO)
                        sendBroadcast(intent)
                    } else {

                        val intent = Intent(ACTION_ENABLE_FLOATING_VIDEO)
                        sendBroadcast(intent)
                    }
                }
            } catch (e: Exception) {
                // Nothing needs to be done if it fails
            }
//            disableSelf()


        }


    }


    val CHANNEL_ID = "com.example.simpleapp"
    var manager: NotificationManager? = null
    var notification: Notification? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sharedPreferences = getSharedPreferences("hieu", Context.MODE_PRIVATE)
        edit = sharedPreferences?.edit()
        edit!!.putBoolean("destroy",false)
        edit!!.apply()
        registerReceiver(broadcastReceiver, IntentFilter("STOP"))
        registerReceiver(brColor, IntentFilter("COLOR"))
        registerReceiver(brShadow, IntentFilter("SHADOW"))
        registerReceiver(brWidth, IntentFilter("WIDTH"))
        registerReceiver(brHeight, IntentFilter("HEIGHT"))
        registerReceiver(brMargin, IntentFilter("MARGIN"))



//        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
//
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP);
        val pendingIntent = PendingIntent.getActivity(
            this,
            100, notificationIntent, 0
        )

        notification = NotificationCompat.Builder(this, CHANNEL_ID)

            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle())

            .build()
        startForeground(1, notification)
        setUp()
        return Service.START_STICKY
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        this.popupView = p0
        when (p1!!.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = p1.x
                downY = p1.y
                return true
            }
            MotionEvent.ACTION_UP -> {
                upX = p1.x
                upY = p1.y

                val deltaX = downX - upX
                val deltaY = downY - upY

                //HORIZONTAL SCROLL
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (Math.abs(deltaX) > min_distance) {
                        // left or right
                        if (deltaX < 0) {
                            right()


                            return true
                        }
                        if (deltaX > 0) {
                            left()
                            return true
                        }
                    } else {
                        //not long enough swipe...
                        return false
                    }
                } else if (Math.abs(deltaX) < Math.abs(deltaY)) {
                    if (Math.abs(deltaY) > min_distance) {
                        // top or down
                        if (deltaY < 0) {
//                            performGlobalAction(sharedPreferences!!.getInt("right",0))
                            return true
                        }
                        if (deltaY > 0) {
                            up()
                            return true
                        }
                    } else {

                        return false
                    }
                } else {
                    i++
                    val handler = Handler()
                    val r = Runnable {
                        if (i != 0) {
                            click()
                        }
                        i = 0

                    }

                    if (i == 1) {
                        //Single click
                        handler.postDelayed(r, 750)
                    } else if (i == 2) {
                        double()
                        i = 0

                    }
                }


                return true
            }
        }
        return false
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        onDestroy()
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action


            if (action!!.equals("STOP", ignoreCase = true)) {
                windowManager!!.removeViewImmediate(popupView)

                stopForeground(true)
//                disableSelf()
                manager?.cancel(1)
                Toast.makeText(context, "stop", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private var brColor: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val code = intent.extras!!.getString("code")
            if (action!!.equals("COLOR", ignoreCase = true)) {
                popupView!!.tv_win.setBackgroundColor(Color.parseColor(code))

            }
        }
    }
    private var brShadow: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val cb = intent.extras!!.getBoolean("cb")
            if (action!!.equals("SHADOW", ignoreCase = true)) {
                if (cb) {
                    popupView!!.tv_win.setBackgroundColor(Color.parseColor("#00FFFFFF"))
                } else {
                    popupView!!.tv_win.setBackgroundColor(
                        Color.parseColor(
                            sharedPreferences!!.getString(
                                "color",
                                ""
                            )
                        )
                    )
                }


            }
        }
    }
    private var brWidth: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val process = intent.extras!!.getInt("sbWidth")
            if (action!!.equals("WIDTH", ignoreCase = true)) {
                popupView!!.tv_win.layoutParams.width = convertToPx(process)
                windowManager!!.updateViewLayout(popupView,params)


            }
        }
    }
    private var brHeight: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val process = intent.extras!!.getInt("sbHeight")
            if (action!!.equals("HEIGHT", ignoreCase = true)) {
                popupView!!.tv_win.layoutParams.height = convertToPx(process)/2
                windowManager!!.updateViewLayout(popupView,params)







            }
        }
    }
    private var brMargin : BroadcastReceiver = object :BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1!!.action
            val process = p1.extras!!.getInt("sbMargin")
            if (action!!.equals("MARGIN", ignoreCase = true)) {
                params!!.y = convertToPx(process)
                windowManager!!.updateViewLayout(popupView,params)

            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (popupView != null) {
            if (popupView!!.getWindowToken() != null) {
                windowManager!!.removeViewImmediate(popupView)
            }
        }
        windowManager = null
        unregisterReceiver(broadcastReceiver)
        unregisterReceiver(brColor)
        unregisterReceiver(brShadow)
        unregisterReceiver(brWidth)
        unregisterReceiver(brHeight)
        unregisterReceiver(brMargin)

        edit!!.putBoolean("destroy",true)
        edit!!.putBoolean("switch", false)

        edit!!.apply()

    }

    fun setUp() {
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params!!.gravity = Gravity.CENTER or Gravity.BOTTOM
        params!!.x = 0
        params!!.y = convertToPx(sharedPreferences!!.getInt("sbMargin",0))


        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutInflater =
            baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = layoutInflater.inflate(R.layout.window_manager, null)
        windowManager!!.addView(popupView, params)
        popupView!!.tv_win.layoutParams.height = (convertToPx(sharedPreferences!!.getInt("sbHeight",convertToPx(50)))/2).toInt()
        popupView!!.tv_win.layoutParams.width = (convertToPx(sharedPreferences!!.getInt("sbWidth",convertToPx(100))))
        if(sharedPreferences!!.getBoolean("cbShadow",false)){
            popupView!!.tv_win.setBackgroundColor(Color.parseColor(sharedPreferences!!.getString("color", "#FFFFFF")))
        }else{
            popupView!!.tv_win.setBackgroundColor(Color.parseColor("#00FFFFFF"))
        }

        popupView!!.tv_win.setOnTouchListener(this)


    }


    fun double() {
        Toast.makeText(applicationContext, "double", Toast.LENGTH_SHORT).show()
        performGlobalAction(sharedPreferences!!.getInt("double", 0))
    }

    fun click() {
        Toast.makeText(applicationContext, "on", Toast.LENGTH_SHORT).show()
        performGlobalAction(sharedPreferences!!.getInt("on", 0))
    }

    fun right() {
        Toast.makeText(applicationContext, "right", Toast.LENGTH_SHORT).show()
        performGlobalAction(sharedPreferences!!.getInt("right", 0))
    }

    fun left() {
        Toast.makeText(applicationContext, "left", Toast.LENGTH_SHORT).show()
        performGlobalAction(sharedPreferences!!.getInt("left", 0))
    }

    fun up() {
        Toast.makeText(applicationContext, "up", Toast.LENGTH_SHORT).show()

        performGlobalAction(sharedPreferences!!.getInt("up", 0))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
//            serviceChannel.lightColor = Color.BLUE
//            serviceChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            manager?.createNotificationChannel(serviceChannel)
        }


    }
    private fun convertToPx(dp: Int): Int {
        // Get the screen's density scale
        val scale = resources.displayMetrics.density
        // Convert the dps to pixels, based on density scale
        return (dp * scale + 0.5f).toInt()
    }
}