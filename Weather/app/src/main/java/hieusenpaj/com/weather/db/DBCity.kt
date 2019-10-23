package hieusenpaj.com.weather.db

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import hieusenpaj.com.weather.models.Add
import hieusenpaj.com.weather.models.City
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DBCity(private val context: Context) {


    companion object {
        private val DB_NAME = "city.db"
        private val dbName = "City"
//        private val DB_PATH = "/data/data/hieusenpaj.com.weather/databases/"
    }

    fun openDatabase(): SQLiteDatabase {
//        db = DBFactory.open(context, dbName)
        val dbFile = context.getDatabasePath(DB_NAME)

        if (!dbFile.exists()) {
            try {
                val checkDB = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null)
                checkDB?.close()
                copyDatabase(dbFile)
            } catch (e: IOException) {
                throw RuntimeException("error", e)
            }
        }
        return SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }
//    init {
//
//        val dbFile = context.getDatabasePath(DB_NAME)
//    }

    @SuppressLint("WrongConstant")
    private fun copyDatabase(dbFile: File) {
        val `is` = context.assets.open(DB_NAME)
        val os = FileOutputStream(dbFile)
        val buffer = ByteArray(1024)
        while (`is`.read(buffer) > 0) {
            os.write(buffer)
        }
        os.flush()
        os.close()
        `is`.close()
    }



    fun getListCity(string: String): ArrayList<City> {
        var arr = ArrayList<City>()
        val cursor = this.openDatabase().rawQuery("SELECT * FROM worldcities WHERE city LIKE  '$string%' LIMIT 20 ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val city = cursor.getString(cursor.getColumnIndex("city"))
            val country = cursor.getString(cursor.getColumnIndex("field5"))
            val lat = cursor.getString(cursor.getColumnIndex("lat"))

            val lon = cursor.getString(cursor.getColumnIndex("lng"))


            arr.add(City(city, country,lat.toDouble(),lon.toDouble(),"","",false))
            cursor.moveToNext()
        }

        return arr


    }
}