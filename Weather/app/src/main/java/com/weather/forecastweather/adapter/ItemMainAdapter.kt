package com.weather.forecastweather.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.weather.forecastweather.R
import com.weather.forecastweather.models.ItemMain
import kotlinx.android.synthetic.main.item_menu.view.*

class ItemMainAdapter(private val context: Context,
                      private var arr: ArrayList<ItemMain>,
                      private val listener: ItemListener) : BaseAdapter()  {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_menu, null)
        view.ll.setOnClickListener {
            listener.onClick(p0)
        }
        view.iv.setImageResource(arr[p0].image)
        view.tv.text = arr[p0].string
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