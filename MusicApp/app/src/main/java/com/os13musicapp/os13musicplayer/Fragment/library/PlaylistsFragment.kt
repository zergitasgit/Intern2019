package com.os13musicapp.os13musicplayer.fragment.library


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.os13musicapp.os13musicplayer.fragment.contentlibrary.ContentPlayListFragment
import com.os13musicapp.os13musicplayer.R
import com.os13musicapp.os13musicplayer.`object`.Playlist
import com.os13musicapp.os13musicplayer.activity.NewPlaylistActivity
import com.os13musicapp.os13musicplayer.adapter.AdapterPlaylist
import com.os13musicapp.os13musicplayer.db.DatabasePlaylist
import com.znitenda.A
import kotlinx.android.synthetic.main.fragment_playlists.*
import kotlinx.android.synthetic.main.fragment_playlists.view.*

import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class PlaylistsFragment : Fragment() {
    var dbPlaylist  : DatabasePlaylist?=null
    var arrPlaylist = ArrayList<Playlist>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view : View =  inflater.inflate(R.layout.fragment_playlists, container, false)
        view.nestedScrollview.requestFocus();
        dbPlaylist= DatabasePlaylist(context!!, null)
        arrPlaylist = dbPlaylist!!.getPlaylist()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ll_new_playlist.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, NewPlaylistActivity::class.java)
            startActivity(intent)
        })
        view.ll_back.setOnClickListener(View.OnClickListener {
                activity!!.onBackPressed()
        })


        rv_playlist.layoutManager = LinearLayoutManager(context)
        var adapter = AdapterPlaylist(context!!,arrPlaylist,object :AdapterPlaylist.ItemSongListener{
            override fun onClick(pos: Int) {

            }

        })
        A.f(activity)
        rv_playlist.adapter = adapter

    }

    private fun openFragment(fragment: Fragment) {

        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    override fun onResume() {
        super.onResume()
        val contentPlayListFragment  = ContentPlayListFragment()
        arrPlaylist = dbPlaylist!!.getPlaylist()
        var adapter = AdapterPlaylist(context!!,arrPlaylist,object :AdapterPlaylist.ItemSongListener{
            override fun onClick(pos: Int) {
                openFragment(contentPlayListFragment,arrPlaylist.get(pos).id,arrPlaylist.get(pos).title,arrPlaylist.get(pos).art)

            }

        })
        rv_playlist.adapter = adapter

    }
    private fun openFragment(fragment: Fragment,id:Long,name:String,art:String) {
        val bundle = Bundle()
        bundle.putLong("id", id)
        bundle.putString("name", name)
        bundle.putString("art", art)
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
        fragment.arguments = bundle
    }
}
