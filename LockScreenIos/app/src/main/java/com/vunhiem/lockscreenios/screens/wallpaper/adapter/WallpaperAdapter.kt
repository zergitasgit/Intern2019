package com.vunhiem.lockscreenios.screens.wallpaper.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vunhiem.lockscreenios.R
import com.vunhiem.lockscreenios.model.Image
import com.vunhiem.lockscreenios.screens.wallpaper.SetWallpaper
import kotlinx.android.synthetic.main.row_wallpaper.view.*

class WallpaperAdapter(val data:ArrayList<Image>,val context: Context):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WallpaperViewHolder(LayoutInflater.from(context).inflate(R.layout.row_wallpaper,parent,false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var image:Image = data[position]
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            Picasso.with(context).load(image.id).fit().centerCrop().into(holder.itemView.imgWallpaper)
//        } else{
         Glide.with(context).load(image.id).into(holder.itemView.imgWallpaper)
//        }

        holder.itemView.imgWallpaper.setOnClickListener {
            val intent = Intent(context, SetWallpaper::class.java)
            intent.putExtra("key", image.id)
            context.startActivity(intent)
        }
    }
class WallpaperViewHolder(view:View) :RecyclerView.ViewHolder(view){

}
}