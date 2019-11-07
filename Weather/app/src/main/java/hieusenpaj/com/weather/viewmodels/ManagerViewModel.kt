package hieusenpaj.com.weather.viewmodels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.adapter.ListCityAdapter
import hieusenpaj.com.weather.data.DataCity
import hieusenpaj.com.weather.databinding.FragmentManagerCityBinding
import hieusenpaj.com.weather.models.City
import hieusenpaj.com.weather.views.ManagerCityFragment
import hieusenpaj.com.weather.views.SearchFragment
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
//                arrDelete.clear()
//                Toast.makeText(context,arrDelete.size.toString(),Toast.LENGTH_SHORT).show()
                for (i in arrDelete){
                    DataCity.deleteId(context,i)
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
        binding.ivSearchCity.setOnClickListener{
//            val intent = Intent(context, SearchFragment::class.java)
//            context.startContext(intent)
//            context.finish()

            val fragment = SearchFragment()
            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
            check=true
        }
    }
    fun onBack(){
        if(check){
            arr = DataCity.getCityViewPager(context)
            setUpAdapter(arr)
            binding.ivEdit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_edit))
            check = false

        }else{
//            context.finish()

            (context as AppCompatActivity).finish()
        }
    }
    private fun setUpAdapter(arr: ArrayList<City>){
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