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
import hieusenpaj.com.musicapp.`object`.Album
import hieusenpaj.com.musicapp.`object`.Artists
import kotlinx.android.synthetic.main.adapter_album.view.*

class AlbumAdapter (
        private val context: Context
        , private var arrAlbum: ArrayList<Album>,
        private val listener: ItemSongListener
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> (){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AlbumAdapter.AlbumViewHolder {
        return AlbumViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_album, p0, false))
    }

    override fun getItemCount(): Int =arrAlbum.size


    override fun onBindViewHolder(p0: AlbumAdapter.AlbumViewHolder, p1: Int) {

        var album = arrAlbum.get(p1)
        p0.tvName.text = album.title
        p0.tvArtists.text = album.artist
        Glide
                .with(context)
                .load(album.art)
                .apply(RequestOptions()
                        .placeholder(R.drawable.ic_albums)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .centerCrop()
                )

                .into(p0.ivArt)
        p0.item.setOnClickListener(View.OnClickListener {
            listener.onClick(p1)
        })
    }
    class AlbumViewHolder(v:View) : RecyclerView.ViewHolder(v){
        val tvName = v.tv_albums_name
        val tvArtists = v.tv_album_artist
        val ivArt = v.iv_album_art
        val item = v.item_album

    }
    interface ItemSongListener {
        fun onClick(pos: Int)
    }
}