package hieusenpaj.com.weather.data

import android.app.Activity
import android.util.Log
import hieusenpaj.com.weather.db.DBCity
import hieusenpaj.com.weather.db.DBHistory
import hieusenpaj.com.weather.models.Add
import hieusenpaj.com.weather.models.City

class DataCity {
    companion object{
        fun getListCity(activity:Activity,name: String):ArrayList<City>{
            val dbCity = DBCity(activity)
            val arr = dbCity.getListCity(name)
            return arr

        }
        fun getCityViewPager(activity: Activity):ArrayList<City>{
            val dbHistory =DBHistory(activity,null)
            val arr = dbHistory.getCityHistory(false)
            return arr
        }
        fun getCityHistory(activity: Activity):ArrayList<City>{
            val dbHistory =DBHistory(activity,null)
            val arr = dbHistory.getCityHistory(true)
            return arr
        }
        fun checkCitySearch(activity: Activity,string: String):Boolean{
            val dbHistory= DBHistory(activity,null)
            var boolean = false
            val arr = dbHistory.getCityByName(string)
            Log.e("hieu",arr.size.toString())
            if (arr.size>0){
               boolean = true
            }
            return boolean
        }
        fun getIdCity(activity: Activity,name: String):Int{
            val dbHistory= DBHistory(activity,null)
            val id = dbHistory.getCityByNameSearch(name)
            return id
        }

        fun insertHistory(activity: Activity,city: String, country: String, lat: String, lon: String,temp:String,status:String,
                          history:Long){
            val dbHistory =DBHistory(activity,null)
            dbHistory.insertHistory(city,country,lat,lon,temp,status,history)

        }
        fun updateHistory(activity: Activity,city: String,tim:Long){
            val dbHistory =DBHistory(activity,null)
            dbHistory.updateHistory(city,tim)
        }
        fun updateLocal(activity: Activity,city: String, country: String, lat: String, lon: String,temp:String,status:String){
            val dbHistory =DBHistory(activity,null)
            dbHistory.updateLocal(city,country,lat,lon,temp,status)
        }
        fun deleteId(activity: Activity,city: String){
            val dbHistory =DBHistory(activity,null)
            dbHistory.deleteId(city)
        }




    }
}