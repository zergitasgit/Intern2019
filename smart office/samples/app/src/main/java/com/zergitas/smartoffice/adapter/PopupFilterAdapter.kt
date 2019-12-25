package com.zergitas.smartoffice.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.zergitas.smartoffice.ItemMain
import com.zergitas.smartoffice.R
import kotlinx.android.synthetic.main.item_menu.view.*

class PopupFilterAdapter(private val context: Context,
                         private var arr: ArrayList<ItemMain>,
                         private val listener: ItemListener) : BaseAdapter()  {
    var sharedPreferences: SharedPreferences ?=null
    var edit : SharedPreferences.Editor?= null
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        sharedPreferences = context.getSharedPreferences("hieu",Context.MODE_PRIVATE)
        edit = sharedPreferences!!.edit()
        val view = LayoutInflater.from(context).inflate(R.layout.item_menu, null)
        view.ll.setOnClickListener {
            listener.onClick(p0)
        }
//        view.iv.setImageResource(arr[p0].image)

        view.tv.text = arr[p0].string
        view.iv.setImageResource(arr[p0].image)
        return view
    }

    override fun getItem(p0: Int): Any {
        return arr[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int =arr.size

    interface ItemListener {
        fun onClick(position : Int)
    }
}