package com.reader.pdfreader.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reader.pdfreader.R
import com.reader.pdfreader.`object`.Office
import kotlinx.android.synthetic.main.item_file.view.*
import java.io.File

class FilesAdapter (private var context: Context,
                    private var arr:List<Office>,
                    private var listener:Listener
) : RecyclerView.Adapter<FilesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_file,parent,false))

    }

    override fun getItemCount(): Int = arr.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val office = arr[position]
        holder.tvName.text = office.title
        if (office.isFolder){
            holder.iv_file.setImageResource(R.drawable.ic_folder_2)
        }else{
            holder.iv_file.setImageResource(R.drawable.ic_pdf)
        }
        holder.tvDate.text = office.date
        holder.tvSize.text = office.size
        holder.rl.setOnClickListener {
            listener.onClick(office.title,office.size,office.path,office.isFolder)
        }

    }
    class ViewHolder(v: View):RecyclerView.ViewHolder(v){
        val tvSize = v.tv_size
        val tvName = v.tv_name
        val tvDate = v.tv_date
        val iv_file = v.iv
        val rl = v.rl_item

    }
    interface Listener{
        fun onClick(title:String,size:String,path:String,isFolder:Boolean)
    }




}