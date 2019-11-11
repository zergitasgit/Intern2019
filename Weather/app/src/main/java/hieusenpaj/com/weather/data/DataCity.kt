package hieusenpaj.com.weather.data

import android.content.Context
import android.util.Log
import hieusenpaj.com.weather.db.DBBackground
import hieusenpaj.com.weather.db.DBCity
import hieusenpaj.com.weather.db.DBHistory
import hieusenpaj.com.weather.models.Add
import hieusenpaj.com.weather.models.BackGround
import hieusenpaj.com.weather.models.City
import hieusenpaj.com.weather.models.Language

class DataCity {
    companion object{
        fun getListCity(context:Context,name: String):ArrayList<City>{
            val dbCity = DBCity(context)
            val arr = dbCity.getListCity(name)
            return arr

        }
        fun getCityViewPager(context: Context):ArrayList<City>{
            val dbHistory =DBHistory(context,null)
            val arr = dbHistory.getCityHistory(false)
            return arr
        }
        fun getCityHistory(context: Context):ArrayList<City>{
            val dbHistory =DBHistory(context,null)
            val arr = dbHistory.getCityHistory(true)
            return arr
        }
        fun checkCitySearch(context: Context,string: String):Boolean{
            val dbHistory= DBHistory(context,null)
            var boolean = false
            val arr = dbHistory.getCityByName(string)
            Log.e("hieu",arr.size.toString())
            if (arr.size>0){
               boolean = true
            }
            return boolean
        }
        fun getIdCity(context: Context,name: String):Int{
            val dbHistory= DBHistory(context,null)
            val id = dbHistory.getCityByNameSearch(name)
            return id
        }

        fun insertHistory(context: Context,city: String, country: String, lat: String, lon: String,temp:String,status:String,
                          history:Long,code :String,timeZone:String){
            val dbHistory =DBHistory(context,null)
            dbHistory.insertHistory(city,country,lat,lon,temp,status,history,code,timeZone)

        }
        fun updateHistory(context: Context,city: String,tim:Long){
            val dbHistory =DBHistory(context,null)
            dbHistory.updateHistory(city,tim)
        }
        fun updateLocal(context: Context,city: String, country: String, lat: String, lon: String,temp:String,status:String,
                        code: String,timeZone: String,pos:Int){
            val dbHistory =DBHistory(context,null)
            dbHistory.updateLocal(city,country,lat,lon,temp,status,code,timeZone,pos+1)
        }
        fun deleteId(context: Context,city: String){
            val dbHistory =DBHistory(context,null)
            dbHistory.deleteId(city)
        }
        fun getLanguage(context: Context,code: String):Language{
            val dbHistory =DBBackground(context)
            return dbHistory.getLanguage(code)
        }
        fun getBg(context: Context,code:Int):ArrayList<BackGround>{
            val dbBg = DBBackground(context)
            return dbBg.getBG(code)
        }
//        fun getIcon(context: Context,code:Int):ArrayList<BackGround>{
//            val dbBg = DBBackground(context)
//            return dbBg.getIcon(code)
//        }




    }
}