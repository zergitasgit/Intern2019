package com.example.swipenavigationbar.db

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.swipenavigationbar.`object`.Action
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DBAction(private var context: Context) {

    companion object {
        private val DB_NAME = "action.db"
        private val dbName = "action"
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
    fun getAction(): ArrayList<Action> {
        var arr = ArrayList<Action>()
        val cursor = this.openDatabase().rawQuery("SELECT * FROM ac ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val action = cursor.getString(cursor.getColumnIndex("action"))
            val code = cursor.getString(cursor.getColumnIndex("code"))
            arr.add(Action(action,code,false))
            cursor.moveToNext()
        }

        return arr
    }
}