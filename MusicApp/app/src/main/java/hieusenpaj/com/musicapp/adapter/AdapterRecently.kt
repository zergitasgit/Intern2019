package hieusenpaj.com.musicapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import hieusenpaj.com.musicapp.R
import hieusenpaj.com.musicapp.`object`.Song
import kotlinx.android.synthetic.main.adapter_recently.view.*

class AdapterRecently(private val context: Context
                      , private var arrSong: ArrayList<Song>,
                      private val listener: ItemSongListener


) : RecyclerView.Adapter<AdapterRecently.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterRecently.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_recently, p0, false))
    }

    override fun getItemCount(): Int = arrSong.size


    override fun onBindViewHolder(p0: AdapterRecently.ViewHolder, p1: Int) {
        var song =arrSong.get(p1)
        Glide
                .with(context)
                .load(arrSong.get(p1).art)

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
                .into(p0.iv)

        p0.iv.setOnClickListener(View.OnClickListener {
            listener.onClick(p1,song.art,song.title,song.artist,song.path,song.duration,song.favorite)

        })
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val iv = v.iv_recently

//

    }


    interface ItemSongListener {
        fun onClick(position: Int, art: String, title: String, artist: String, path: String, duration: Long,favorite : Int)
    }
}