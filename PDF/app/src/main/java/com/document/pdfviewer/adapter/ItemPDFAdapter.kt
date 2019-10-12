package com.document.pdfviewer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.document.pdfviewer.R
import com.document.pdfviewer.`object`.ItemPDF
import kotlinx.android.synthetic.main.item_menu_pdf.view.*

class ItemPDFAdapter(private val context: Context,
                     private var arr: ArrayList<ItemPDF>,
                     private val listener: ItemListener) : BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_menu_pdf, null)
        view.ll.setOnClickListener {
            listener.onClick(p0,it)
        }
        view.iv.setImageResource(arr[p0].image)
        view.tv.text = arr[p0].string
        view.iv2.setImageResource(arr[p0].image2)
        return view
    }

    override fun getItem(p0: Int): Any {
        return arr[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int = arr.size

    interface ItemListener {
        fun onClick(position: Int,it:View)
    }
    fun updateNightMode(view :View,isNightMode: Boolean){
        if(!isNightMode) {
            view.iv2.setImageResource(R.drawable.ic_ic_on)
        }else{
            view.iv2.setImageResource(R.drawable.ic_ic_off)
        }
    }
    fun updateHorizontal(view :View,ishorizontal: Boolean){
        if(!ishorizontal){
            view.tv.text = context.resources.getString(R.string.h_mode)
        }else{
            view.tv.text = context.resources.getString(R.string.v_mode )
        }
    }
}