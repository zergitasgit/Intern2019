package hieusenpaj.com.weather.adapter

import android.app.Activity
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.databinding.ItemCityBinding
import hieusenpaj.com.weather.models.City

class SearchCityAdapter(private val context: Activity,
                        private var arr : ArrayList<City>,
                        private val listener: ItemListener):RecyclerView.Adapter<SearchCityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SearchCityAdapter.ViewHolder {
        val view:ItemCityBinding = DataBindingUtil
                .inflate(LayoutInflater.from(context),
                        R.layout.item_city, p0, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int =arr.size

    override fun onBindViewHolder(p0: SearchCityAdapter.ViewHolder, p1: Int) {
        p0.binding.setItem(arr[p1])
        p0.binding.rl.setOnClickListener {
            listener.onClick(p1,arr[p1].city,arr[p1].country,arr[p1].lat,arr[p1].lon,arr[p1].temp,arr[p1].status)
            Toast.makeText(context,arr[p1].country,Toast.LENGTH_SHORT).show()
        }

    }
    class ViewHolder(binding: ItemCityBinding):RecyclerView.ViewHolder(binding.root){
        val binding = binding

    }
    interface ItemListener {
        fun onClick(pos:Int,city:String,country:String,lat:Double,lon:Double,temp:String,status:String)
    }
}