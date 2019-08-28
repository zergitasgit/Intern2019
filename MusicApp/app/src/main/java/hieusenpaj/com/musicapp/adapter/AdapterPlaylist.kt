package hieusenpaj.com.musicapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    }
    class ViewHolder(v :View) : RecyclerView.ViewHolder(v){
        val tv = v.tv_title
        val rl = v.rl_adapter_playlist


    }
    interface ItemSongListener {
        fun onClick(pos: Int)
    }
}