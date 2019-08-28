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
import hieusenpaj.com.musicapp.`object`.SongAdd
import kotlinx.android.synthetic.main.adapter_content_add_song.view.*

class AdapterContentAddSong(private val context: Context
                            , private var arr: ArrayList<SongAdd>)
    : RecyclerView.Adapter<AdapterContentAddSong.ViewHolder>() {
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        var songAdd = arr.get(p1)
        p0.tvSong.text = songAdd.song.title
        p0.tvArtist.text = songAdd.song.artist
        Glide
                .with(context)
                .load(songAdd.song.art)
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
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterContentAddSong.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_content_add_song, p0, false))
    }

    override fun getItemCount(): Int = arr.size




    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvSong = v.tv_name_song
        val tvArtist = v.tv_artist
        val ivArt =v.iv_art
    }

    fun removeItem(position: Int) {
        arr.get(position).ischeck=false
        arr.removeAt(position)

        notifyItemRemoved(position)
        notifyItemRangeChanged(position, arr.size);
    }

    fun restoreItem(item: SongAdd, position: Int) {
        arr.get(position).ischeck=true
        arr.add(position, item)

        notifyItemInserted(position)
    }
}