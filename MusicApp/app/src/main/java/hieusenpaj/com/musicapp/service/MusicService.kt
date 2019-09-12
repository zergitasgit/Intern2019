package hieusenpaj.com.musicapp.service

import android.app.*
import android.content.*
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.support.transition.Transition
import android.support.v4.app.NotificationCompat
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.AppWidgetTarget
import hieusenpaj.com.musicapp.R
import hieusenpaj.com.musicapp.R.id.sb_duration
import hieusenpaj.com.musicapp.`object`.Song
import hieusenpaj.com.musicapp.activity.MainActivity
import hieusenpaj.com.musicapp.activity.MainActivity.Companion.mp
import hieusenpaj.com.musicapp.activity.MainActivity.Companion.seed
import hieusenpaj.com.musicapp.db.DatabaseSong
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*
import com.bumptech.glide.request.target.NotificationTarget
import android.content.Context.NOTIFICATION_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.app.NotificationManager
import android.app.NotificationChannel
import android.graphics.Color
import hieusenpaj.com.musicapp.db.DatabasePlaylistSong


class MusicService : Service() {
    val CHANNEL_ID = "com.example.simpleapp"
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var remoteView: RemoteViews? = null
    var remoteViewSmall: RemoteViews? = null
    var manager: NotificationManager? = null
    var notification: Notification? = null
    var path: String? = null
    var name: String? = null
    var nameArtist: String? = null
    var art: String? = null
    var status: String? = null
    var arrayList = ArrayList<Song>()
    val dbSong = DatabaseSong(this, null)
    val dbPlaylistSong = DatabasePlaylistSong(this, null)
    var arrPath = ArrayList<String>()
    var arrSong = ArrayList<Song>()
    var arrSongPlaylist = ArrayList<Song>()
    var boolean = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sharedPreferences = getSharedPreferences("hieu", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()
        updateDB()

        val intent1 = IntentFilter()
        intent1.addAction("com.example.app.ACTION_PLAY")
        intent1.addAction("com.example.app.ACTION_BACK")
        intent1.addAction("com.example.app.ACTION_NEXT")
        intent1.addAction("com.example.app.ACTION_DISMISS")
        intent1.addAction("IV_PLAY")
        intent1.addAction("SHUFFLE_FALSE")
        intent1.addAction("SHUFFLE_TRUE")
        registerReceiver(broadcastReceiver, intent1)
        registerReceiver(broadcastReceiverIv, intent1)
        registerReceiver(broadcastReceiverShuffle, intent1)
        status = intent?.extras!!.getString("status")
        createNotificationChannel()

        updateStatus(status!!)

//        control()


        return Service.START_STICKY;
    }


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    fun updateStatus(status: String) {
        if (status.equals("next")) {
            var song: Song? = null
            if (sharedPreferences?.getInt("repeat", 0) != 2) {
                if (sharedPreferences!!.getInt("pos", 0) + 1 == arrayList.size) {
                    name = arrayList.get(0).title
                    path = arrayList.get(0).path
                    art = arrayList.get(0).art
                    nameArtist = arrayList.get(0).artist
                    editor!!.putInt("pos", 0)
                    editor!!.apply()
                } else {
                    song = arrayList.get(sharedPreferences!!.getInt("pos", 0) + 1)
                    name = song.title
                    path = song.path
                    art = song.art
                    nameArtist = song.artist
                    editor!!.putInt("pos", sharedPreferences!!.getInt("pos", 0) + 1)
                    editor!!.apply()
                }
            } else {
                song = arrayList.get(sharedPreferences!!.getInt("pos", 0))
                name = song.title
                path = song.path
                art = song.art
                nameArtist = song.artist
                editor!!.putInt("pos", sharedPreferences!!.getInt("pos", 0))
                editor!!.apply()
            }


        } else if (status.equals("back")) {
            var song: Song? = null
            if (sharedPreferences?.getInt("repeat", 0) != 2) {
                if (sharedPreferences!!.getInt("pos", 0) == 0) {
                    name = arrayList.get(0).title
                    path = arrayList.get(0).path
                    art = arrayList.get(0).art
                    nameArtist = arrayList.get(0).artist
                    editor!!.putInt("pos", 0)
                    editor!!.apply()
                } else {
                    song = arrayList.get(sharedPreferences!!.getInt("pos", 0) - 1)
                    name = song.title
                    path = song.path
                    art = song.art
                    nameArtist = song.artist
                    editor!!.putInt("pos", sharedPreferences!!.getInt("pos", 0) - 1)
                    editor!!.apply()
                }
            } else {
                song = arrayList.get(sharedPreferences!!.getInt("pos", 0))
                name = song.title
                path = song.path
                art = song.art
                nameArtist = song.artist
                editor!!.putInt("pos", sharedPreferences!!.getInt("pos", 0))
                editor!!.apply()
            }

        } else {
            name = sharedPreferences!!.getString("name", "")
            path = sharedPreferences!!.getString("path", "")
            art = sharedPreferences!!.getString("art", "")
            nameArtist = sharedPreferences!!.getString("artist", "")
        }
        control(name!!, path!!, art!!, nameArtist!!)
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP);
        val pendingIntent = PendingIntent.getActivity(this,
                100, notificationIntent, 0)

        remoteView = RemoteViews(applicationContext.packageName, R.layout.remote_view_layout)
        remoteViewSmall = RemoteViews(applicationContext.packageName, R.layout.remote_view_small_layout)
        remoteView?.setTextViewText(R.id.tv_name_song, name)
        remoteView?.setTextViewText(R.id.tv_name_artist, nameArtist)
        remoteView?.setImageViewUri(R.id.iv_art, Uri.parse(art))

        remoteViewSmall?.setTextViewText(R.id.tv_name, name)
        remoteViewSmall?.setTextViewText(R.id.tv_artist, nameArtist)
        remoteViewSmall?.setImageViewUri(R.id.iv_art, Uri.parse(art))
//        loadArt(art!!)

//        if(sharedPreferences.getBoolean("receiver",false)==false) {
        val playIntent = Intent("com.example.app.ACTION_PLAY")
        val pendingPlayIntent = PendingIntent.getBroadcast(this, 100, playIntent, 0)
        val backIntent = Intent("com.example.app.ACTION_BACK")
        val pendingBackIntent = PendingIntent.getBroadcast(this, 100, backIntent, 0)
        val nectIntent = Intent("com.example.app.ACTION_NEXT")
        val pendingNextIntent = PendingIntent.getBroadcast(this, 100, nectIntent, 0)
        val dismissIntent = Intent("com.example.app.ACTION_DISMISS")
        val pendingDismissIntent = PendingIntent.getBroadcast(this, 100, dismissIntent, 0)
        remoteView?.setOnClickPendingIntent(R.id.rl_next, pendingNextIntent)
        remoteView?.setOnClickPendingIntent(R.id.rl_back, pendingBackIntent)
        remoteView?.setOnClickPendingIntent(R.id.rl_pause_play, pendingPlayIntent)
        remoteView?.setOnClickPendingIntent(R.id.iv_dismiss, pendingDismissIntent)
        remoteViewSmall?.setOnClickPendingIntent(R.id.iv_next, pendingNextIntent)
        remoteViewSmall?.setOnClickPendingIntent(R.id.iv_pause_play, pendingPlayIntent)
        remoteViewSmall?.setOnClickPendingIntent(R.id.iv_dismiss, pendingDismissIntent)
        remoteViewSmall?.setOnClickPendingIntent(R.id.iv_back, pendingBackIntent)

//
        val notifystyle = NotificationCompat.BigPictureStyle()

        notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_songs)
                .setContentTitle(name)
                .setContentText(nameArtist)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigTextStyle())
                .setCustomBigContentView(remoteView)
                .setCustomContentView(remoteViewSmall)
                .build()
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) notification.setChannelId(youChannelID);

//        val notificationTarget :NotificationTarget = NotificationTarget(
//                applicationContext,
//                remoteView,
//                R.id.iv_art,
//                notification,
//                CHANNEL_ID)
//        notificationView.setOnClickPendingIntent(R.id.btn_play_pause_in_notification, pendingSwitchIntent);


        startForeground(1, notification)
    }
    fun updateDB(){
        if (sharedPreferences!!.getString("array", "").equals("song")) {
            arrayList = dbSong.getSong()
        } else if (sharedPreferences!!.getString("array", "").equals("album")) {
            arrayList = dbSong.getSongOfAlbum(sharedPreferences!!.getLong("albumid", 0))
        } else if (sharedPreferences!!.getString("array", "").equals("artist")) {
            arrayList = dbSong.getSongOfArttist(sharedPreferences!!.getString("artist", ""))

        }
        else if(sharedPreferences!!.getString("array","").equals("playlist")){
            arrPath = dbPlaylistSong.getPath(sharedPreferences!!.getLong("playlistId",0))
//            arrSong = dbSong.getSong()
            arrayList.clear()
            for (i in arrPath) {

                arrSong = dbSong.getSongByPath(i)

                arrayList.add(arrSong.get(0))
            }
//            arrayList = arrSongPlaylist
        }else if(sharedPreferences?.getString("array","").equals("favorite")){
            arrayList = dbSong!!.getSongFavorite()
        }

//        arrayList = dbSong.getSong()
        if (sharedPreferences?.getBoolean("shuffle", false) == true) {

            Collections.shuffle(arrayList, Random(sharedPreferences!!.getLong("seed", 0)))
            for (i in arrayList.indices) {
                if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
                    editor!!.putInt("pos", i)
                    editor!!.apply()
                }
            }

        } else {
            if (sharedPreferences!!.getString("array", "").equals("song")) {
                arrayList = dbSong.getSong()
                var position = dbSong.getPositionSong(sharedPreferences?.getString("path", "")!!)
                editor!!.putInt("pos", position - 1)
                editor!!.apply()
            } else if (sharedPreferences!!.getString("array", "").equals("album")) {
                arrayList = dbSong.getSongOfAlbum(sharedPreferences!!.getLong("albumid", 0))
                for (i in arrayList.indices) {
                    if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
                        editor!!.putInt("pos", i)
                        editor!!.apply()
                    }
                }
            } else if (sharedPreferences!!.getString("array", "").equals("artist")) {
                arrayList = dbSong.getSongOfArttist(sharedPreferences!!.getString("artist", ""))
                for (i in arrayList.indices) {
                    if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
                        editor!!.putInt("pos", i)
                        editor!!.apply()
                    }
                }
            }
            else if(sharedPreferences!!.getString("array","").equals("playlist")){
                arrPath = dbPlaylistSong.getPath(sharedPreferences!!.getLong("playlistId",0))
//            arrSong = dbSong.getSong()
                arrayList.clear()
                for (i in arrPath) {

                    arrSong = dbSong.getSongByPath(i)

                    arrayList.add(arrSong.get(0))
                }
//                arrayList = arrSongPlaylist
                for (i in arrayList.indices) {
                    if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
                        editor!!.putInt("pos", i)
                        boolean = true
                        editor!!.apply()
                    }
                }
                if (boolean==false){
                    editor!!.putInt("pos", 0)
                    boolean = false
                    editor!!.apply()
                }

            }else if(sharedPreferences?.getString("array","").equals("favorite")){
                arrayList = dbSong!!.getSongFavorite()
                for (i in arrayList.indices) {
                    if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
                        editor!!.putInt("pos", i)
                        editor!!.apply()
                    }
                }
            }


        }
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

    override fun onDestroy() {
        super.onDestroy()
        //        stopSelf();

        unregisterReceiver(broadcastReceiver)
        unregisterReceiver(broadcastReceiverIv)
        unregisterReceiver(broadcastReceiverShuffle)
        mp?.stop()
        mp?.release()
        editor?.putBoolean("isplay", false)
        editor?.apply()

    }

    private fun updatePlayPause(isPlay: Boolean?) {

        val api = Build.VERSION.SDK_INT
        // update the icon
        if (isPlay == false) {
            remoteView?.setImageViewResource(R.id.iv_play_pause, R.drawable.ic_play)
            remoteViewSmall?.setImageViewResource(R.id.iv_pause_play,R.drawable.ic_play)
        } else {
            remoteView?.setImageViewResource(R.id.iv_play_pause, R.drawable.ic_pause)
            remoteViewSmall?.setImageViewResource(R.id.iv_pause_play,R.drawable.ic_pause)
        }
        // update the title
        // update the content

//        remoteView?.setImageViewUri(R.id.iv_art, Uri.parse(sharedPreferences!!.getString("art", "")))
//        remoteViewSmall?.setImageViewUri(R.id.iv_art, Uri.parse(sharedPreferences!!.getString("art", "")))
        // update the notification
        if (api >= Build.VERSION_CODES.HONEYCOMB) {

            startForeground(1, notification)
        }else{
            manager?.notify(1, notification)
        }

    }

    private fun updateNextBack(name: String, art: String, artist: String) {

        val api = Build.VERSION.SDK_INT
        // update the icon

        // update the title
        remoteView?.setTextViewText(R.id.tv_name_song, name)
        remoteView?.setImageViewUri(R.id.iv_art, Uri.parse(art))
        remoteView?.setTextViewText(R.id.tv_name_artist, artist)

        remoteViewSmall?.setTextViewText(R.id.tv_name, name)
        remoteViewSmall?.setImageViewUri(R.id.iv_art, Uri.parse(art))
        remoteViewSmall?.setTextViewText(R.id.tv_artist, artist)
//        loadArt(art)
        // update the content


        // update the notification

        if (api >= Build.VERSION_CODES.HONEYCOMB) {

            startForeground(1, notification)
        }else{
            manager?.notify(1, notification)
        }

    }

    internal var broadcastReceiverIv: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (action!!.equals("IV_PLAY", ignoreCase = true)) {
                if (sharedPreferences?.getBoolean("isplay", false) == true) {
                    updatePlayPause(true)
                } else {
                    updatePlayPause(false)
                    pausePlaying()
                }
            }
        }
    }
    internal var broadcastReceiverShuffle: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (action!!.equals("SHUFFLE_TRUE", ignoreCase = true)) {

                Collections.shuffle(arrayList, Random(sharedPreferences!!.getLong("seed", 0)))
                for (i in arrayList.indices) {
                    if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
                        editor!!.putInt("pos", i)
                        editor!!.apply()
                    }
                }

            } else if (action.equals("SHUFFLE_FALSE", ignoreCase = true)) {

                if (sharedPreferences!!.getString("array", "").equals("song")) {
                    arrayList = dbSong.getSong()
                    var position = dbSong.getPositionSong(sharedPreferences?.getString("path", "")!!)
                    editor!!.putInt("pos", position - 1)
                    editor!!.apply()
                } else if (sharedPreferences!!.getString("array", "").equals("album")) {
                    arrayList = dbSong.getSongOfAlbum(sharedPreferences!!.getLong("albumid", 0))
                    for (i in arrayList.indices) {
                        if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
                            editor!!.putInt("pos", i)
                            editor!!.apply()
                        }
                    }
                } else if (sharedPreferences!!.getString("array", "").equals("artist")) {
                    arrayList = dbSong.getSongOfArttist(sharedPreferences!!.getString("artist", ""))
                    for (i in arrayList.indices) {
                        if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
                            editor!!.putInt("pos", i)
                            editor!!.apply()
                        }
                    }
                }
                else if(sharedPreferences!!.getString("array","").equals("playlist")){
                    arrPath = dbPlaylistSong.getPath(sharedPreferences!!.getLong("playlistId",0))
//            arrSong = dbSong.getSong()
                    arrayList.clear()
                    for (i in arrPath) {

                        arrSong = dbSong.getSongByPath(i)

                        arrayList.add(arrSong.get(0))
                    }
//                    arrayList = arrSongPlaylist
                    for (i in arrayList.indices) {
                        if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
                            editor!!.putInt("pos",i)
                            editor!!.apply()
                        }
                    }

                } else if(sharedPreferences?.getString("array","").equals("favorite")){
                    arrayList = dbSong!!.getSongFavorite()
                    for (i in arrayList.indices) {
                        if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
                            editor!!.putInt("pos", i)
                            editor!!.apply()
                        }
                    }
                }

            }
        }
    }


    internal var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            // internet lost alert dialog method call from here...
            //            Toast.makeText(getApplicationContext(),"hieu",Toast.LENGTH_SHORT).show();
            val action = intent.action

            if (action!!.equals("com.example.app.ACTION_PLAY", ignoreCase = true)) {

                if (sharedPreferences?.getBoolean("isplay", false) == true) {
                    pausePlaying()
                    //            remoteView.setImageViewResource(R.drawable.ic_pause,R.drawable.ic_play);
                    updatePlayPause(false)
                    editor?.putBoolean("isplay", false)
                    editor?.apply()
                } else {
                    if (mp != null) {
                        mp?.start()
                    }
                    editor?.putBoolean("isplay", true)
                    editor?.apply()
                    updatePlayPause(true)

                }
                val intent = Intent("SERVICE_PLAY")
                context.sendBroadcast(intent)
                //                Toast.makeText(context, "back", Toast.LENGTH_SHORT).show();
            }
            if (action.equals("com.example.app.ACTION_BACK", ignoreCase = true)) {
                // do your stuff to play action;
//                                Toast.makeText(context, "back", Toast.LENGTH_SHORT).show();
                updateDB()
                var song: Song? = null
                if (sharedPreferences?.getInt("repeat", 0) != 2) {
                    if (sharedPreferences!!.getInt("pos", 0) == 0) {
                        song = arrayList.get(0)
                        editor!!.putBoolean("isplay", false)
                        editor!!.putInt("pos", 0)

                    } else {
                        song = arrayList.get(sharedPreferences!!.getInt("pos", 0) - 1)
                        editor!!.putBoolean("isplay", true)
                        editor!!.putInt("pos", sharedPreferences!!.getInt("pos", 0) - 1)

                    }
                } else {
                    song = arrayList.get(sharedPreferences!!.getInt("pos", 0))
                    editor!!.putInt("pos", sharedPreferences!!.getInt("pos", 0))
                }
                name = song.title
                path = song.path
                editor!!.putString("art", song.art)
                editor!!.putString("artist", song.artist)
                if (sharedPreferences?.getInt("repeat", 0) != 0) {
                    editor!!.putBoolean("isplay", true);
                }
                //                    pos = sharedPreferences.getInt("pos", 0) - 1;
                //        editor.putBoolean("isplay", true);
                editor?.apply()
                control(name!!, path!!, song.art, song.artist)
                updateNextBack(name!!, song.art, song.artist)
                updatePlayPause(sharedPreferences?.getBoolean("isplay", false))
                val intent = Intent("SERVICE_BACK")
                context.sendBroadcast(intent)


            }
            if (action.equals("com.example.app.ACTION_NEXT", ignoreCase = true)) {
                // do your stuff to play action;
                //                Toast.makeText(context, "next", Toast.LENGTH_SHORT).show();
                //                    Toast.makeText(getApplicationContext(),"next",Toast.LENGTH_SHORT).show();
                next()
            }
            if (action.equals("com.example.app.ACTION_DISMISS", ignoreCase = true)) {
//                Toast.makeText(context, "dimiss", Toast.LENGTH_SHORT).show();

                pausePlaying()
                //            remoteView.setImageViewResource(R.drawable.ic_pause,R.drawable.ic_play);
//                updatePlayPause(false)
                editor?.putBoolean("isplay", false)
                editor?.apply()
                val intent = Intent("SERVICE_DISMISS")
                context.sendBroadcast(intent)
                stopForeground(true);
                manager?.cancel(1);
            }
            //            }

        }
    }


    private fun pausePlaying() {
        if (mp != null) {
            if (mp!!.isPlaying()) {
                mp!!.pause()
                //            mp.release();
                //            mp = null;
            }
        }
    }

    private fun stopPlaying() {
        if (mp != null) {
            mp!!.stop()
            mp!!.release()
        }
    }

    fun control(name: String, path: String, art: String, artist: String) {

        if (mp != null) {
            mp!!.stop()
            mp!!.release()
        }

        mp = MediaPlayer()
        try {
            mp!!.setDataSource(path)
            mp!!.prepare()
            mp!!.start()
//            mp!!.seekTo(sharedPreferences!!.getLong("seek",0).toInt())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        editor?.putString("path", path)
        editor?.putString("name", name)
        editor?.putString("art", art)
        editor?.putString("artist", artist)
        editor?.putString("artist", artist)

        editor?.putLong("time", mp?.getDuration()!!.toLong())
        editor?.putLong("seek", mp?.getCurrentPosition()!!.toLong())
        editor?.apply()
        dbSong.updateRecently(path,System.currentTimeMillis())

        if (sharedPreferences?.getBoolean("isplay", false) == true) {

        } else {
            mp!!.pause()
        }
        if (mp != null) {
            mp!!.setOnCompletionListener(object : MediaPlayer.OnCompletionListener {
                override fun onCompletion(p0: MediaPlayer?) {
                    next()

                }

            })
        }
    }

    fun next() {
        updateDB()
        var song: Song? = null
        if (sharedPreferences?.getInt("repeat", 0) != 2) {
            if (sharedPreferences!!.getInt("pos", 0) + 1 == arrayList.size) {
                song = arrayList.get(0)
                editor!!.putBoolean("isplay", false)
                editor!!.putInt("pos", 0)

            } else {
                song = arrayList.get(sharedPreferences!!.getInt("pos", 0) + 1)
                editor!!.putBoolean("isplay", true)
                editor!!.putInt("pos", sharedPreferences?.getInt("pos", 0)!! + 1)


            }
        } else {
            song = arrayList.get(sharedPreferences!!.getInt("pos", 0))
            editor!!.putInt("pos", sharedPreferences?.getInt("pos", 0)!!)
        }
        name = song.title
        path = song.path
        editor!!.putString("art", song.art)
        editor!!.putString("artist", song.artist)
        if (sharedPreferences?.getInt("repeat", 0) != 0) {
            editor!!.putBoolean("isplay", true);
        }
        //                    pos =sharedPreferences.getInt("pos", 0) + 1;
        //        editor.putBoolean("isplay", true);
        editor?.apply()
        control(name!!, path!!, song.art, song.artist)
        updateNextBack(name!!, song.art, song.artist)
        updatePlayPause(sharedPreferences?.getBoolean("isplay", false))
        val intent = Intent("SERVICE_NEXT")
        applicationContext.sendBroadcast(intent)


    }

    fun loadArt(url: String) {
        val target = NotificationTarget(
                applicationContext,
                R.id.iv_art,
                remoteView,
                notification,
                1)

        Glide.with(applicationContext)
                .asBitmap()
                .load(url)
                .into(target)
    }

}
