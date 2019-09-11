package hieusenpaj.com.musicapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import hieusenpaj.com.musicapp.R
import hieusenpaj.com.musicapp.`object`.Song
import kotlinx.android.synthetic.main.adapter_song.view.*

class SongAdapter(
        private val context: Context
        , private var arrSong: ArrayList<Song>,
        private val listener: ItemSongListener


) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
//    var songs: ArrayList<Song>?=null


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SongAdapter.SongViewHolder {
        return SongViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_song, p0, false))
    }

    override fun getItemCount(): Int = arrSong.size
    override fun onBindViewHolder(p0: SongAdapter.SongViewHolder, p1: Int) {
        var song =arrSong.get(p1)
        p0.tvName.text = song.title
//        p0.tvAlbum.text = song.album
        p0.tvArtist.text = song.artist


        Glide
                .with(context)
                .load(song.art)

                .apply(RequestOptions()
                        .placeholder(R.drawable.ic_songs)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .centerCrop()
                )
                .thumbnail(0.5f)
                .transition(DrawableTransitionOptions()
                        .crossFade()
                )
                .into(p0.ivArt)

//
//        val seconds: Int
//        val min: Int
//
//        seconds = (song.duration / 1000).toInt()
//        min = seconds / 60
        var secondsString :String
        val minutes = (song.duration.toInt() % (1000 * 60 * 60))  / (1000 * 60)
        val seconds = (song.duration.toInt() % (1000 * 60 * 60) % (1000 * 60) / 1000)
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        p0.tvTime.text =minutes.toString() + ":" + secondsString
        p0.rl.setOnClickListener(View.OnClickListener {
            listener.onClick(p1,song.art,song.title,song.artist,song.path,song.duration,song.favorite)

        })
    }

    class SongViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvName = v.tv_name_song
        val tvArtist = v.tv_artists
//        val tvAlbum: TextView = v.tv_albums
        val ivArt: ImageView = v.iv_art
        val tvTime: TextView = v.tv_time
        val rl = v.rl_adapter_song

//

    }



    interface ItemSongListener {
        fun onClick(position:Int,art:String,title:String,artist:String,path:String,duration:Long,favorite : Int)
    }
}