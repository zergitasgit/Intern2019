package hieusenpaj.com.musicapp.Fragment.library


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import hieusenpaj.com.musicapp.Fragment.contentlibrary.ContentPlayListFragment
import hieusenpaj.com.musicapp.R
import hieusenpaj.com.musicapp.`object`.Playlist
import hieusenpaj.com.musicapp.activity.NewPlaylistActivity
import hieusenpaj.com.musicapp.adapter.AdapterPlaylist
import hieusenpaj.com.musicapp.db.DatabasePlaylist
import hieusenpaj.com.musicapp.db.DatabasePlaylistSong
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
        dbPlaylist= DatabasePlaylist(context!!, null)
        view.nestedScrollview.requestFocus();
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ll_new_playlist.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, NewPlaylistActivity::class.java)
            startActivity(intent)
        })
        arrPlaylist = dbPlaylist!!.getPlaylist()

        rv_playlist.layoutManager = LinearLayoutManager(context)
        var adapter = AdapterPlaylist(context!!,arrPlaylist,object :AdapterPlaylist.ItemSongListener{
            override fun onClick(pos: Int) {
                Toast.makeText(context,"hi",Toast.LENGTH_SHORT).show()

            }

        })
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
                openFragment(contentPlayListFragment,arrPlaylist.get(pos).id,arrPlaylist.get(pos).title)

            }

        })
        rv_playlist.adapter = adapter

    }
    private fun openFragment(fragment: Fragment,id:Long,name:String) {
        val bundle = Bundle()
        bundle.putLong("id", id)
        bundle.putString("name", name)
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
        fragment.arguments = bundle
    }
}
