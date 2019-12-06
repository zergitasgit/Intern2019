package com.weather.forecastweather.viewmodels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.weather.forecastweather.R
import com.weather.forecastweather.adapter.ListCityAdapter
import com.weather.forecastweather.data.DataCity
import com.weather.forecastweather.databinding.FragmentManagerCityBinding
import com.weather.forecastweather.models.City
import com.weather.forecastweather.views.ManagerCityFragment
import com.weather.forecastweather.views.SearchFragment
import java.util.*
import android.support.v7.app.AppCompatActivity
import android.widget.Toast


class ManagerViewModel(private var context: Activity, private var binding: FragmentManagerCityBinding
) : Observable() {
    private var adapter: ListCityAdapter? = null
    private var arr: ArrayList<City> = ArrayList()
    private var arrDelete: ArrayList<String> = ArrayList()

    companion object {
        var check: Boolean = false
    }

    init {
        arr = DataCity.getCityViewPager(context)
        setUpAdapter(arr)


        binding.ivEdit.setOnClickListener {
            if (check) {
//
                for (i in arrDelete) {
                    DataCity.deleteId(context, i)
                }
                adapter!!.setUpDelete(false)
                binding.ivEdit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_edit))
                check = false
                val intent = Intent("DELETE")
                context.sendBroadcast(intent)
            } else {

                adapter!!.setUpDelete(true)
                binding.ivEdit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_done))
                check = true
            }
        }

        binding.ivBack.setOnClickListener {
            onBack()
        }
        binding.ivSearchCity.setOnClickListener {
            //

            val fragment = SearchFragment()
            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
            check = true
        }
    }

    fun onBack() {
        if (check) {
            arr = DataCity.getCityViewPager(context)
//            for (i in arr.indices){
//                if(arr[i].code)
//            }
            setUpAdapter(arr)
            binding.ivEdit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_edit))
            check = false

        } else {
//            context.finish()

            (context as AppCompatActivity).finish()
        }
    }

    private fun setUpAdapter(arr: ArrayList<City>) {
        adapter = ListCityAdapter(context, arr, object : ListCityAdapter.ItemListener {
            override fun onClick(pos: Int, city: String, country: String, lat: Double, lon: Double) {
                if (!arr[pos].ischeck) {
//                    context.onBackPressed()
                    (context as AppCompatActivity).onBackPressed()
                    val intent = Intent("SEARCH_LIST")
                    intent.putExtra("lat", lat)
                    intent.putExtra("lon", lon)
                    intent.putExtra("pos", pos)
                    context.sendBroadcast(intent)
                }
//
            }

        }, object : ListCityAdapter.DeleteItemListener {
            override fun onClick(pos: Int, city: String, country: String, lat: Double, lon: Double) {
                adapter!!.delete(pos)
                arrDelete.add(city)
            }

        })
        binding.rv.layoutManager = LinearLayoutManager(context)
        binding.rv.setAdapter(adapter)
    }
}