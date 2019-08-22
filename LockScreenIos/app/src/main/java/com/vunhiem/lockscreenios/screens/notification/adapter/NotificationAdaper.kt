package com.vunhiem.lockscreenios.screens.notification.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vunhiem.lockscreenios.R
import com.vunhiem.lockscreenios.model.Notification
import kotlinx.android.synthetic.main.item_notifi.view.*


class NotificationAdaper(
    var context: Context,
    var noti: ArrayList<Notification>,
    private val listener: ItemNotiListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_notifi, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return noti.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var data: Notification = noti[position]
        holder.itemView.tv_title_app.text = data.appName
        holder.itemView.tv_text_title_noti.text = data.title
        holder.itemView.tv_content_noti.text = data.content
        holder.itemView.setOnClickListener {
            listener.onClick(position)
            noti.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)

            val pm = context.packageManager
            val launchIntent = pm.getLaunchIntentForPackage(data.appName!!)
            context.startActivity(launchIntent)

        }


//        holder.itemView.img_clear_noti.setOnClickListener {
//            noti.removeAt(holder.adapterPosition)
//            notifyItemRemoved(holder.adapterPosition)
//        }
    }

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    interface ItemNotiListener {
        fun onClick(pos: Int)
    }

    fun removeAt(position: Int) {
        noti.removeAt(position)
        notifyItemRemoved(position)
    }
}