package hieusenpaj.com.pdf.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hieusenpaj.com.pdf.R
import hieusenpaj.com.pdf.`object`.PDF
import kotlinx.android.synthetic.main.adapter_pdf.view.*

class PDFAdapter(private val context: Context,
                 private var arr : ArrayList<PDF>,
                 private val listener: ItemListener,
                 private val listenerMenu: MenuItemListener
                 ) : RecyclerView.Adapter<PDFAdapter.ViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PDFAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_pdf,p0,false))

    }


    override fun getItemCount(): Int =arr.size


    override fun onBindViewHolder(p0: PDFAdapter.ViewHolder, p1: Int) {
        var pdf = arr[p1]
        var path = pdf.path
        p0.iv.setOnClickListener(View.OnClickListener {
            listenerMenu.onClick(p1,it,pdf.name,path,pdf.date,pdf.size)
        })
        p0.rl.setOnClickListener(View.OnClickListener {
            listener.onClick(path,arr[p1].favorite,pdf.name,pdf.date,pdf.size)
        })
        p0.tvName.text = pdf.name
        p0.tvDate.text = pdf.date
        p0.tvSize.text = pdf.size
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
        fun onClick(position:Int,it:View,name:String,path: String,date:String,size: String)
    }
}