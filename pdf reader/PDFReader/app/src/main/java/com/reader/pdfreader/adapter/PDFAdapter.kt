package com.reader.pdfreader.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.document.pdfviewer.`object`.PDF
import com.reader.pdfreader.R
import kotlinx.android.synthetic.main.adapter_pdf.view.*

class PDFAdapter(private val context: Context,
                 private var arr : ArrayList<PDF>,
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
            p0.iv.setImageResource(R.drawable.ic_on)
        }else{
            p0.iv.setImageResource(R.drawable.ic_off)
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