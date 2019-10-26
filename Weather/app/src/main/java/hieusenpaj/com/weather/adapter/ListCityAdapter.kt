package hieusenpaj.com.weather.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.api.ApiUtils
import hieusenpaj.com.weather.databinding.ItemCityBinding
import hieusenpaj.com.weather.databinding.ItemListCityBinding
import hieusenpaj.com.weather.models.City

class ListCityAdapter(private val context: Context,
                      private var arr: ArrayList<City>,
                      private val listener: ItemListener,
                      private val deleteListener: DeleteItemListener) : RecyclerView.Adapter<ListCityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ListCityAdapter.ViewHolder {
        val view: ItemListCityBinding = DataBindingUtil
                .inflate(LayoutInflater.from(context),
                        R.layout.item_list_city, p0, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int = arr.size

    override fun onBindViewHolder(p0: ListCityAdapter.ViewHolder, p1: Int) {
        p0.binding.setItem(arr[p1])

        Glide.with(context)
                .load(ApiUtils.ICON + arr[p1].status + ".png")
                .into(p0.binding.ivStatus)

        if (arr[p1].ischeck) {
            if(p1>0) {
                p0.binding.ivDelete.visibility = View.VISIBLE
                p0.binding.tv.visibility = View.GONE
                p0.binding.tvTemp.visibility = View.GONE
                p0.binding.ivStatus.visibility = View.GONE
                p0.binding.ivDelete.setOnClickListener {
                    deleteListener.onClick(p1, arr[p1].city, arr[p1].country, arr[p1].lat, arr[p1].lon)
                }
            }

        } else {
            p0.binding.ivDelete.visibility = View.GONE
            p0.binding.tv.visibility = View.VISIBLE
            p0.binding.tvTemp.visibility = View.VISIBLE
            p0.binding.ivStatus.visibility = View.VISIBLE

            p0.binding.rl.setOnClickListener {
                listener.onClick(p1, arr[p1].city, arr[p1].country, arr[p1].lat, arr[p1].lon)
            }
        }

    }

    class ViewHolder(binding: ItemListCityBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding

    }

    fun setUpDelete(isCheck:Boolean) {
        for (i in arr.indices) {

                arr[i].ischeck = isCheck

        }
        notifyDataSetChanged()
    }

    fun delete(pos: Int) {
        arr.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos, arr.size);
    }

    interface ItemListener {
        fun onClick(pos: Int, city: String, country: String, lat: Double, lon: Double)
    }

    interface DeleteItemListener {
        fun onClick(pos: Int, city: String, country: String, lat: Double, lon: Double)
    }

}