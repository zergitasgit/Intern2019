package com.os13musicapp.os13musicplayer.db

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.os13musicapp.os13musicplayer.`object`.Playlist

class DatabasePlaylist(private val context: Context,
                       factory: SQLiteDatabase.CursorFactory?)
    : SQLiteOpenHelper(context, DATABASE_NAME_PLAYLIST, factory, DATABASE_VERSION) {
    override fun onCreate(p0: SQLiteDatabase?) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sharedPreferences.edit()
        if (sharedPreferences.getBoolean("playlist", false) == false) {
            val CREATE_PRODUCTS_TABLE_PLAYLIST = ("CREATE TABLE " +
                    TABLE_NAME_PLAYLIST + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_ID_PLAYLIST + " REAL," +
                    COLUMN_NAME_PLAYLIST
                    + " TEXT," + COLUMN_ART_PLAYLIST + " TEXT" +
                    ")")

            p0!!.execSQL(CREATE_PRODUCTS_TABLE_PLAYLIST)
            edit.putBoolean("playlist", true)
            edit.apply()

        } else {

        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME_PLAYLIST = "playlist.db"
        val TABLE_NAME_PLAYLIST = "playlist"
        val COLUMN_ID = "_id"
        val COLUMN_ID_PLAYLIST = "id_playlist"

        val COLUMN_NAME_PLAYLIST = "name"
        val COLUMN_ART_PLAYLIST = "art"
    }

    fun insert(id: Long, name: String,art:String) {
        val values = ContentValues()
        values.put(COLUMN_ID_PLAYLIST, id)
        values.put(COLUMN_NAME_PLAYLIST, name)
        values.put(COLUMN_ART_PLAYLIST, art)


        val db = this.writableDatabase
        db.insert(TABLE_NAME_PLAYLIST, null, values)
        db.close()
    }

    fun getPlaylist(): ArrayList<Playlist> {
        var arr: ArrayList<Playlist> = ArrayList()
        val db = this.getWritableDatabase()
        val cursor = db.rawQuery("SELECT * FROM playlist ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast ) {

            var playlist = Playlist(cursor.getLong(cursor.getColumnIndex("id_playlist")),
                    cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("art"))
            )
//            subject.setMon(cursor.getString(cursor.getColumnIndex("name")))
//            subject.setImage(cursor.getBlob(cursor.getColumnIndex("image")))
//            subject.setId(cursor.getString(cursor.getColumnIndex("id")))
            arr.add(playlist)
            cursor.moveToNext()
        }
        return arr
    }

    fun update(id:Long,name:String) {
        val contentValues = ContentValues()
        val db = this.getReadableDatabase()
        contentValues.put(COLUMN_NAME_PLAYLIST, name)
        db.update(TABLE_NAME_PLAYLIST, contentValues, "id_playlist = '$id'", null)
        return

    }
    fun updateArt(id:Long,art:String) {
        val contentValues = ContentValues()
        val db = this.getReadableDatabase()
        contentValues.put(COLUMN_ART_PLAYLIST, art)
        db.update(TABLE_NAME_PLAYLIST, contentValues, "id_playlist = '$id'", null)
        return

    }

}