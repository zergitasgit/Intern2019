package hieusenpaj.com.weather.viewmodels

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.adapter.ListCityAdapter
import hieusenpaj.com.weather.data.DataCity
import hieusenpaj.com.weather.databinding.ActivityListCityBinding
import hieusenpaj.com.weather.models.City
import java.util.*

class ListCityViewModel(private var activity: Activity, private var binding: ActivityListCityBinding
) : Observable() {
    private var adapter: ListCityAdapter? = null
    private var arr: ArrayList<City> = ArrayList()
    private var arrDelete: ArrayList<String> = ArrayList()
    private var check: Boolean = false

    init {
        arr = DataCity.getCityViewPager(activity)
        setUpAdapter(arr)


        binding.ivEdit.setOnClickListener {
            if (check) {
                arrDelete.clear()
//                Toast.makeText(activity,arrDelete.size.toString(),Toast.LENGTH_SHORT).show()
                for (i in arrDelete){
                    DataCity.deleteId(activity,i)
                }
                adapter!!.setUpDelete(false)
                binding.ivEdit.setImageDrawable(activity.getResources().getDrawable(R.drawable.edit))
                check = false
                val intent = Intent("DELETE")
                activity.sendBroadcast(intent)
            } else {

                adapter!!.setUpDelete(true)
                binding.ivEdit.setImageDrawable(activity.getResources().getDrawable(R.drawable.tick))
                check = true
            }
        }

        binding.ivBack.setOnClickListener {
            onBack()
        }
    }
    fun onBack(){
        if(check){
            arr = DataCity.getCityViewPager(activity)
            setUpAdapter(arr)
            binding.ivEdit.setImageDrawable(activity.getResources().getDrawable(R.drawable.edit))
            check = false

        }else{
            activity.finish()
        }
    }
    private fun setUpAdapter(arr: ArrayList<City>){
        adapter = ListCityAdapter(activity, arr, object : ListCityAdapter.ItemListener {
            override fun onClick(pos: Int, city: String, country: String, lat: Double, lon: Double) {
                if (!arr[pos].ischeck) {
                    activity.onBackPressed()
                    val intent = Intent("SEARCH_LIST")
                    intent.putExtra("lat", lat)
                    intent.putExtra("lon", lon)
                    intent.putExtra("pos", pos)
                    activity.sendBroadcast(intent)
                }
//
            }

        }, object : ListCityAdapter.DeleteItemListener {
            override fun onClick(pos: Int, city: String, country: String, lat: Double, lon: Double) {
                adapter!!.delete(pos)
                arrDelete.add(city)
            }

        })
        binding.rv.layoutManager = LinearLayoutManager(activity)
        binding.rv.setAdapter(adapter)
    }
}