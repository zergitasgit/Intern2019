package com.lock.applock.service

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.*
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.lock.applock.R
import com.lock.applock.`object`.App
import com.lock.applock.activity.SplashActivity
import com.lock.applock.db.DbApp
import java.util.*


class LockService : Service() {
    var params: WindowManager.LayoutParams? = null
    var popupView: View? = null
    var sharedPreferences: SharedPreferences? = null
    var edit: SharedPreferences.Editor? = null
    val CHANNEL_ID = "com.lock.applock"
    var manager: NotificationManager? = null
    var notification: Notification? = null
    var windowManager: WindowManager? = null
    var pakageName = ArrayList<App>()
    var dbApp: DbApp? = null

    var timer: Timer? = null
    companion object{
        var currentApp = ""
        var previousApp = ""
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }


    override fun onCreate() {
        super.onCreate()
        dbApp = DbApp(applicationContext, null)
        pakageName = dbApp!!.getLocked()
        val intent = IntentFilter()
        intent.addAction("LOCKED")
        intent.addAction("LOCK")
        intent.addAction("PAKAGENAME")
        applicationContext.registerReceiver(broadcastReceiver, intent)
        setUp()
        createNotificationChannel()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {





        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        timer!!.cancel()
        timer = null
        if (popupView != null) {
            if (popupView!!.windowToken != null) {
                windowManager!!.removeViewImmediate(popupView)
            }
        }
        windowManager = null
    }

    private var broadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action

//
            if (action!!.equals("PAKAGENAME", ignoreCase = true)) {
                var lock = p1.extras?.getBoolean("lock")
                if (lock!!) {
//                    popupView!!.ll_pattern.visibility = View.VISIBLE
//                    windowManager!!.updateViewLayout(popupView, params)
//                    Toast.makeText(applicationContext,"true",Toast.LENGTH_SHORT).show()


                } else {
//                    popupView!!.ll_pattern.visibility = View.GONE
//                    windowManager!!.updateViewLayout(popupView, params)
//                    Toast.makeText(applicationContext,"false",Toast.LENGTH_SHORT).show()
                }
//                Toast.makeText(context, "hi", Toast.LENGTH_SHORT).show()
            } else if (action.equals("LOCKED", ignoreCase = true) || action.equals(
                    "LOCK",
                    ignoreCase = true
                )
            ) {
                pakageName = dbApp!!.getLocked()


            }
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
        val notificationIntent = Intent(this, SplashActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
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


    }

    private val updateTask: TimerTask = object : TimerTask() {
        override fun run() {

            val intent = Intent("PAKAGENAME")

            if (isConcernedAppIsInForeground() && currentApp != packageName) {
                Log.d("isConcernedAppIsInFrgnd", currentApp)
                if (!currentApp.matches(previousApp.toRegex())) {
                 previousApp = currentApp
                    intent.putExtra("lock", true)

                }else{
//                    Log.d("isConcernedAppIsInFrgnd", "false")
                }


            } else {
                intent.putExtra("lock", false)

                Log.d("isConcernedAppIsInFrgnd", currentApp)
                previousApp =""
            }

        }
    }

    fun setUp() {
        timer = Timer("LockServices")
        timer!!.schedule(updateTask, 0, 1000L)

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,

            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_SPLIT_TOUCH or
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
            ,
            PixelFormat.TRANSLUCENT
        )
        params!!.gravity = Gravity.CENTER
        params!!.x = WindowManager.LayoutParams.MATCH_PARENT
        params!!.y = WindowManager.LayoutParams.MATCH_PARENT

        val layoutInflater =
            baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = layoutInflater.inflate(R.layout.window_manager, null)
        windowManager!!.addView(popupView, params)

    }

    fun isConcernedAppIsInForeground(): Boolean {
        val manager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val task =
            manager.getRunningTasks(5)
        if (Build.VERSION.SDK_INT <= 20) {
            if (task.size > 0) {
                val componentInfo = task[0].topActivity
                var i = 0
                while (pakageName != null && i < pakageName.size) {
                    if (componentInfo.packageName == pakageName[i].packagename) {
                        currentApp =
                            pakageName[i].packagename
                        return true
                    }
                    i++
                }
            }
        } else {
            var mpackageName = manager.runningAppProcesses[0].processName
            val usage =
                applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val stats =
                usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, time)
            if (stats != null) {
                val runningTask: SortedMap<Long, UsageStats> =
                    TreeMap()
                for (usageStats in stats) {
                    runningTask[usageStats.lastTimeUsed] = usageStats
                }
                if (runningTask.isEmpty()) {

                    mpackageName = ""
                } else {
                    mpackageName = runningTask[runningTask.lastKey()]!!.packageName
                    currentApp = mpackageName

                }
            }
            var i = 0
            while (pakageName != null && i < pakageName.size) {
//                Log.d("AppCheckService", "pakageName Size" + pakageName.size)
                if (mpackageName == pakageName[i].packagename) {
                    currentApp =
                        pakageName[i].packagename
                    return true
                }
                i++
            }
        }
        return false
    }

}
