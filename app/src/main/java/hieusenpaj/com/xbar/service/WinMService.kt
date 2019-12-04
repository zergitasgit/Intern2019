package hieusenpaj.com.xbar.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityService.SoftKeyboardController
import android.annotation.SuppressLint
import android.app.*
import android.app.admin.DevicePolicyManager
import android.content.*
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.os.*
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import hieusenpaj.com.xbar.R
import hieusenpaj.com.xbar.activity.MainActivity
import kotlinx.android.synthetic.main.window_manager.view.*
import android.view.inputmethod.InputMethodManager
import android.view.WindowManager
import hieusenpaj.com.xbar.AdminReceiver
import kotlin.math.abs


class WinMService : AccessibilityService(), View.OnTouchListener {

    var downX: Float = 0.toFloat()
    var downY: Float = 0.toFloat()
    var upX: Float = 0.toFloat()
    var upY: Float = 0.toFloat()
    val min_distance = 50

    var params: WindowManager.LayoutParams? = null
    var popupView: View? = null
    var sharedPreferences: SharedPreferences? = null
    var edit: SharedPreferences.Editor? = null
    var i = 0

    companion object {
        val EVENT_TYPE_ACTION_WINDOW = 32
        val ACCESSIBILITY_REQUEST_CODE = 1867
        val PACKAGE_NAME = "hieusenpaj.com.xbar"
        val ACCESSIBILITY_ID = "$PACKAGE_NAME/.service.WinMService"
        val ACTION_DISABLE_FLOATING_VIDEO = "Disable Overlay"
        val ACTION_ENABLE_FLOATING_VIDEO = "Enable Overlay"
        var windowManager: WindowManager? = null
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
    var v: Vibrator? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        v = getSystemService(android.content.Context.VIBRATOR_SERVICE) as android.os.Vibrator
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        sharedPreferences = getSharedPreferences("hieu", Context.MODE_PRIVATE)
        edit = sharedPreferences?.edit()
        edit!!.putBoolean("destroy", false)
        edit!!.apply()
        registerReceiver(broadcastReceiver, IntentFilter("STOP"))
        registerReceiver(brColor, IntentFilter("COLOR"))
        registerReceiver(brShadow, IntentFilter("SHADOW"))
        registerReceiver(brWidth, IntentFilter("WIDTH"))
        registerReceiver(brHeight, IntentFilter("HEIGHT"))
        registerReceiver(brMargin, IntentFilter("MARGIN"))
        registerReceiver(brKey, IntentFilter("KEYBOARD"))


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
            .setContentTitle("Xbar")
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle())

            .build()
        startForeground(1, notification)
        setUp()


        return START_STICKY
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
                if (abs(deltaX) > abs(deltaY)) {
                    if (abs(deltaX) > min_distance) {
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
                        onClick()
                    }
                } else if (abs(deltaX) < abs(deltaY)) {
                    if (abs(deltaY) > min_distance) {
                        // top or down
                        if (deltaY < 0) {
//                            performGlobalAction(sharedPreferences!!.getInt("right",0))
                            return false
                        }
                        if (deltaY > 0) {
                            up()
                            return true
                        }
                    } else {
                        if (deltaY > 0) {
                            onClick()
                        }
                    }
                } else {
                    onClick()

                }


                return true
            }

        }
        return false
    }

    private fun onClick() {
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

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        onDestroy()
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action


            if (action!!.equals("STOP", ignoreCase = true)) {
                if (popupView != null) {
                    if (popupView!!.windowToken != null) {
                        windowManager!!.removeViewImmediate(popupView)
                    }
                }
                windowManager = null
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
                if (sharedPreferences!!.getBoolean("cbShadow", false)) {
//                    popupView!!.tv_win.setBackgroundColor(Color.parseColor(code))
                    popupView!!.background.setColorFilter(
                        Color.parseColor(code),
                        PorterDuff.Mode.SRC_IN
                    )
                }

            }
        }
    }
    private var brShadow: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val cb = intent.extras!!.getBoolean("cb")
            if (action!!.equals("SHADOW", ignoreCase = true)) {
                if (cb) {
//                    popupView!!.tv_win.setBackgroundColor(Color.parseColor("#00FFFFFF"))
                    popupView!!.background.setColorFilter(
                        Color.parseColor("#00FFFFFF"),
                        PorterDuff.Mode.SRC_IN
                    )
                } else {
                    popupView!!.background.setColorFilter(
                        Color.parseColor(sharedPreferences!!.getString("color", "#00AEFF")),
                        PorterDuff.Mode.SRC_IN
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
                popupView!!.tv_win.requestLayout()
                windowManager!!.updateViewLayout(popupView, params)
            }
        }
    }
    private var brHeight: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val process = intent.extras!!.getInt("sbHeight")
            if (action!!.equals("HEIGHT", ignoreCase = true)) {
                popupView!!.tv_win.layoutParams.height = convertToPx(process) / 2
                popupView!!.tv_win.requestLayout()
                windowManager!!.updateViewLayout(popupView, params)

            }
        }
    }
    private var brMargin: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1!!.action
            val process = p1.extras!!.getInt("sbMargin")
            if (action!!.equals("MARGIN", ignoreCase = true)) {
                params!!.y = convertToPx(process)
                windowManager!!.updateViewLayout(popupView, params)


//                windowManager!!.updateViewLayout(popupView, params)

            }

        }
    }
    private var brKey: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1!!.action
            val on = p1.extras!!.getBoolean("on")
            if (action!!.equals("KEYBOARD", ignoreCase = true)) {
                if (on) {
                    params!!.flags = 8
                    windowManager!!.updateViewLayout(popupView, params)
                } else {
                    params!!.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                            WindowManager.LayoutParams.FLAG_SPLIT_TOUCH or
                            WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                    windowManager!!.updateViewLayout(popupView, params)
                }


            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (popupView != null) {
            if (popupView!!.windowToken != null) {
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
        unregisterReceiver(brKey)


        edit!!.putBoolean("destroy", true)
        edit!!.putBoolean("switch", false)

        edit!!.apply()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun setUp() {
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
            if (sharedPreferences!!.getBoolean("onKey", false))
                8
            else
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_SPLIT_TOUCH or
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
            ,
            PixelFormat.TRANSLUCENT
        )
        params!!.gravity = Gravity.CENTER or Gravity.BOTTOM
        params!!.x = 0
        params!!.y = convertToPx(sharedPreferences!!.getInt("sbMargin", 0))


        val layoutInflater =
            baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = layoutInflater.inflate(R.layout.window_manager, null)
        windowManager!!.addView(popupView, params)
        popupView!!.tv_win.layoutParams.height =
            (convertToPx(sharedPreferences!!.getInt("sbHeight", 50) / 2)).toInt()
        popupView!!.tv_win.layoutParams.width =
            (convertToPx(sharedPreferences!!.getInt("sbWidth", 100)))
        if (sharedPreferences!!.getBoolean("cbShadow", false)) {
//            popupView!!.tv_win.setBackgroundColor(
//                Color.parseColor(sharedPreferences!!.getString("color", "#EBEBEB")))
            popupView!!.background.setColorFilter(
                Color.parseColor(sharedPreferences!!.getString("color", "#00AEFF")),
                PorterDuff.Mode.SRC_IN
            )
        } else {
            popupView!!.background.setColorFilter(
                Color.parseColor(sharedPreferences!!.getString("color", "#00FFFFFF")),
                PorterDuff.Mode.SRC_IN
            )
        }

        windowManager!!.updateViewLayout(popupView, params)



        popupView!!.tv_win.setOnTouchListener(this)


//
//        val handler = Handler()
//
//        val r = object : Runnable {
//            override fun run() {
//                val softKeyboardController = softKeyboardController
//
//                    Toast.makeText(applicationContext, softKeyboardController.showMode.toString(), Toast.LENGTH_SHORT).show()
//
//                handler.postDelayed(this, 1000)
//            }
//        }
//
//        handler.postDelayed(r, 1000)

    }


    private fun double() {
        setUpVib()
        Toast.makeText(applicationContext, "double", Toast.LENGTH_SHORT).show()
        performGlobalAction(sharedPreferences!!.getInt("double", 0))
        if (sharedPreferences!!.getInt("double", 0) == 8) {
            lockScreen()
        }
    }

    private fun click() {
        setUpVib()
        Toast.makeText(applicationContext, "on", Toast.LENGTH_SHORT).show()
        performGlobalAction(sharedPreferences!!.getInt("on", 0))
        if (sharedPreferences!!.getInt("on", 0) == 8) {
            lockScreen()
        }
    }

    private fun right() {
        setUpVib()
        Toast.makeText(applicationContext, "right", Toast.LENGTH_SHORT).show()
        performGlobalAction(sharedPreferences!!.getInt("right", 0))
        if (sharedPreferences!!.getInt("right", 0) == 8) {
            lockScreen()
        }
    }

    private fun left() {
        setUpVib()
        Toast.makeText(applicationContext, "left", Toast.LENGTH_SHORT).show()
        performGlobalAction(sharedPreferences!!.getInt("left", 0))
        if (sharedPreferences!!.getInt("left", 0) == 8) {
            lockScreen()
        }
    }

    private fun up() {
        setUpVib()
        Toast.makeText(applicationContext, "up", Toast.LENGTH_SHORT).show()
        performGlobalAction(sharedPreferences!!.getInt("up", 0))
        if (sharedPreferences!!.getInt("up", 0) == 8) {
            lockScreen()
        }
    }

    private fun setUpVib() {
        if (!sharedPreferences!!.getBoolean("cbVibration", false)) {
            vibrate(0.toFloat())
        } else {
            vibrate((sharedPreferences!!.getInt("sbVib", 0).toFloat() / 100.toFloat()).toFloat())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground WinMService Channel",
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

    private fun vibrate(strength: Float) {
        // Vibrate for 500 milliseconds only
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v!!.vibrate(VibrationEffect.createWaveform(genVibratorPattern(strength, 100), -1))

        } else {
            v!!.vibrate(100) // deprecated in API 26
        }

    }

    private fun disVib() {
        v!!.cancel()
    }

    private fun genVibratorPattern(intensity: Float, duration: Long): LongArray {
        val dutyCycle = Math.abs(intensity * 2.0f - 1.0f)
        val hWidth = (dutyCycle * (duration - 1)).toLong() + 1
        val lWidth = (if (dutyCycle == 1.0f) 0 else 1).toLong()

        val pulseCount = (2.0f * (duration.toFloat() / (hWidth + lWidth).toFloat())).toInt()
        val pattern = LongArray(pulseCount)

        for (i in 0 until pulseCount) {
            pattern[i] =
                if (intensity < 0.5f) if (i % 2 == 0) hWidth else lWidth else if (i % 2 == 0) lWidth else hWidth
        }

        return pattern
    }

    private fun lockScreen() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        var admin = ComponentName(this, AdminReceiver::class.java)
        if (pm.isScreenOn()) {
            val policy =
                getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            if (policy.isAdminActive(admin)) {
                policy.lockNow();
            } else {
                Toast.makeText(
                    this,
                    "You must enable this app as a device administrator\n\n" +
                            "Please enable it and press back button to return here.",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN
                ).putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin)
                this.startActivity(intent)
            }

        }
    }

}