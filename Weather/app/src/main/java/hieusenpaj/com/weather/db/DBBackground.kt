package hieusenpaj.com.weather.db

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import hieusenpaj.com.weather.models.BackGround
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DBBackground(private val context: Context) {


    companion object {
        private val DB_NAME = "bg.db"
        private val dbName = "bg"
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
        val cursor = this.openDatabase().rawQuery("SELECT * FROM bg WHERE code = '$code' ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val bgDay = cursor.getBlob(cursor.getColumnIndex("bg_day"))
            val bgNight = cursor.getBlob(cursor.getColumnIndex("bg_night"))



            arr.add(BackGround(code,bgDay,bgNight,bgNight,bgNight))
            cursor.moveToNext()
        }

        return arr
    }
//    fun getIcon(code: Int):ArrayList<BackGround>{
//        var arr = ArrayList<BackGround>()
//        val cursor = this.openDatabase().rawQuery("SELECT * FROM bg WHERE code = '$code' ", null)
//        cursor.moveToFirst()
//        while (!cursor.isAfterLast) {
//            val ivDay = cursor.getBlob(cursor.getColumnIndex("iv_day"))
//            val ivNight = cursor.getBlob(cursor.getColumnIndex("iv_night"))
//
//
//
//            arr.add(BackGround(code,null,null,ivDay,ivNight))
//            cursor.moveToNext()
//        }
//
//        return arr
//    }
}