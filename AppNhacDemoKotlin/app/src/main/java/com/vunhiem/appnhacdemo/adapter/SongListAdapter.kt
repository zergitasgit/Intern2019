package com.vunhiem.appnhacdemo.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.vunhiem.appnhacdemo.R
import com.vunhiem.appnhacdemo.interfacee.CostomItemClickListener
import com.vunhiem.appnhacdemo.model.Song
import com.vunhiem.appnhacdemo.service.PlayMusic
import kotlinx.android.synthetic.main.music_row.view.*
import java.util.concurrent.TimeUnit

class SongListAdapter(Song:ArrayList<Song>, context: Context): RecyclerView.Adapter<SongListAdapter.SongListViewHolder>() {
   var mContext = context
    var mSongModel = Song
    val allMusicList:ArrayList<String> = ArrayList()

    companion object{
        val MUSICLIST = "musiclist"
        val MUSICTEMPOS = "post"
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongListViewHolder {
       var view = LayoutInflater.from(parent!!.context).inflate(R.layout.music_row, parent, false)
        return SongListViewHolder(view)
    }

    override fun getItemCount(): Int {
       return mSongModel.size
    }
fun toMands(millis :Long):String{
    var duration =  String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis),
        TimeUnit.MILLISECONDS.toSeconds(millis)-TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(millis)
        ))
return duration
}
    override fun onBindViewHolder(holder: SongListViewHolder, position: Int) {
        var model = mSongModel[position]
        var songName = model.mSongName
        var songDuration = toMands(model.mSongDuration.toLong())
        holder.itemView.song_name.text = songName
        holder.itemView.song_duration.text = songDuration
        holder.setOnCostomItemClickListener(object : CostomItemClickListener{
            override fun onCostomItemClick(view: View, pos: Int) {
                for (i in 0 until mSongModel.size){
                  allMusicList.add(mSongModel[i].mSongPath)
                }
                Log.i("allmusiclist", allMusicList.toString())
                Toast.makeText(mContext, "Play:   $songName",Toast.LENGTH_LONG).show()
                var musicDataIntent = Intent(mContext,PlayMusic::class.java)
                musicDataIntent.putStringArrayListExtra(MUSICLIST,allMusicList)
                musicDataIntent.putExtra(MUSICTEMPOS,pos)
                mContext.startService(musicDataIntent)
            }

        })

    }


    class SongListViewHolder(itemview:View):RecyclerView.ViewHolder(itemview),View.OnClickListener
    {
        init {
            itemview.setOnClickListener(this)
        }

        var mCostomItemClickListener:CostomItemClickListener? = null
        fun setOnCostomItemClickListener(costomItemClickListener: CostomItemClickListener){
            this.mCostomItemClickListener = costomItemClickListener
        }
        override fun onClick(p0: View?) {
            this.mCostomItemClickListener!!.onCostomItemClick(p0!!, adapterPosition)
        }
    }

}