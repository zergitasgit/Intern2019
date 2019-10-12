package com.os13musicapp.os13musicplayer.fragment.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.os13musicapp.os13musicplayer.R
import com.os13musicapp.os13musicplayer.`object`.Song
import com.os13musicapp.os13musicplayer.activity.MainActivity
import com.os13musicapp.os13musicplayer.adapter.SongAdapter
import com.os13musicapp.os13musicplayer.db.DatabaseSong
import kotlinx.android.synthetic.main.fragment_favorite.view.*


class FavoriteFragment : Fragment() {
    var arrayList = ArrayList<Song>()
    var dbSong: DatabaseSong? = null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var songAdapter: SongAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View= inflater.inflate(R.layout.fragment_favorite, container, false)
        sharedPreferences = context!!.getSharedPreferences("hieu", Context.MODE_PRIVATE)

        editor = sharedPreferences!!.edit()
        dbSong = DatabaseSong(context!!, null)
        arrayList = dbSong!!.getSongFavorite()
        view.recyclerView.layoutManager = LinearLayoutManager(context!!)
        songAdapter = SongAdapter(context!!, arrayList, object : SongAdapter.ItemSongListener {
            override fun onClick(position: Int, art: String, title: String, artist: String, path: String, duration: Long,favorite : Int) {

                editor!!.putString("path", path)
                editor!!.putString("art", art)
                editor!!.putString("artist", artist)
                editor!!.putString("name", title)
                editor!!.putLong("time", duration)
                editor!!.putBoolean("isplay", true)
                editor!!.putString("array", "favorite")
                editor!!.apply()


                (activity as MainActivity).songClicked(art, title, artist, path, duration, "favorite",favorite)
            }

        })
        view.recyclerView.adapter = songAdapter

//        Toast.makeText(context, "search", Toast.LENGTH_SHORT).show();
        return view

    }
}
