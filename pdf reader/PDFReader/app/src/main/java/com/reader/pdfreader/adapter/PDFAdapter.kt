package com.reader.pdfreader.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.pdftron.pdf.utils.Utils.getResources
import com.reader.pdfreader.R
import com.reader.pdfreader.`object`.PDF
import kotlinx.android.synthetic.main.adapter_pdf.view.*

class PDFAdapter(private val context: Context,
                 private var arr : List<PDF>,
                 private val listener: ItemListener,
                 private val listenerMenu: MenuItemListener
                 ) : RecyclerView.Adapter<PDFAdapter.ViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_pdf,p0,false))

    }


    override fun getItemCount(): Int =arr.size


    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        var pdf = arr[p1]
        var path = pdf.path

        p0.iv.setOnClickListener {

            listenerMenu.onClick(p1,pdf.favorite,pdf.name,path,pdf.date,pdf.size)
            notifyDataSetChanged()
        }
        p0.rl.setOnClickListener {
            listener.onClick(path,arr[p1].favorite,pdf.name,pdf.date,pdf.size)
        }
        p0.tvName.text = pdf.name
        p0.tvDate.text = pdf.date
        p0.tvSize.text = pdf.size
        if(pdf.favorite==1){
            Glide
                .with(context)
                .load(R.drawable.ic_like_click)
                .thumbnail(0.5f)
                .transition(
                    DrawableTransitionOptions()
                        .crossFade()
                )
                .into(p0.iv)

        }else{
            Glide
                .with(context)
                .load(R.drawable.ic_like)
                .thumbnail(0.5f)
                .transition(
                    DrawableTransitionOptions()
                        .crossFade()
                )
                .into(p0.iv)
        }
    }
    class ViewHolder(v:View) : RecyclerView.ViewHolder(v){
        val tvName = v.tv_name
        val tvDate = v.tv_date
        val tvSize = v.tv_size
        val iv = v.iv_pop_menu
        val rl = v.rl


    }
    interface ItemListener {
        fun onClick(path:String,favorite : Int,name:String,date:String,size: String)
    }
    interface MenuItemListener {
        fun onClick(position:Int,favorite: Int,name:String,path: String,date:String,size: String)
    }
}