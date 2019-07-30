package com.vunhiem.appnhacdemo.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.vunhiem.appnhacdemo.adapter.SongListAdapter

class PlayMusic : Service() {

    var currentPost : Int =0
    var musicDataList : ArrayList<String> = ArrayList()
    var mp:MediaPlayer?=null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        currentPost = intent!!.getIntExtra(SongListAdapter.MUSICTEMPOS, 0)
        musicDataList = intent!!.getStringArrayListExtra(SongListAdapter.MUSICLIST)

        if(mp !=null){
            mp!!.stop()
            mp!!.release()
            mp = null
        }
        mp = MediaPlayer()
         mp!!.setDataSource(musicDataList[currentPost])
        mp!!.prepare()
        mp!!.setOnPreparedListener {
         mp!!.start()
        }

        return super.onStartCommand(intent, flags, startId)
    }

//    fun creatNotification(song: Song): Notification {
//        val remoteViews = creatRemoteViews(song)
//        val i = Intent(this, PlaySongActivity::class.java)
//        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//        i.putExtra(Contants.EXTRA_OPEN_FROM_NOTI, true)
//        val uniqueInt = (System.currentTimeMillis() and 0xfffffff).toInt()
//        val intent = PendingIntent.getActivity(
//            this, uniqueInt, i,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        val builder = NotificationCompat.Builder(this)
//            .setSmallIcon(R.drawable.ic_song)
//            .setContentIntent(intent)
//        val notification = builder.build()
//        notification.contentView = remoteViews
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            notification.bigContentView = remoteViews
//        }
//        return notification
//    }
//    private fun creatPendingIntent(@ActionSong action: String): PendingIntent {
//        val intent = Intent(this, PlayMusic::class.java)
//        intent.action = action
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//    }
//
//
//    private fun creatRemoteViews(song: Song): RemoteViews? {
//
//        val remoteViews = RemoteViews(packageName, R.layout.noti_layout)
//        remoteViews.setTextViewText(R.id.tv_song, song.mSongName)
//        remoteViews.setTextViewText(R.id.tv_singer, song.mSongDuration)
//        val pendingNext = creatPendingIntent(NEXT)
//        val pendingPre = creatPendingIntent(PRE)
//        val pendingPlay = creatPendingIntent(PLAY)
//        remoteViews.setOnClickPendingIntent(R.id.image_prev, pendingPre)
//        remoteViews.setOnClickPendingIntent(R.id.image_next, pendingNext)
//        remoteViews.setOnClickPendingIntent(R.id.image_play, pendingPlay)
//        return remoteViews
//    }
}