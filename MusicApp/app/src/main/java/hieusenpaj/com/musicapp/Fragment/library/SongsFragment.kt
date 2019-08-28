package hieusenpaj.com.musicapp.Fragment.library


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import hieusenpaj.com.musicapp.R
import hieusenpaj.com.musicapp.`object`.Song
import hieusenpaj.com.musicapp.activity.MainActivity
import hieusenpaj.com.musicapp.adapter.SongAdapter
import hieusenpaj.com.musicapp.db.DatabaseSong
import kotlinx.android.synthetic.main.fragment_songs.*
import kotlinx.android.synthetic.main.fragment_songs.view.*
import java.util.*
import java.util.jar.Attributes
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 *
 */
@SuppressLint("ValidFragment")
class SongsFragment : Fragment() {
    var arrayList = ArrayList<Song>()
    var sharedPreferences: SharedPreferences? = null


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_songs, container, false)
//
        sharedPreferences = context!!.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        var editor  =  sharedPreferences?.edit()

        val dbSong = DatabaseSong(this.context!!, null)
        arrayList = dbSong.getSong()
        view.rv_song.layoutManager = LinearLayoutManager(context)
        val songAdapter: SongAdapter = SongAdapter(this.context!!, arrayList, object : SongAdapter.ItemSongListener {
            override fun onClick(position: Int, art: String, title: String, artist: String, path: String, duration: Long,favorite : Int) {
                if (editor != null) {

                    editor.putString("path", path)
                    editor.putString("art", art)
                    editor.putString("artist", artist)
                    editor.putString("name", arrayList[position].title)
                    editor.putInt("pos", position)
                    editor.putLong("time", duration)
                    editor.putBoolean("isplay", true)
                    editor.putString("array","song")
                    editor.apply()
                }

                (activity as MainActivity).songClicked(art, title, artist,path,duration,"song",favorite)
            }
        })
        view.rv_song.adapter = songAdapter

        return view
    }

}
