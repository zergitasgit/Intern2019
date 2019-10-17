package hieusenpaj.com.weather.db

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import hieusenpaj.com.weather.models.Add
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DBCity (private val context: Context) {
    companion object {
        private val DB_NAME = "city.db"
    }
    fun openDatabase(): SQLiteDatabase {
        val dbFile = context.getDatabasePath(DB_NAME)

        if (!dbFile.exists()) {
            try {
                val checkDB = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null)
                checkDB?.close()
                copyDatabase(dbFile)
            } catch (e: IOException) {
                throw RuntimeException("error", e)
            }
        }
        return SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }

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

    fun getCityByName(name:String) : Add{
        var add :Add?=null
        val cursor=this.openDatabase().rawQuery("SELECT * FROM worldcities WHERE city = '$name' ", null)
        cursor.moveToFirst()
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
             add = Add(cursor.getString(cursor.getColumnIndex("lat")).toDouble(),
                    cursor.getString(cursor.getColumnIndex("lng")).toDouble())

            cursor.moveToNext()
        }

        return add!!
    }
    fun getListCity():ArrayList<String>{
        var arr = ArrayList<String>()
        val cursor=this.openDatabase().rawQuery("SELECT * FROM worldcities ", null)
        cursor.moveToFirst()
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            var name = cursor.getString(cursor.getColumnIndex("city"))
            arr.add(name)


            cursor.moveToNext()
        }
        return arr
    }
}