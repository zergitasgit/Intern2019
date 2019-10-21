package hieusenpaj.com.weather.adapter

import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.databinding.ItemCityBinding
import hieusenpaj.com.weather.models.City

class CityAdapter(private val context: Activity,
                  private var arr : ArrayList<City>,
                  private val listener: ItemListener):RecyclerView.Adapter<CityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CityAdapter.ViewHolder {
        val view:ItemCityBinding = DataBindingUtil
                .inflate(LayoutInflater.from(context),
                        R.layout.item_city, p0, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int =arr.size

    override fun onBindViewHolder(p0: CityAdapter.ViewHolder, p1: Int) {
        p0.binding.setItem(arr[p1])
        p0.binding.rl.setOnClickListener {
            listener.onClick(arr[p1].city,arr[p1].country,arr[p1].lat,arr[p1].lon)
        }

    }
    class ViewHolder(binding: ItemCityBinding):RecyclerView.ViewHolder(binding.root){
        val binding = binding

    }
    interface ItemListener {
        fun onClick(city:String,country:String,lat:Double,lon:Double)
    }
}