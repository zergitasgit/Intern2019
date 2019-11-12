package hieusenpaj.com.weather.db

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import hieusenpaj.com.weather.models.BackGround
import hieusenpaj.com.weather.models.Language
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DBBackground(private val context: Context) {


    companion object {
        private val DB_NAME = "icon.db"
        private val dbName = "icon"
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
    fun getBG(code:Int):ArrayList<BackGround>{
        var arr = ArrayList<BackGround>()
        val cursor = this.openDatabase().rawQuery("SELECT * FROM icon WHERE code = '$code' ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val bgDay = cursor.getString(cursor.getColumnIndex("bg_day"))
            val bgNight = cursor.getString(cursor.getColumnIndex("bg_night"))
            val ivDay = cursor.getBlob(cursor.getColumnIndex("iv_day"))
            val ivNight = cursor.getBlob(cursor.getColumnIndex("iv_night"))
            val check = cursor.getString(cursor.getColumnIndex("check"))



            arr.add(BackGround(code,bgDay,bgNight,ivDay,ivNight,check))
            cursor.moveToNext()
        }

        return arr
    }
    fun getLanguage(code: String): Language {

        var la: Language? = null
        val cursor = this.openDatabase().rawQuery("SELECT * FROM icon WHERE code = '$code' ", null)

        cursor!!.moveToFirst()
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {

                var en = cursor.getString(cursor.getColumnIndex("bg_day"))
                var vn = cursor.getString(cursor.getColumnIndex("vn"))
                la = Language(en!!,vn!!)

                cursor.moveToNext()
            }
        }
        return la!!
    }
//
//
}