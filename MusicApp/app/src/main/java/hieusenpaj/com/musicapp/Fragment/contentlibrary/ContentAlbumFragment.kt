package hieusenpaj.com.musicapp.Fragment.contentlibrary


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

import hieusenpaj.com.musicapp.R
import hieusenpaj.com.musicapp.`object`.Song
import hieusenpaj.com.musicapp.activity.MainActivity
import hieusenpaj.com.musicapp.adapter.ContentAlbumAdapter
import hieusenpaj.com.musicapp.db.DatabaseSong
import kotlinx.android.synthetic.main.fragment_content_album.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 *
 */
class ContentAlbumFragment : Fragment() {
    var arrSong:ArrayList<Song> = ArrayList()
    var sharedPreferences: SharedPreferences? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v:View= inflater.inflate(R.layout.fragment_content_album, container, false)
        v.ll_back.setOnClickListener(View.OnClickListener {
            activity!!.onBackPressed()
        })
        sharedPreferences = context!!.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        var editor  =  sharedPreferences?.edit()

        val name = arguments!!.getString("name")
        val id = arguments!!.getInt("id")
        val art = arguments!!.getString("art")
        val artist = arguments!!.getString("artist")
        var dbSong = DatabaseSong(this.context!!, null)

        arrSong = dbSong.getSongOfAlbum(id.toLong())

        v.rv_content_album.layoutManager = LinearLayoutManager(context)
        var adpater = ContentAlbumAdapter(this!!.context!!,arrSong,object:ContentAlbumAdapter.ItemSongListener{
            override fun onClick(position: Int, art: String, title: String, artist: String, path: String, duration: Long,favorite:Int) {
                if (editor != null) {

                    editor.putString("path", path)
                    editor.putString("art", art)
                    editor.putString("artist", artist)
                    editor.putString("name", arrSong[position].title)
                    editor.putInt("pos", position)
                    editor.putLong("time", duration)
                    editor.putBoolean("isplay", true)
                    editor.putString("array","album")
                    editor.putLong("albumid",id.toLong())
                    editor.apply()
                }

                (activity as MainActivity).songClicked(art, title, artist,path,duration,"album",favorite)
            }

        })
        v.rv_content_album.adapter = adpater
        adpater.notifyDataSetChanged()

        v.tv_artists.text = artist
        v.tv_name_album.text=name
        Glide
                .with(context)
                .load(art)
                .apply(RequestOptions()
                        .placeholder(R.drawable.ic_albums)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .centerCrop()
                )
                .thumbnail(0.5f)
                .transition(DrawableTransitionOptions()
                        .crossFade()
                )
                .into(v.iv_art_content_album)

        return v
    }


}
