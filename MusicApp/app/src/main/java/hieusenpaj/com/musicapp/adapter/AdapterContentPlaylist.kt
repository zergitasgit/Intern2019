package hieusenpaj.com.musicapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import hieusenpaj.com.musicapp.R
import hieusenpaj.com.musicapp.`object`.Playlist
import hieusenpaj.com.musicapp.`object`.Song
import hieusenpaj.com.musicapp.`object`.SongAdd
import kotlinx.android.synthetic.main.adapter_content_playlist.view.*

class AdapterContentPlaylist(private val context: Context
                             , private var arr: ArrayList<SongAdd>,
                             private val listener: ItemSongListener
                            )
    : RecyclerView.Adapter<AdapterContentPlaylist.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterContentPlaylist.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_content_playlist, p0, false))

    }

    override fun getItemCount(): Int  = arr.size


    override fun onBindViewHolder(p0: AdapterContentPlaylist.ViewHolder, p1: Int) {
        var song = arr.get(p1).song
        p0.tvSong.text = song.title
        p0.tvArtist.text = song.artist
        Glide
                .with(context)
                .load(song.art)
                .apply(RequestOptions()
                        .placeholder(R.drawable.album_art)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .centerCrop()
                )
                .thumbnail(0.5f)
                .transition(DrawableTransitionOptions()
                        .crossFade()
                )
                .into(p0.ivArt)
        p0.rl.setOnClickListener(View.OnClickListener {
            listener.onClick(p1,song.art,song.title,song.artist,song.path,song.duration,song.favorite)

        })
    }
    class ViewHolder(v : View) : RecyclerView.ViewHolder(v){
        val tvSong = v.tv_name_song
        val tvArtist = v.tv_artist
        val ivArt =v.iv_art
        val rl = v.rl

    }

    interface ItemSongListener {
        fun onClick(position: Int, art: String, title: String, artist: String, path: String, duration: Long,favorite : Int)
    }
    fun removeItem(position: Int) {
        arr.get(position).ischeck=false
        arr.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, arr.size);
    }
}