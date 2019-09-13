package hieusenpaj.com.musicapp.activity

import android.content.*
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.view.View
import android.view.animation.Animation
import hieusenpaj.com.musicapp.fragment.main.LibraryFragment
import hieusenpaj.com.musicapp.fragment.main.SearchFragment
import hieusenpaj.com.musicapp.fragment.main.FavoriteFragment
import hieusenpaj.com.musicapp.R
import kotlinx.android.synthetic.main.activity_main.*
import android.view.animation.TranslateAnimation
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import hieusenpaj.com.musicapp.R.id.sb_sound
import hieusenpaj.com.musicapp.`object`.Song
import hieusenpaj.com.musicapp.db.DatabasePlaylist
import hieusenpaj.com.musicapp.db.DatabasePlaylistSong
import hieusenpaj.com.musicapp.db.DatabaseSong
import hieusenpaj.com.musicapp.service.MusicService
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    var isUp: Boolean = false
    var isCollapsed: Boolean = false
    var isExpanted: Boolean = false

    var sharedPreferences: SharedPreferences? = null
    var edit: SharedPreferences.Editor? = null
    var updateHandler = Handler()
    var isRegistered = false

    companion object {
        var mp: MediaPlayer? = null
        var seed: Long = 0
    }

    var audioManager: AudioManager? = null
    var path: String? = null
    var name: String? = null
    var art: String? = null
    var artist: String? = null
    var favorite: Int? = null
    var dbSong = DatabaseSong(this, null)
    val dbPlaylist = DatabasePlaylist(this, null)
    val dbPlaylistSong = DatabasePlaylistSong(this, null)
    var arrayList = ArrayList<Song>()
    var arrPath = ArrayList<String>()
    var arrSong = ArrayList<Song>()
    var arrSongPlaylist = ArrayList<Song>()
    var boolean: Boolean = false
    lateinit var behavior: BottomSheetBehavior<RelativeLayout>
    var currentApiVersion: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideSystemUI()
        tv_name_song_expanded.isSelected = true
        tv_name_song_expanded.marqueeRepeatLimit = -1
        tv_name_song_collapsed.isSelected = true;
        tv_name_song_collapsed.marqueeRepeatLimit = -1
        seed = System.nanoTime()
//        openBottomSheet()

//        setUpToolbar()

        // To open the first tab as default
        sharedPreferences = getSharedPreferences("hieu", Context.MODE_PRIVATE)
        edit = sharedPreferences?.edit()
        name = sharedPreferences?.getString("name", "")
        path = sharedPreferences?.getString("path", "")
        if (sharedPreferences!!.getLong("seed", 0).toInt() == 0) {
            edit!!.putLong("seed", seed);
            edit!!.apply()
        }
        openBottomSheet()
        handlePermiss()

        val firstFragment = LibraryFragment()
        openFragment(firstFragment)

        registerReceiver(broadcastReceiver, IntentFilter("SERVICE_PLAY"))
        registerReceiver(broadcastReceiver, IntentFilter("SERVICE_NEXT"))
        registerReceiver(broadcastReceiver, IntentFilter("SERVICE_BACK"))
        registerReceiver(broadcastReceiver, IntentFilter("SERVICE_DISMISS"))
        registerReceiver(brSound, IntentFilter("android.media.VOLUME_CHANGED_ACTION"))
        registerReceiver(brTele, IntentFilter("TELEPHONE"))


        ivControl()
        shuffle()
        repeat()

        doStart()

        if (mp != null) {
            if (sharedPreferences?.getBoolean("isplay", false) == true) {
                iv_play_pause_collapsed.setImageDrawable(resources.getDrawable(R.drawable.ic_pause))
                iv_play_pause_expanded.setImageDrawable(resources.getDrawable(R.drawable.ic_pause))
            } else {
                iv_play_pause_collapsed.setImageDrawable(resources.getDrawable(R.drawable.ic_play))
                iv_play_pause_expanded.setImageDrawable(resources.getDrawable(R.drawable.ic_play))
            }
        } else {
            iv_play_pause_collapsed.setImageDrawable(resources.getDrawable(R.drawable.ic_play))
            iv_play_pause_expanded.setImageDrawable(resources.getDrawable(R.drawable.ic_play))
        }
        setSound()


    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {

        when (it.itemId) {

            R.id.navigation_library -> {
                val firstFragment = LibraryFragment()
                openFragment(firstFragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_search -> {
                val secondFragment = SearchFragment()
                openFragment(secondFragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_favourite -> {
                val thirdFragment = FavoriteFragment()
                openFragment(thirdFragment)
                return@OnNavigationItemSelectedListener true
            }
        }

        false

    }

    fun ivControl() {
        iv_play_pause_collapsed.setOnClickListener(View.OnClickListener {
            pausePlay()
        })
        iv_play_pause_expanded.setOnClickListener(View.OnClickListener { pausePlay() })
        iv_next_collapsed.setOnClickListener(View.OnClickListener {
            next()
            startIntentService("next")
        })
        iv_next_expanded.setOnClickListener(View.OnClickListener {
            next()
            startIntentService("next")
        })
        iv_back_expanded.setOnClickListener(View.OnClickListener {
            back()
            startIntentService("back")
        })
        rl_shuffle.setOnClickListener(View.OnClickListener {
            shuffleCheck()

        })
        rl_repeat.setOnClickListener(View.OnClickListener {
            repeatCheck()
        })
        iv_favorite.setOnClickListener(View.OnClickListener {
            favoriteCheck()

        })

    }

    fun favoriteCheck() {
        if (arrayList.get(sharedPreferences!!.getInt("pos", 0)).favorite == 0) {
            iv_favorite.setImageDrawable(resources.getDrawable(R.drawable.ic_favourite_click))
            dbSong?.updateFavorite(sharedPreferences!!.getString("path", ""), 1)
            arrayList.get(sharedPreferences!!.getInt("pos", 0)).favorite = 1
        } else {
            iv_favorite.setImageDrawable(resources.getDrawable(R.drawable.ic_favourite))
            dbSong?.updateFavorite(sharedPreferences!!.getString("path", ""), 0)
            arrayList.get(sharedPreferences!!.getInt("pos", 0)).favorite = 0
        }
    }


    fun repeatCheck() {
        if (sharedPreferences?.getInt("repeat", 0) == 0) {
            rl_repeat.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_shufffle_true));
            iv_repeat.setImageDrawable(resources.getDrawable(R.drawable.repeat_true))
            tv_repeat.setTextColor(Color.parseColor("#ffffff"))
            tv_repeat_1.visibility = View.GONE
            edit?.putInt("repeat", 1);
            edit?.apply()

        } else if (sharedPreferences?.getInt("repeat", 0) == 1) {
            rl_repeat.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_shufffle_true));
            iv_repeat.setImageDrawable(resources.getDrawable(R.drawable.repeat_true))
            tv_repeat.setTextColor(Color.parseColor("#ffffff"))
            tv_repeat_1.visibility = View.VISIBLE
            edit?.putInt("repeat", 2);
            edit?.apply()

        } else {
            rl_repeat.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_shuffle_false));
            iv_repeat.setImageDrawable(resources.getDrawable(R.drawable.repeat))
            tv_repeat.setTextColor(Color.parseColor("#f65856"))
            tv_repeat_1.visibility = View.GONE
            edit?.putInt("repeat", 0);
            edit?.apply()

        }
    }

    fun repeat() {
        if (sharedPreferences?.getInt("repeat", 0) == 0) {
            rl_repeat.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_shuffle_false));
            iv_repeat.setImageDrawable(resources.getDrawable(R.drawable.repeat))
            tv_repeat.setTextColor(Color.parseColor("#f65856"))
        } else if (sharedPreferences?.getInt("repeat", 0) == 1) {
            rl_repeat.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_shufffle_true));
            iv_repeat.setImageDrawable(resources.getDrawable(R.drawable.repeat_true))
            tv_repeat.setTextColor(Color.parseColor("#ffffff"))
        } else {
            rl_repeat.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_shufffle_true));
            iv_repeat.setImageDrawable(resources.getDrawable(R.drawable.repeat_true))
            tv_repeat.setTextColor(Color.parseColor("#ffffff"))
            tv_repeat_1.visibility = View.VISIBLE
        }
    }


    fun shuffleCheck() {
        if (sharedPreferences?.getBoolean("shuffle", false) == false) {
            rl_shuffle.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_shufffle_true));
            iv_shuffle.setImageDrawable(resources.getDrawable(R.drawable.shuffle_true))
            tv_shuffle.setTextColor(Color.parseColor("#ffffff"))
            edit?.putBoolean("shuffle", true)
//
//            Collections.shuffle(arrayList, Random(sharedPreferences!!.getLong("seed", 0)))
//            for (i in arrayList.indices) {
//                if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
//                    edit!!.putInt("pos", i)
//                    edit!!.apply()
//                }
//            }
            sendBrShuffle("SHUFFLE_TRUE")

            edit?.apply()

        } else {
            rl_shuffle.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_shuffle_false));
            iv_shuffle.setImageDrawable(resources.getDrawable(R.drawable.shuffle))
            tv_shuffle.setTextColor(Color.parseColor("#f65856"))
//            if (sharedPreferences!!.getString("array", "").equals("song")) {
//                arrayList = dbSong!!.getSong()
//                var position = dbSong!!.getPositionSong(sharedPreferences?.getString("path", "")!!)
//                edit!!.putInt("pos", position - 1)
//                edit!!.apply()
//            } else if (sharedPreferences!!.getString("array", "").equals("album")) {
//                arrayList = dbSong!!.getSongOfAlbum(sharedPreferences!!.getLong("albumid", 0))
//                for (i in arrayList.indices) {
//                    if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
//                        edit!!.putInt("pos", i)
//                        edit!!.apply()
//                    }
//                }
//            } else if (sharedPreferences!!.getString("array", "").equals("artist")) {
//                arrayList = dbSong!!.getSongOfArttist(sharedPreferences!!.getString("artist", ""))
//                for (i in arrayList.indices) {
//                    if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
//                        edit!!.putInt("pos", i)
//                        edit!!.apply()
//                    }
//                }
//            } else if (sharedPreferences!!.getString("array", "").equals("playlist")) {
//                arrPath = dbPlaylistSong.getPath(sharedPreferences!!.getLong("playlistId", 0))
////            arrSong = dbSong!!.getSong()
//                arrayList.clear()
//                for (i in arrPath) {
//
//                    arrSong = dbSong!!.getSongByPath(i)
//
//                    arrayList.add(arrSong.get(0))
//                }
////                arrayList= arrSongPlaylist
//                for (i in arrayList.indices) {
//                    if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
//                        edit!!.putInt("pos", i)
//                        boolean = true
//                        edit!!.apply()
//                    }
//                }
//                if (boolean == false) {
//                    edit!!.putInt("pos", 0)
//                    boolean = false
//                    edit!!.apply()
//                }
//
//            } else if (sharedPreferences?.getString("array", "").equals("favorite")) {
//                arrayList = dbSong!!.getSongFavorite()
//                if(arrayList.size>0) {
//                    for (i in arrayList.indices) {
//                        if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
//                            edit!!.putInt("pos", i)
//                            boolean = true
//                            edit!!.apply()
//                        }
//
//                    }
//                    if (boolean == false) {
//                        edit!!.putInt("pos", 0)
//                        boolean = false
//                        edit!!.apply()
//                    }
//                }else{
//                    arrayList = dbSong!!.getSong()
//                    var position = dbSong!!.getPositionSong(sharedPreferences?.getString("path", "")!!)
//                    edit!!.putInt("pos", position - 1)
//                    edit!!.putString("array","song")
//                    edit!!.apply()
//                }
//
//            }


            edit?.putBoolean("shuffle", false)
            edit!!.apply()
            sendBrShuffle("SHUFFLE_FALSE")
        }
    }

    fun shuffle() {
        if (sharedPreferences?.getBoolean("shuffle", false) == true) {
            rl_shuffle.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_shufffle_true));
            iv_shuffle.setImageDrawable(resources.getDrawable(R.drawable.shuffle_true))
            tv_shuffle.setTextColor(Color.parseColor("#ffffff"))

        } else {

            rl_shuffle.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_shuffle_false));
            iv_shuffle.setImageDrawable(resources.getDrawable(R.drawable.shuffle))
            tv_shuffle.setTextColor(Color.parseColor("#f65856"))

        }
    }

    fun songClicked(art: String, title: String, artist: String, path: String, duration: Long, array: String, favorite: Int) {
//
        updateDB()

        behavior.peekHeight = convertToPx(126)

        updateView(art, title, artist, duration, favorite)
        sendBrIvPlay()
        startIntentService("")
        doStart()

    }

    fun updateDB() {
        if (sharedPreferences!!.getString("array", "").equals("song")) {
            arrayList = dbSong!!.getSong()
        } else if (sharedPreferences!!.getString("array", "").equals("album")) {
            arrayList = dbSong!!.getSongOfAlbum(sharedPreferences!!.getLong("albumid", 0))
        } else if (sharedPreferences!!.getString("array", "").equals("artist")) {
            arrayList = dbSong!!.getSongOfArttist(sharedPreferences!!.getString("artist", ""))

        } else if (sharedPreferences!!.getString("array", "").equals("playlist")) {
            arrPath = dbPlaylistSong.getPath(sharedPreferences!!.getLong("playlistId", 0))
//            arrSong = dbSong.getSong()
            arrayList.clear()
            for (i in arrPath) {

                arrSong = dbSong!!.getSongByPath(i)

                arrayList.add(arrSong.get(0))
            }
//            arrayList = arrSongPlaylist
        } else {
            arrayList = dbSong!!.getSongFavorite()
            if (arrayList.size == 0) {
                arrayList = dbSong!!.getSong()
            }
        }
//        arrayList = dbSong.getSong()
        if (sharedPreferences?.getBoolean("shuffle", false) == true) {

            Collections.shuffle(arrayList, Random(sharedPreferences!!.getLong("seed", 0)))
            for (i in arrayList.indices) {
                if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
                    edit!!.putInt("pos", i)
                    boolean = true
                    edit!!.apply()
                }

            }
            if (boolean == false) {
                edit!!.putInt("pos", 0)
                boolean = false
                edit!!.apply()
            }


    } else
    {
        if (sharedPreferences?.getString("array", "").equals("song")) {
            arrayList = dbSong!!.getSong()
            var position = dbSong!!.getPositionSong(sharedPreferences?.getString("path", "")!!)
            edit!!.putInt("pos", position - 1)
            edit!!.apply()
        } else if (sharedPreferences?.getString("array", "").equals("album")) {
            arrayList = dbSong!!.getSongOfAlbum(sharedPreferences!!.getLong("albumid", 0))
            for (i in arrayList.indices) {
                if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
                    edit!!.putInt("pos", i)
                    edit!!.apply()
                }
            }
        } else if (sharedPreferences?.getString("array", "").equals("artist")) {
            arrayList = dbSong!!.getSongOfArttist(sharedPreferences!!.getString("artist", ""))
            for (i in arrayList.indices) {
                if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
                    edit!!.putInt("pos", i)
                    edit!!.apply()
                }
            }
        } else if (sharedPreferences?.getString("array", "").equals("playlist")) {
//                arrPath = dbPlaylistSong.getPath(sharedPreferences!!.getLong("playlistId",0))
////            arrSong = dbSong.getSong()
//                arrayList.clear()
//                for (i in arrPath) {
//
//                    arrSong = dbSong!!.getSongByPath(i)
//
//                    arrayList.add(arrSong.get(0))
//                }
//                arrayList = arrSongPlaylist
            for (i in arrayList.indices) {
                if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
                    edit!!.putInt("pos", i)
                    boolean = true
                    edit!!.apply()
                }
            }
            if (boolean == false) {
                edit!!.putInt("pos", 0)
                boolean = false
                edit!!.apply()
            }

        } else if (sharedPreferences?.getString("array", "").equals("favorite")) {
            arrayList = dbSong!!.getSongFavorite()
            if (arrayList.size > 0) {
                for (i in arrayList.indices) {
                    if (arrayList[i].path.equals(sharedPreferences?.getString("path", ""))) {
                        edit!!.putInt("pos", i)
                        boolean = true
                        edit!!.apply()
                    }

                }
                if (boolean == false) {
                    edit!!.putInt("pos", 0)
                    boolean = false
                    edit!!.apply()
                }
            } else {
                arrayList = dbSong!!.getSong()
                var position = dbSong!!.getPositionSong(sharedPreferences?.getString("path", "")!!)
                edit!!.putInt("pos", position - 1)
                edit!!.putString("array", "song")
                edit!!.apply()
            }
        }


    }
}


fun updateView(art: String, title: String, artist: String, duration: Long, favorite: Int) {
    Glide
            .with(this)
            .load(art)
            .apply(RequestOptions()
                    .placeholder(R.drawable.ic_songs)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
            )

            .transition(DrawableTransitionOptions()
                    .crossFade()
            )
            .into(iv_art_collapsed)
    Glide
            .with(this)
            .load(art)

            .apply(RequestOptions()
                    .placeholder(R.drawable.ic_songs)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
            )


            .transition(DrawableTransitionOptions()
                    .crossFade()
            )
            .into(iv_art_expanded)
    tv_name_artist_collapsed.text = artist
    tv_name_song_collapsed.text = title
//        tv_name_song_expanded.isSelected = true
//        tv_name_song_expanded.marqueeRepeatLimit = -1;
    tv_name_song_expanded.text = title

    tv_name_artist_expanded.text = artist

    tv_sb_start.text = "0:00"
//        val seconds: Int
    var secondsString: String
    val minutes = (duration.toInt() % (1000 * 60 * 60)) / (1000 * 60)
    val seconds = (duration.toInt() % (1000 * 60 * 60) % (1000 * 60) / 1000)
    if (seconds < 10) {
        secondsString = "0" + seconds;
    } else {
        secondsString = "" + seconds;
    }

    tv_sb_end.text = minutes.toString() + ":" + secondsString.toString()
//        tv_sb_end.text = min.toString() + ":" + seconds % 60

    if (sharedPreferences?.getBoolean("isplay", false) == true) {
        iv_play_pause_collapsed.setImageDrawable(resources.getDrawable(R.drawable.ic_pause))
        iv_play_pause_expanded.setImageDrawable(resources.getDrawable(R.drawable.ic_pause))
    } else {
        iv_play_pause_collapsed.setImageDrawable(resources.getDrawable(R.drawable.ic_play))
        iv_play_pause_expanded.setImageDrawable(resources.getDrawable(R.drawable.ic_play))
    }
    if (favorite == 1) {
        iv_favorite.setImageDrawable(resources.getDrawable(R.drawable.ic_favourite_click))

    } else {
        iv_favorite.setImageDrawable(resources.getDrawable(R.drawable.ic_favourite))

    }

}

fun openBottomSheet() {

    navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    behavior = BottomSheetBehavior.from(nestedScrollview)
    nestedScrollview.setOnClickListener(View.OnClickListener {

    })
    if (name.equals(null) || name.equals("")) {
        behavior.peekHeight = 0
    } else {
        behavior.peekHeight = convertToPx(126)
    }
    behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {

                    isCollapsed = true
                    rl_collapsed.visibility = View.VISIBLE
                    ll_expanded.visibility = View.GONE
                    if (isExpanted) {
                        slideUp(navigationView)
                        isExpanted = false
                    }

                }

                BottomSheetBehavior.STATE_EXPANDED -> {
                    rl_collapsed.visibility = View.GONE
                    ll_expanded.visibility = View.VISIBLE
                    isExpanted = true
                    if (isCollapsed) {
                        slideDown(navigationView)
                        isCollapsed = false
                    }
                }
//                    BottomSheetBehavior.STATE_DRAGGING ->
//                    BottomSheetBehavior.STATE_HIDDEN ->
//                    BottomSheetBehavior.STATE_SETTLING ->
            }
        }
    })
    if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
        isExpanted = true
    } else if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
        isCollapsed = true
    }
}

fun slideDown(view: View) {
    val animate = TranslateAnimation(
            0f, // fromXDelta
            0f, // toXDelta
            0f, // fromYDelta
            view.height.toFloat()) // toYDelta
    animate.duration = 700
    animate.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(p0: Animation?) {

        }

        override fun onAnimationEnd(p0: Animation?) {
            view.visibility = View.GONE
        }

        override fun onAnimationStart(p0: Animation?) {

        }

    })
    view.startAnimation(animate)

}

fun slideUp(view: View) {
    view.visibility = View.VISIBLE
    val animate = TranslateAnimation(
            0f, // fromXDelta
            0f, // toXDelta
            view.height.toFloat(), // fromYDelta
            0f)                // toYDelta
    animate.duration = 700
    view.startAnimation(animate)

}

private fun openFragment(fragment: Fragment) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.container, fragment)
//        transaction.addToBackStack(null)
    transaction.commit()
}


fun setUpToolbar() {

    // Hide action bar
    val actionBar = supportActionBar
    actionBar!!.hide()
}

private fun handlePermiss() {
    val perms = arrayOf("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.READ_PHONE_STATE")
    if (Build.VERSION.SDK_INT >= 23) {
        requestPermissions(perms, 3)
    } else {
        version()
    }

}

@RequiresApi(Build.VERSION_CODES.KITKAT)
override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
        3/*200*/ -> {
            if (grantResults[0] == 0) {
                version()

            }

            return
        }
        else -> return
    }
}

fun startIntentService(status: String) {

    val serviceIntent = Intent(this, MusicService::class.java)
    serviceIntent.putExtra("status", status)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        this.startForegroundService(serviceIntent)
    } else {
        this.startService(serviceIntent)
    }
}

fun playBC() {
    if (sharedPreferences?.getBoolean("isplay", false) == false) {
//            stopPlaying()
        iv_play_pause_collapsed.setImageDrawable(resources.getDrawable(R.drawable.ic_play))
        iv_play_pause_expanded.setImageDrawable(resources.getDrawable(R.drawable.ic_play))

    } else {
//            doStart()
        iv_play_pause_collapsed.setImageDrawable(resources.getDrawable(R.drawable.ic_pause))
        iv_play_pause_expanded.setImageDrawable(resources.getDrawable(R.drawable.ic_pause))

    }
}

fun setSound() {
    audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager;
    sb_sound.setMax(audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
    sb_sound.setProgress(audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC))
    sb_sound.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            if (p2) {

                audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, p1, 0);
            }

        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }

    })

}


var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        // internet lost alert dialog method call from here...
        //            Toast.makeText(getApplicationContext(),"hieu",Toast.LENGTH_SHORT).show();

        if (action!!.equals("SERVICE_PLAY", ignoreCase = true)) {
            playBC()
//                              Toast.makeText(getApplicationContext(),"hieu",Toast.LENGTH_SHORT).show();

        }
        if (action.equals("SERVICE_BACK", ignoreCase = true)) {
            // do your stuff to play action;
//                                Toast.makeText(context, "hieu", Toast.LENGTH_SHORT).show();
            playBC()
            updateDB()
            var song = arrayList.get(sharedPreferences!!.getInt("pos", 0))
            edit?.putLong("time", song.duration)
//                edit!!.putBoolean("isplay",true)
            edit?.apply()
            doStart()
            updateView(song.art, song.title, song.artist, song.duration, song.favorite)

            //                Toast.makeText(getApplicationContext(),"back",Toast.LENGTH_SHORT).show();
        }
        if (action.equals("SERVICE_NEXT", ignoreCase = true)) {
            // do your stuff to play action;
            //
            playBC()
            updateDB()
            var song = arrayList.get(sharedPreferences!!.getInt("pos", 0))
            edit?.putLong("time", song.duration)
//                edit!!.putBoolean("isplay",true)
            edit?.apply()
            doStart()
            updateView(song.art, song.title, song.artist, song.duration, song.favorite)

//                Toast.makeText(this@MainActivity, "nextAc", Toast.LENGTH_SHORT).show();
        }
        if (action.equals("SERVICE_DISMISS", ignoreCase = true)) {

            playBC()
        }
    }

}
var brSound: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val action = p1?.action
        // internet lost alert dialog method call from here...
        //            Toast.makeText(getApplicationContext(),"hieu",Toast.LENGTH_SHORT).show();

        if (action!!.equals("android.media.VOLUME_CHANGED_ACTION", ignoreCase = true)) {

            sb_sound.setProgress(audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC))
        }
    }

}
var brTele = object : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val action = p1?.action
        // internet lost alert dialog method call from here...
        //            Toast.makeText(getApplicationContext(),"hieu",Toast.LENGTH_SHORT).show();

        if (action!!.equals("TELEPHONE", ignoreCase = true)) {
            edit?.putBoolean("isplay", false)
            edit?.apply()
            playBC()
//                              Toast.makeText(getApplicationContext(),"hieu",Toast.LENGTH_SHORT).show();

        }
    }

}

fun hideSystemUI() {
    currentApiVersion = android.os.Build.VERSION.SDK_INT

    val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

    // This work only for android 4.4+
    if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

        window.decorView.systemUiVisibility = flags

        // Code below is to handle presses of Volume up or Volume down.
        // Without this, after pressing volume buttons, the navigation bar will
        // show up and won't hide
        val decorView = window.decorView
        decorView
                .setOnSystemUiVisibilityChangeListener { visibility ->
                    if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                        decorView.systemUiVisibility = flags
                    }
                }
    }
}

override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}

override fun onDestroy() {
    super.onDestroy()
//        if (mp != null) mp?.release()

    unregisterReceiver(broadcastReceiver)
    unregisterReceiver(brSound)
    unregisterReceiver(brTele)

}

override fun onStop() {
    super.onStop()


}

fun doStart() {

    sb_duration.setMax(sharedPreferences!!.getLong("time", 0).toInt())
//        sb_duration.setProgress(sharedPreferences!!.getLong("seek", 0).toInt())


    //        seekBar.setMax((int) sharedPreferences.getLong("time",0));

    sb_duration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            if (p2) {
                if (mp != null) {
                    mp?.seekTo(p1)
                }


            }
            var seconds: Int
            var min: Int

            seconds = (p1 / 1000)
            min = seconds / 60

            if (seconds % 60 < 10) {
                tv_sb_start.text = min.toString() + ":" + "0" + seconds % 60
            } else {
                tv_sb_start.text = min.toString() + ":" + seconds % 60
            }

        }

        override fun onStartTrackingTouch(p0: SeekBar?) {

        }

        override fun onStopTrackingTouch(p0: SeekBar?) {

        }

    })

    updateHandler = Handler()
    updateHandler.postDelayed(update, 100)

}

private val update = object : Runnable {
    override fun run() {
        if (mp != null) {
            val currentTime = mp?.getCurrentPosition()!!.toLong()
            sb_duration.setProgress(currentTime.toInt())
            val minutes = TimeUnit.MILLISECONDS.toMinutes(currentTime).toInt()
            val seconds = TimeUnit.MILLISECONDS.toSeconds(currentTime).toInt() - minutes * 60
            //            remainingTime.setText(String.format("%02d:%02d",minutes, seconds));
            edit!!.putLong("seek", currentTime)
            edit!!.apply()
            updateHandler.postDelayed(this, 100)
        }
    }
}


fun sendBrIvPlay() {
    val intent = Intent("IV_PLAY")
    sendBroadcast(intent)
}

fun sendBrShuffle(action: String) {
    val intent = Intent(action)
    sendBroadcast(intent)
}

fun next() {
    updateDB()
    var song: Song? = null
    if (sharedPreferences?.getInt("repeat", 0) != 2) {
        if (sharedPreferences!!.getInt("pos", 0) + 1 == arrayList.size) {
            song = arrayList.get(0)
            edit!!.putBoolean("isplay", false)
        } else {
            if(sharedPreferences!!.getInt("pos", 0) + 1>arrayList.size){
                song = arrayList.get(0)
            }else {
                song = arrayList.get(sharedPreferences!!.getInt("pos", 0) + 1)
                edit!!.putBoolean("isplay", true)
            }
        }
    } else {
        song = arrayList.get(sharedPreferences!!.getInt("pos", 0))

    }

    if (song != null) {
        name = song.title
        path = song.path
        art = song.art
        artist = song.artist
        favorite = song.favorite

        edit!!.putLong("time", song.duration)

        edit!!.putString("art", art)
        edit!!.putString("artist", artist)
        if (sharedPreferences?.getInt("repeat", 0) != 0) {
            edit!!.putBoolean("isplay", true);
        }
        //
        edit!!.apply()
        updateView(art!!, name!!, artist!!, song.duration, favorite!!)
    }

//        control()
    //        startService(path, name);
    doStart()
    sendBrIvPlay()
}

fun back() {
    updateDB()
    var song: Song? = null
    if (sharedPreferences?.getInt("repeat", 0) != 2) {
        if (sharedPreferences!!.getInt("pos", 0) == 0) {
            song = arrayList.get(0)
            edit!!.putBoolean("isplay", false)
        } else {
            song = arrayList.get(sharedPreferences!!.getInt("pos", 0) - 1)
            edit!!.putBoolean("isplay", true)

        }
    } else {
        song = arrayList.get(sharedPreferences!!.getInt("pos", 0))
    }
    name = song.title
    path = song.path
    art = song.art
    artist = song.artist
    favorite = song.favorite
    edit!!.putLong("time", song.duration)
    edit!!.putString("art", art)
    edit!!.putString("artist", artist)
    if (sharedPreferences?.getInt("repeat", 0) != 0) {
        edit!!.putBoolean("isplay", true);
    }
    //        editor.putBoolean("isplay", true);
    edit!!.apply()
//        control()

    updateView(art!!, name!!, artist!!, song.duration, favorite!!)

    //        startService(path, name);
    doStart()
    sendBrIvPlay()
}


fun pausePlay() {

    if (sharedPreferences?.getBoolean("isplay", false) == true) {
        iv_play_pause_collapsed.setImageDrawable(resources.getDrawable(R.drawable.ic_play))
        iv_play_pause_expanded.setImageDrawable(resources.getDrawable(R.drawable.ic_play))
        //            remoteView.setImageViewResource(R.drawable.ic_pause,R.drawable.ic_play);
        edit!!.putBoolean("isplay", false)
        edit!!.apply()
    } else {
        if (mp != null) {
            mp!!.start()
        } else {
            startIntentService("")
            //                startService(sharedPreferences.getString("path", ""), sharedPreferences.getString("name", ""));
            doStart()
        }
        edit!!.putBoolean("isplay", true)
        edit!!.apply()
        iv_play_pause_collapsed.setImageDrawable(resources.getDrawable(R.drawable.ic_pause))
        iv_play_pause_expanded.setImageDrawable(resources.getDrawable(R.drawable.ic_pause))
        //            remoteView.setImageViewResource(R.drawable.ic_play,R.drawable.ic_pause);


    }
    sendBrIvPlay()
}

fun convertToPx(dp: Int): Int {
    // Get the screen's density scale
    val scale = resources.displayMetrics.density
    // Convert the dps to pixels, based on density scale
    return (dp * scale + 0.5f).toInt()
}

fun version() {
    arrayList = listOfSongs(this)
//
    edit?.putBoolean("permission", true)
    edit?.apply()
    if (dbSong!!.getSong().size != arrayList.size) {
        dbSong.deleteId("hieusenpaj")
        for (song in arrayList) {

            dbSong!!.insertSong(song.title, song.artist, song.album, song.path, song.duration, song.albumId, song.art, System.currentTimeMillis())
        }
    } else {
        if (sharedPreferences?.getString("array", "").equals("song")) {
            arrayList = dbSong.getSong()
        } else if (sharedPreferences?.getString("array", "").equals("album")) {
            arrayList = dbSong?.getSongOfAlbum(sharedPreferences!!.getLong("albumid", 0))
        } else if (sharedPreferences?.getString("array", "").equals("artist")) {
            arrayList = dbSong!!.getSongOfArttist(sharedPreferences!!.getString("artist", ""))

        } else if (sharedPreferences?.getString("array", "").equals("playlist")) {
            arrPath = dbPlaylistSong.getPath(sharedPreferences!!.getLong("playlistId", 0))
//            arrSong = dbSong!!.getSong()
            arrayList.clear()
            for (i in arrPath) {

                arrSong = dbSong!!.getSongByPath(i)

                arrayList.add(arrSong.get(0))
            }
//                        arrayList = arrSongPlaylist

        } else if (sharedPreferences?.getString("array", "").equals("favorite")) {
            arrayList = dbSong!!.getSongFavorite()
            if (arrayList.size == 0) {
                arrayList = dbSong!!.getSong()
            }


        }

        if (sharedPreferences?.getBoolean("shuffle", false) == true) {

            Collections.shuffle(arrayList, Random(sharedPreferences!!.getLong("seed", 0)))

        }
    }
    updateView(sharedPreferences?.getString("art", "")!!, name!!, sharedPreferences?.getString("artist", "")!!,
            sharedPreferences?.getLong("time", 0)!!, arrayList.get(sharedPreferences!!.getInt("pos", 0)).favorite)

}


fun listOfSongs(context: Context?): ArrayList<Song> {
    val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
    var title: String
    var artist: String
    var album: String
    var albumart: String
    var path: String
    var _id: Long
    var time: Long
    var artistId: Int
    var albumId: Long
    val seconds: Int
    val min: Int
    val albumartData = HashMap<String, String>()
    val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.DATA)

    val content = context!!.contentResolver
    val projection1 = arrayOf(MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART)

    val albumArtCursor = content.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            projection1,
            null, null, null)//new String[]{songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))},

//        while (Objects.requireNonNull(albumArtCursor).moveToNext()) {
//
//            albumartData[albumArtCursor!!.getString(albumArtCursor.getColumnIndex(MediaStore.Audio.Albums._ID))] =
//                    albumArtCursor.getString(albumArtCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
//            //   Log.d(TAG,albumartData.get(AlbumArtCursor.getString(AlbumArtCursor.getColumnIndex(MediaStore.Audio.Albums._ID)))+"Albumdata");
//        }


    albumArtCursor!!.close()

    val songCursor = content.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection, null,
            MediaStore.Audio.Media.TITLE)

    val listOfSongs = ArrayList<Song>()
    songCursor!!.moveToFirst()

    while (!songCursor.isAfterLast) {


        _id = songCursor.getLong(songCursor.getColumnIndex(MediaStore.Audio.Media._ID))
        title = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
        artist = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
        album = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
        time = songCursor.getLong(songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
        path = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DATA))
        albumId = songCursor.getLong(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

        albumart = albumartData[songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))] + ""

        var sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        var albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
        if (time >= 1000) {
            val songData = Song(title, artist, album, path, time, albumId, albumArtUri.toString(), 0)
            listOfSongs.add(songData)
        }



        songCursor.moveToNext()
    }
    songCursor.close()
//        val listArtist = listOfSongs.groupBy {
//            it.artist
//        }
//
//        val artNames = listArtist.keys
//        for (art in artNames){
//            val list = listArtist[art]
//        }
    return listOfSongs
}
}
