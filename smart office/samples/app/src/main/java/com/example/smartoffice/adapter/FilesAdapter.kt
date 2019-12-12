package com.example.smartoffice.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartoffice.R
import kotlinx.android.synthetic.main.item_file.view.*
import java.io.File

class FilesAdapter (private var context: Context,
                    private var arr:List<File>,
                    private var listener:Listener
) : RecyclerView.Adapter<FilesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_file,parent,false))

    }

    override fun getItemCount(): Int = arr.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = arr[position]
        holder.tvName.text = file.name
        holder.iv_file.setImageResource(if (file.isDirectory) R.drawable.ic_folder_primary_24dp else R.drawable.ic_file_primary_24dp)
        if (file.isDirectory) {
            holder.tvSize.visibility = View.GONE
        } else {
            holder.tvSize.visibility = View.VISIBLE
            holder.tvSize.text = getSize(file)
        }
        holder.ll.setOnClickListener {
            listener.onClick(file)
        }

    }
    class ViewHolder(v: View):RecyclerView.ViewHolder(v){
        val tvSize = v.tv_size
        val tvName = v.tv_name
        val iv_file = v.iv_file
        val ll = v.ll_file

    }
    interface Listener{
        fun onClick(file:File)
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