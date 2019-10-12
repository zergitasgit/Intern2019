package com.os13musicapp.os13musicplayer.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.os13musicapp.os13musicplayer.R
import com.os13musicapp.os13musicplayer.`object`.SongAdd
import kotlinx.android.synthetic.main.adapter_song_add.view.*

class AdapterAddSong(private val context: Context
                     , private var arrSong: ArrayList<SongAdd>,
                     private val listener: ItemSongListener)
    : RecyclerView.Adapter<AdapterAddSong.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterAddSong.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_song_add, p0, false))

    }

    override fun getItemCount(): Int =arrSong.size


    override fun onBindViewHolder(p0: AdapterAddSong.ViewHolder, p1: Int) {
        p0.tvName.text = arrSong.get(p1).song.title
        if (arrSong.get(p1).ischeck == true){
            p0.iv.visibility = View.VISIBLE
        }else{
            p0.iv.visibility = View.GONE
        }
        p0.rl.setOnClickListener(View.OnClickListener {
            listener.onClick(p1)
        })
    }
    class ViewHolder(v:View) : RecyclerView.ViewHolder(v){
        val tvName = v.tv_song_add
        val iv = v.iv_song_add
        val rl = v.rl_adapter_song_add
    }
    interface ItemSongListener {
        fun onClick(pos: Int)
    }
    fun setAllFalse(){
        for(songAdd in arrSong){
            songAdd.ischeck = false
        }
        notifyDataSetChanged()
    }
    fun removeItem(position: Int) {
        arrSong.get(position).ischeck=false

        notifyItemRangeChanged(position, arrSong.size);
    }

}