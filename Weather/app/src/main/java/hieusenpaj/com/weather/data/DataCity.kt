package hieusenpaj.com.weather.data

import android.app.Activity
import com.snappydb.DB
import hieusenpaj.com.weather.db.DBCity
import hieusenpaj.com.weather.models.Add
import hieusenpaj.com.weather.models.City

class DataCity {
    companion object{
        fun getListCity(activity:Activity,name: String):ArrayList<City>{
            val dbCity = DBCity(activity)
            val arr = dbCity.getListCity(name)
            return arr

        }
        fun getLatLon(activity: Activity,name:String) : Add {
            val dbCity = DBCity(activity)
            val add = dbCity.getCityByName(name)
            return add
        }

    }
}