package com.os13musicapp.os13musicplayer.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.os13musicapp.os13musicplayer.R
import com.os13musicapp.os13musicplayer.`object`.Artists
import kotlinx.android.synthetic.main.adapter_artist.view.*

class ArtistAdapter(
        private val context: Context
        , private var arrArtist: ArrayList<Artists>,
        private val listener: ItemSongListener
) : RecyclerView.Adapter<ArtistAdapter.ArtistsViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ArtistsViewHolder {
        return ArtistsViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_artist, p0, false))
    }

    override fun getItemCount(): Int = arrArtist.size


    override fun onBindViewHolder(p0: ArtistsViewHolder, p1: Int) {
        val artists: Artists = arrArtist.get(p1)

        p0.tvName.text = artists.title
        p0.tvNoOfSong.text = artists.track.toString() +" song"
        Glide
                .with(context)
                .load(artists.art)
                .apply(RequestOptions()
                        .placeholder(R.drawable.ic_singer)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .centerCrop()
                )
                .thumbnail(0.5f)
                .transition(DrawableTransitionOptions()
                        .crossFade()
                )
                .into(p0.ivArt)
        p0.itemArtist.setOnClickListener(View.OnClickListener {
            listener.onClick(p1)
        })

    }

    class ArtistsViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.tv_artist_name
        val tvNoOfSong: TextView = v.tv_no_of_songs
        val ivArt = v.iv_artist_albumArt
        val itemArtist = v.item_artist
    }

    interface ItemSongListener {
        fun onClick(pos: Int)
    }


}