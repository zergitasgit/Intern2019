package com.os13musicapp.os13musicplayer.fragment.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.os13musicapp.os13musicplayer.R
import com.os13musicapp.os13musicplayer.`object`.Song
import com.os13musicapp.os13musicplayer.activity.MainActivity
import com.os13musicapp.os13musicplayer.adapter.SongAdapter
import com.os13musicapp.os13musicplayer.db.DatabaseSong
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment : Fragment() {
    var arrayList = ArrayList<Song>()
    val filteredSong = ArrayList<Song>()
    var songAdapter: SongAdapter? = null
    var filteredAdapter: SongAdapter? = null
    var dbSong: DatabaseSong? = null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_search, container, false)
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar)
        view.toolbar.setTitle(getResources().getText(R.string.bottom_search))

        dbSong = DatabaseSong(context!!, null)
        arrayList = dbSong!!.getSong()
        view.recyclerView.layoutManager = LinearLayoutManager(context!!)
        songAdapter = SongAdapter(context!!, arrayList, object : SongAdapter.ItemSongListener {
            override fun onClick(position: Int, art: String, title: String, artist: String, path: String, duration: Long,favorite : Int) {

                editor!!.putString("path", path)
                editor!!.putString("art", art)
                editor!!.putString("artist", artist)
                editor!!.putString("name", title)

                editor!!.putLong("time", duration)
                editor!!.putBoolean("isplay", true)
                editor!!.putString("array", "song")
                editor!!.apply()


                (activity as MainActivity).songClicked(art, title, artist, path, duration, "song",favorite)
            }

        })
       view.recyclerView.adapter = songAdapter

        sharedPreferences = context!!.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {

        inflater!!.inflate(R.menu.menu, menu)

        //getting the search view from the menu
        val searchViewItem = menu!!.findItem(R.id.menuSearch)

        //getting the search view
        val searchView = searchViewItem.actionView as SearchView

        //making the searchview consume all the toolbar when open
        searchView.maxWidth = Int.MAX_VALUE

        searchView.queryHint = "Search"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                //action while typing

                //hiding the empty textview
                tvEmpty.visibility = View.GONE

                if (newText.isEmpty()) {

                    recyclerView.adapter = songAdapter

                } else {
                    filteredSong.clear()
                    for (song in arrayList) {
                        if (song.title.toLowerCase().contains(newText.toLowerCase())) {
                            filteredSong.add(song)
                        }
                    }
                    if (filteredSong.isEmpty()) {
                        //showing the empty textview when the list is empty
                        tvEmpty.visibility = View.VISIBLE
                    }
                    filteredAdapter = SongAdapter(context!!, filteredSong, object : SongAdapter.ItemSongListener {
                        override fun onClick(position: Int, art: String, title: String, artist: String, path: String, duration: Long,favorite : Int) {


                            editor!!.putString("path", path)
                            editor!!.putString("art", art)
                            editor!!.putString("artist", artist)
                            editor!!.putString("name", title)

                            editor!!.putLong("time", duration)
                            editor!!.putBoolean("isplay", true)
                            editor!!.putString("array", "song")
                            editor!!.apply()


                            (activity as MainActivity).songClicked(art, title, artist, path, duration, "song",favorite)
                        }

                    })
                    recyclerView.adapter = filteredAdapter
                }

                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                //action when type Enter
                return false
            }

        })

        super.onCreateOptionsMenu(menu, inflater);
    }

}
