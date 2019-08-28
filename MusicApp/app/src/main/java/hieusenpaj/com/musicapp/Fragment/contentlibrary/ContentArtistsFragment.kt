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
import hieusenpaj.com.musicapp.adapter.ContentArtistAdapter
import hieusenpaj.com.musicapp.db.DatabaseSong
import kotlinx.android.synthetic.main.fragment_content_artists.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 *
 */
class ContentArtistsFragment : Fragment() {
    var arrSong:ArrayList<Song> = ArrayList()
    var sharedPreferences: SharedPreferences? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
       var  view : View= inflater.inflate(R.layout.fragment_content_artists, container, false)
        sharedPreferences = context!!.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        var editor  =  sharedPreferences?.edit()
        val name = arguments!!.getString("name")
        val track = arguments!!.getInt("track")
        val art = arguments!!.getString("art")

        val dbSong = DatabaseSong(this.context!!, null)
        arrSong = dbSong.getSongOfArttist(name)
        view.rv_content_artist.layoutManager = LinearLayoutManager(context)
        var adapter = ContentArtistAdapter(this!!.context!!,arrSong,object :ContentArtistAdapter.ItemSongListener{
            override fun onClick(position: Int, art: String, title: String, artist: String, path: String, duration: Long,favorite:Int) {
                if (editor != null) {

                    editor.putString("path", path)
                    editor.putString("art", art)
                    editor.putString("artist", artist)
                    editor.putString("name", arrSong[position].title)
                    editor.putInt("pos", position)
                    editor.putLong("time", duration)
                    editor.putBoolean("isplay", true)
                    editor.putString("array","artist")
//                    editor.putLong("albumid",id.toLong())
                    editor.apply()
                }

                (activity as MainActivity).songClicked(art, title, artist,path,duration,"artist",favorite)
            }

        })
        view.rv_content_artist.adapter=adapter



        view.tv_name_artist.text=name
//        view.tv_number_song.text=track.toString()
        Glide
                .with(context)
                .load(art)
                .apply(RequestOptions()
                        .placeholder(R.drawable.album_art)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                )
                .thumbnail(0.5f)
                .transition(DrawableTransitionOptions()
                        .crossFade()
                )
                .into(view.iv_art_content)
        return view
    }


}
