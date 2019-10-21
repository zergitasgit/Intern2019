package hieusenpaj.com.weather.db

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import hieusenpaj.com.weather.models.City

class DBHistory (private val context: Context,
                 factory: SQLiteDatabase.CursorFactory?)
    : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    override fun onCreate(p0: SQLiteDatabase?) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sharedPreferences.edit()
        if (sharedPreferences.getBoolean("history", false) == false) {
            val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " +
                    TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_CITY
                    + " TEXT," + COLUMN_COUNTRY + " TEXT," + COLUMN_LAT + " TEXT," +
                    COLUMN_LON + " TEXT," + COLUMN_HISTORY + " REAL" + ")")

            p0!!.execSQL(CREATE_PRODUCTS_TABLE)
            edit.putBoolean("history", true)
            edit.apply()
        } else {

        }

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "history.db"
        val TABLE_NAME = "his"
        val COLUMN_ID = "_id"
        val COLUMN_CITY = "city"
        val COLUMN_COUNTRY = "country"
        val COLUMN_LAT = "lat"
        val COLUMN_LON = "lon"
        val COLUMN_HISTORY = "history"

    }
    fun insertHistory(city: String, country: String, lat: String, lon: String,history:Long) {
        val values = ContentValues()
        values.put(COLUMN_CITY, city)
        values.put(COLUMN_COUNTRY, country)
        values.put(COLUMN_LAT, lat)
        values.put(COLUMN_LON, lon)
        values.put(COLUMN_HISTORY, history)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
    fun getCity(): ArrayList<City> {
        var arr: ArrayList<City> = ArrayList()
        val db = this.getWritableDatabase()
        val cursor = db.rawQuery("SELECT * FROM his ORDER BY history DESC ", null)
        cursor.moveToFirst()
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {

                var city = cursor.getString(cursor.getColumnIndex("city"))
                var country = cursor.getString(cursor.getColumnIndex("country"))
                var lat = cursor.getString(cursor.getColumnIndex("lat"))

                var lon = cursor.getString(cursor.getColumnIndex("lon"))


                arr.add(City(city, country,lat.toDouble(),lon.toDouble()))
                cursor.moveToNext()
            }
        }
        return arr
    }

}