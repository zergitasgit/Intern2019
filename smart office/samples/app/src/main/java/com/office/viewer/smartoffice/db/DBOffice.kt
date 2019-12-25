package com.office.viewer.smartoffice.db

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.office.viewer.smartoffice.`object`.Office

class DBOffice(
    private val context: Context,
    factory: SQLiteDatabase.CursorFactory?
) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    override fun onCreate(p0: SQLiteDatabase?) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sharedPreferences.edit()
        if (!sharedPreferences.getBoolean("saved", false)) {
            val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " +
                    TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_TITLE
                    + " TEXT," + COLUMN_SIZE + " TEXT," +
                    COLUMN_PATH + " TEXT," + COLUMN_HISTORY + " REAL" +
                    ")")

            p0!!.execSQL(CREATE_PRODUCTS_TABLE)
            edit.putBoolean("saved", true)
            edit.apply()
        } else {

        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "office.db"
        val TABLE_NAME = "office"
        val COLUMN_ID = "_id"
        val COLUMN_TITLE = "title"
        val COLUMN_SIZE = "size"
        val COLUMN_PATH = "path"
        val COLUMN_HISTORY = "history"
    }

    fun insertOffice(name: String, size: String, path: String, history: Long) {
        val values = ContentValues()
        values.put(COLUMN_TITLE, name)
        values.put(COLUMN_SIZE, size)
        values.put(COLUMN_PATH, path)
        values.put(COLUMN_HISTORY, history)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun updateHistory(path: String, time: Long) {
        val contentValues = ContentValues()
        val db = this.readableDatabase
        contentValues.put(COLUMN_HISTORY, time)
        db.update(TABLE_NAME, contentValues, "path = '$path'", null)
        return
    }

    fun getPDFRecently(): ArrayList<Office> {
        var arr: ArrayList<Office> = ArrayList()
        val db = this.getWritableDatabase()
        val cursor = db.rawQuery("SELECT * FROM office ORDER BY history DESC  ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {

            var office = Office(
                cursor.getString(cursor.getColumnIndex("title")),
                cursor.getString(cursor.getColumnIndex("size")),
                cursor.getString(cursor.getColumnIndex("path")), false)
//

            arr.add(office)

            cursor.moveToNext()
        }
        return arr
    }
    fun checkPath(path: String): Boolean {
        var arr: ArrayList<Office> = ArrayList()
        val db = this.getWritableDatabase()
        val cursor = db.rawQuery("SELECT * FROM office WHERE path = '$path' ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {

            var office = Office(
                cursor.getString(cursor.getColumnIndex("title")),
                cursor.getString(cursor.getColumnIndex("size")),
                cursor.getString(cursor.getColumnIndex("path")), false)
//

            arr.add(office)

            cursor.moveToNext()
        }
        if (arr.size>0){
            return true
        }
        return false
    }
}