package com.os13musicapp.os13musicplayer.fragment.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.os13musicapp.os13musicplayer.fragment.library.AlbumFragment
import com.os13musicapp.os13musicplayer.fragment.library.ArtistsFragment
import com.os13musicapp.os13musicplayer.fragment.library.PlaylistsFragment
import com.os13musicapp.os13musicplayer.fragment.library.SongsFragment
import com.os13musicapp.os13musicplayer.R
import com.os13musicapp.os13musicplayer.`object`.Song
import com.os13musicapp.os13musicplayer.activity.MainActivity
import com.os13musicapp.os13musicplayer.adapter.AdapterRecently
import com.os13musicapp.os13musicplayer.db.DatabaseSong
import kotlinx.android.synthetic.main.fragment_first.view.*

class LibraryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    var arrSong = ArrayList<Song>()
    var sharedPreferences: SharedPreferences? = null
    var adapterRecently : AdapterRecently?= null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_first, container, false)
//        tv_playlists.setOnClickListener( View.OnClickListener {
//
//        })

        sharedPreferences = context!!.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        var editor = sharedPreferences?.edit()
        val dbSong: DatabaseSong = DatabaseSong(context!!, null)
        if (sharedPreferences?.getBoolean("permission",false)==true) {
            arrSong = dbSong.getSongRecently()
            view.rv_recently.layoutManager = GridLayoutManager(context!!,2)
             adapterRecently = AdapterRecently(context!!,arrSong,object : AdapterRecently.ItemSongListener{
                override fun onClick(position: Int, art: String, title: String, artist: String, path: String, duration: Long,favorite : Int) {
                    if (editor != null) {

                        editor.putString("path", path)
                        editor.putString("art", art)
                        editor.putString("artist", artist)
                        editor.putString("name", title)
                        editor.putInt("pos", position)
                        editor.putLong("time", duration)
                        editor.putBoolean("isplay", true)
                        editor.putString("array","song")
                        editor.apply()
                    }


                    (activity as MainActivity).songClicked(art, title, artist,path,duration,"song",favorite)
                    dbSong.updateRecently(path,System.currentTimeMillis())
                    arrSong = dbSong.getSongRecently()
//                    view.rv_recently.layoutManager = GridLayoutManager(context!!,2)
//
//                    var size  = arrSong.size
                    adapterRecently?.setListItems(arrSong)
                    adapterRecently?.notifyDataSetChanged()
//                    view.rv_recently.adapter = adapterRecently

                }

            })
            view.rv_recently.adapter = adapterRecently
        }
        val songsFragment = SongsFragment()
        val artistsFragment = ArtistsFragment()
        val albumFragment = AlbumFragment()
        val playlistsFragment = PlaylistsFragment()
        view.tv_songs.setOnClickListener(View.OnClickListener {
            openFragment(songsFragment)
        })
        view.tv_artists.setOnClickListener(View.OnClickListener {
            openFragment(artistsFragment)
        })
        view.tv_albums.setOnClickListener(View.OnClickListener {
            openFragment(albumFragment)
        })
        view.tv_playlists.setOnClickListener(View.OnClickListener {
            openFragment(playlistsFragment)
        })
        return view
    }

    private fun stratArtistFragment(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putString("data", "From Activity")
        openFragment(fragment)
        fragment.arguments = bundle
    }

    private fun openFragment(fragment: Fragment) {

        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }
}
