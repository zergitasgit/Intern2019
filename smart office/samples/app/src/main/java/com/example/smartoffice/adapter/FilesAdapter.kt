package com.example.smartoffice.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartoffice.R
import com.example.smartoffice.`object`.Office
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
        holder.iv_file.setImageResource(if (office.isFolder) R.drawable.ic_folder_primary_24dp else R.drawable.ic_file_primary_24dp)
        if (office.isFolder) {
            holder.tvSize.visibility = View.GONE
        } else {
            holder.tvSize.visibility = View.VISIBLE
            holder.tvSize.text = office.size
        }
        holder.ll.setOnClickListener {
            listener.onClick(office.path,office.isFolder,office.title)
        }

    }
    class ViewHolder(v: View):RecyclerView.ViewHolder(v){
        val tvSize = v.tv_size
        val tvName = v.tv_name
        val iv_file = v.iv_file
        val ll = v.ll_file

    }
    interface Listener{
        fun onClick(path:String,isFolder:Boolean,title:String)
    }

    private fun getSize(file: File): String {
        var size = file.length() // Get size and convert bytes into Kb.
        var suffix = ""
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }
        return size.toString() + suffix
    }


}