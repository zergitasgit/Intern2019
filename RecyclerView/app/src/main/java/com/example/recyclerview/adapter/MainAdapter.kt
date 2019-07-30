package com.example.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview.R
import com.example.recyclerview.model.Nhac
import kotlinx.android.synthetic.main.item_main_layout.view.*

class MainAdapter(val data: ArrayList<Nhac>, val context: Context) :
    RecyclerView.Adapter<ViewHolder>() {

    companion object {
    }
    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item_main_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.tv_title.text = data[position].title
        holder.itemView.tv_singer.text = data[position].singer
        holder.itemView.cb_main.isChecked = data[position].isCheck
        holder.itemView.img_main.setImageResource(data[position].image)
        holder.itemView.cv_main.setOnClickListener {
            //Toast.makeText(context, data.get(position).title, Toast.LENGTH_SHORT).show()
        }

        holder.itemView.cb_main.setOnClickListener {
            data[holder.adapterPosition].isCheck = true

        }

        holder.itemView.img_more.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.inflate(R.menu.popup_item_layout)

            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
                when (item!!.itemId) {
                    R.id.item_xoa -> {
                        data.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)
                        Toast.makeText(context, "xoa", Toast.LENGTH_SHORT).show()
                    }
                    R.id.item_play_list -> {
                        Toast.makeText(context, "dua vao playlist", Toast.LENGTH_SHORT).show()
                    }
                }
                true

            })

            popupMenu.show()
        }


    }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
}