package com.lock.applock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.lock.applock.R
import com.lock.applock.`object`.App
import kotlinx.android.synthetic.main.adapter_app.view.*

class AppAdapter(private val context: Context,
                 private var arr : ArrayList<App>,
                 private val listener: ItemListener
) : RecyclerView.Adapter<AppAdapter.ViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_app,p0,false))

    }


    override fun getItemCount(): Int =arr.size


    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindData()

    }
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && payloads[0] is Int) {
            holder.bindData(payloads[0] as Int)
        } else
            super.onBindViewHolder(holder, position, payloads)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        fun bindData(){
            itemView.tv_name.text = arr[adapterPosition].name
            bindData( arr[adapterPosition].isLock)
            itemView.iv_pop_menu.setOnClickListener {
                if(arr[adapterPosition].packagename!= context.packageName) {
                    listener.onClick(adapterPosition, arr[adapterPosition].packagename)
                    notifyItemChanged(adapterPosition, arr[adapterPosition].isLock)
                }
            }
            Glide
                .with(context)
                .load(context.packageManager.getApplicationIcon(arr[adapterPosition].packagename))
                .thumbnail(0.5f)
                .transition(
                    DrawableTransitionOptions()
                        .crossFade()
                )
                .into(itemView.iv)
        }
        fun bindData(locked:Int){
            if(locked==1) {
                Glide
                    .with(context)
                    .load(R.drawable.lock_click)
                    .thumbnail(0.5f)
                    .transition(
                        DrawableTransitionOptions()
                            .crossFade()
                    )
                    .into(itemView.iv_pop_menu)
            }else{
                Glide
                    .with(context)
                    .load(R.drawable.lock)
                    .thumbnail(0.5f)
                    .transition(
                        DrawableTransitionOptions()
                            .crossFade()
                    )
                    .into(itemView.iv_pop_menu)
            }
        }


    }
    interface ItemListener {
        fun onClick(position:Int,packageName:String)
    }

}