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
import kotlinx.android.synthetic.main.adapter_playlist.view.*

class AdapterPlaylist(   private val context: Context
                         , private var arr: ArrayList<Playlist>,
                         private val listener: ItemSongListener)
    : RecyclerView.Adapter<AdapterPlaylist.ViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterPlaylist.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_playlist, p0, false))

    }

    override fun getItemCount(): Int = arr.size


    override fun onBindViewHolder(p0: AdapterPlaylist.ViewHolder, p1: Int) {
        p0.tv.text = arr.get(p1).title
        p0.rl.setOnClickListener(View.OnClickListener {
            listener.onClick(p1)
        })
        Glide
                .with(context)
                .load(arr.get(p1).art)
                .apply(RequestOptions()
                        .placeholder(R.drawable.ic_playlist)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .centerCrop()
                )
                .thumbnail(0.5f)
                .transition(DrawableTransitionOptions()
                        .crossFade()
                )
                .into(p0.iv)
    }
    class ViewHolder(v :View) : RecyclerView.ViewHolder(v){
        val tv = v.tv_title
        val rl = v.rl_adapter_playlist
        val iv = v.iv_playlist


    }
    interface ItemSongListener {
        fun onClick(pos: Int)
    }
}