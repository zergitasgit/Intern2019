package com.zergitas.smartoffice.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zergitas.smartoffice.`object`.Office
import com.zergitas.smartoffice.R
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
            holder.iv_file.setImageResource(R.drawable.ic_folder)
            holder.tvSize.visibility = View.GONE
        }else{
            if (office.path.contains(".pdf")|| office.path.contains(".PDF")){
                holder.iv_file.setImageResource(R.drawable.ic_pdf)
            }else if(office.path.endsWith(".doc")|| office.path.endsWith(".DOC")|| office.path.endsWith(".docx")|| office.path.endsWith(".DOCX")){
                holder.iv_file.setImageResource(R.drawable.ic_word)
            }else if (office.path.endsWith(".txt")){
                holder.iv_file.setImageResource(R.drawable.ic_txt)
            }else if(office.path.endsWith(".xls") || office.path.endsWith(".xlsx") || office.path.endsWith(".XLSX")){
                holder.iv_file.setImageResource(R.drawable.ic_excel)
            }else if (office.path.endsWith(".ppt") || office.path.endsWith(".pptx") || office.path.endsWith(".PPTX")){
                holder.iv_file.setImageResource(R.drawable.ic_ppt)
            }

            holder.tvSize.visibility = View.VISIBLE
            holder.tvSize.text = office.size
        }


        holder.ll.setOnClickListener {
            listener.onClick(office.title,office.size,office.path,office.isFolder)
        }

    }
    class ViewHolder(v: View):RecyclerView.ViewHolder(v){
        val tvSize = v.tv_size
        val tvName = v.tv_name
        val iv_file = v.iv_file
        val ll = v.ll_file

    }
    interface Listener{
        fun onClick(title:String,size:String,path:String,isFolder:Boolean)
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