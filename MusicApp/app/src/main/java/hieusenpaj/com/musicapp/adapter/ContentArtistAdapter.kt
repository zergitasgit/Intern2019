package hieusenpaj.com.musicapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hieusenpaj.com.musicapp.R
import hieusenpaj.com.musicapp.`object`.Song
import kotlinx.android.synthetic.main.adapter_content_artist.view.*

class ContentArtistAdapter(
        private val context: Context
        , private var arrSong: ArrayList<Song>,
        private val listener: ItemSongListener

) : RecyclerView.Adapter<ContentArtistAdapter.ViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ContentArtistAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_content_artist, p0, false))
    }

    override fun getItemCount(): Int =arrSong.size


    override fun onBindViewHolder(p0: ContentArtistAdapter.ViewHolder, p1: Int) {
        var song = arrSong.get(p1)
        p0.tvName.text = song.title
        p0.tvNumber.text = (p1+1).toString()
        p0.rl.setOnClickListener(View.OnClickListener {
            listener.onClick(p1,song.art,song.title,song.artist,song.path,song.duration,song.favorite)

        })
    }
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        val tvNumber = v.tv_number_song_of_artist
        val tvName = v.tv_name_song_of_artist
        val rl =  v.rl_content_artist

    }
    interface ItemSongListener {
        fun onClick(position:Int,art:String,title:String,artist:String,path:String,duration:Long,favorite : Int)
    }
}