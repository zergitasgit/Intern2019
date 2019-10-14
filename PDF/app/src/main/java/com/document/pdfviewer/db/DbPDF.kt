package com.document.pdfviewer.db

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.document.pdfviewer.`object`.PDF

class DbPDF(private val context: Context,
            factory: SQLiteDatabase.CursorFactory?)
    : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    override fun onCreate(p0: SQLiteDatabase?) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sharedPreferences.edit()
        if (sharedPreferences.getBoolean("saved", false) == false) {
            val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " +
                    TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME
                    + " TEXT," + COLUMN_DATE + " TEXT," + COLUMN_SIZE + " TEXT," +
                    COLUMN_PATH + " TEXT," + COLUMN_HISTORY + " REAL, " +
                    COLUMN_FAVORITE + " INTEGER" + ")")

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
        private val DATABASE_NAME = "pdf.db"
        val TABLE_NAME = "pdf"
        val COLUMN_ID = "_id"
        val COLUMN_NAME = "name"
        val COLUMN_DATE = "date"
        val COLUMN_SIZE = "size"
        val COLUMN_PATH = "path"
        val COLUMN_HISTORY = "history"
        val COLUMN_FAVORITE = "favorite"
    }
    fun insertSong(name: String, date: String, size: String, path: String, history: Long, favorite:Int) {
        val values = ContentValues()
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_DATE, date)
        values.put(COLUMN_SIZE, size)
        values.put(COLUMN_PATH, path)
        values.put(COLUMN_HISTORY, history)
        values.put(COLUMN_FAVORITE, favorite)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
    fun getPdf(): ArrayList<PDF> {
        var arr: ArrayList<PDF> = ArrayList()
        val db = this.getWritableDatabase()
        val cursor = db.rawQuery("SELECT * FROM pdf ", null)
        cursor.moveToFirst()
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {

                var pdf = PDF(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("date")),
                        cursor.getString(cursor.getColumnIndex("size")),
                        cursor.getString(cursor.getColumnIndex("path")), cursor.getLong(cursor.getColumnIndex("history")),
                        cursor.getInt(cursor.getColumnIndex("favorite")))
//
                arr.add(pdf)
                cursor.moveToNext()
            }
        }
        return arr
    }
    fun updateHistory(path: String, time: Long) {
        val contentValues = ContentValues()
        val db = this.readableDatabase
        contentValues.put(COLUMN_HISTORY, time)
        db.update(TABLE_NAME, contentValues, "path = '$path'", null)
        return
    }
    fun updateFavorite(path: String, favorite: Int) {
        val contentValues = ContentValues()
        val db = this.readableDatabase
        contentValues.put(COLUMN_FAVORITE, favorite)
        db.update(TABLE_NAME, contentValues, "path = '$path'", null)
        return
    }
    fun updateName(path: String, name: String) {
        val contentValues = ContentValues()
        val db = this.readableDatabase
        contentValues.put(COLUMN_NAME, name)
        db.update(TABLE_NAME, contentValues, "path = '$path'", null)
        return
    }
    fun updatePath(path: String,pathOld: String) {
        val contentValues = ContentValues()
        val db = this.readableDatabase
        contentValues.put(COLUMN_PATH, path)
        db.update(TABLE_NAME, contentValues, "path = '$pathOld'", null)
        return
    }
    fun getPDFRecently(): ArrayList<PDF> {
        var arr: ArrayList<PDF> = ArrayList()
        val db = this.getWritableDatabase()
        val cursor = db.rawQuery("SELECT * FROM pdf ORDER BY history DESC  ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {

            var pdf = PDF(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("date")),
                    cursor.getString(cursor.getColumnIndex("size")),
                    cursor.getString(cursor.getColumnIndex("path")), cursor.getLong(cursor.getColumnIndex("history")),
                    cursor.getInt(cursor.getColumnIndex("favorite")))
//
            if(pdf.history>0) {
                arr.add(pdf)
            }
            cursor.moveToNext()
        }
        return arr
    }
    fun getFavorite(): ArrayList<PDF> {
        var arr = ArrayList<PDF>()
        val db = this.getWritableDatabase()
        val cursor = db.rawQuery("SELECT * FROM pdf WHERE favorite = 1 ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {

            var pdf = PDF(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("date")),
                    cursor.getString(cursor.getColumnIndex("size")),
                    cursor.getString(cursor.getColumnIndex("path")), cursor.getLong(cursor.getColumnIndex("history")),
                    cursor.getInt(cursor.getColumnIndex("favorite")))
//
            arr.add(pdf)
            cursor.moveToNext()
        }
        return arr
    }
    fun deleteFile(path: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = COLUMN_PATH + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(path)
        // Issue SQL statement.
        db.delete(TABLE_NAME, selection, selectionArgs)
        return true
    }
    fun delete():Int{
        val db = this.writableDatabase
//        val contentValues = ContentValues()
//        contentValues.put(KEY_ID, emp.userId) // EmpModelClass UserId
        // Deleting Row
        val success = db.delete(TABLE_NAME,null,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
}