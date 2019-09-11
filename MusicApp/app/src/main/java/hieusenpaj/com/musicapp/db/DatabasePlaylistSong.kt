package hieusenpaj.com.musicapp.db

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class DatabasePlaylistSong(private val context: Context,
                           factory: SQLiteDatabase.CursorFactory?)
    : SQLiteOpenHelper(context, DATABASE_NAME_PLAYLIST_SONG, factory, DATABASE_VERSION) {
    override fun onCreate(p0: SQLiteDatabase?) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sharedPreferences.edit()
        if (sharedPreferences.getBoolean("playlistsong", false) == false) {
            val CREATE_PRODUCTS_TABLE_PLAYLIST_SONG = ("CREATE TABLE " +
                    TABLE_NAME_PLAYLIST_SONG + "("
                    + COLUMN_ID_PLAYLIST_SONG + " INTEGER PRIMARY KEY," +
                    COLUMN_ID_PLAYLIST_ID
                    + " REAL," + COLUMN_PATH_SONG
                    + " TEXT," + COLUMN_NAME_SONG
                    + " TEXT" +
                    ")")
            p0!!.execSQL(CREATE_PRODUCTS_TABLE_PLAYLIST_SONG)
            edit.putBoolean("playlistsong", true)
            edit.apply()
            Toast.makeText(context, "playlist", Toast.LENGTH_SHORT).show()

        } else {

        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME_PLAYLIST_SONG = "playlist_song.db"
        val TABLE_NAME_PLAYLIST_SONG = "playlist_song"
        val COLUMN_ID_PLAYLIST_SONG = "_id"
        val COLUMN_ID_PLAYLIST_ID = "playlist_id"
        val COLUMN_PATH_SONG = "path_song"
        val COLUMN_NAME_SONG = "name_song"
    }

    fun insert(playlistId: Long, path: String, name: String) {
        val values = ContentValues()
//        values.put(COLUMN_ID_PLAYLIST_SONG, id)
        values.put(COLUMN_ID_PLAYLIST_ID, playlistId)
        values.put(COLUMN_PATH_SONG, path)
        values.put(COLUMN_NAME_SONG, name)
        val db = this.writableDatabase
        db.insert(TABLE_NAME_PLAYLIST_SONG, null, values)
        db.close()
    }

    fun getPath(id: Long): ArrayList<String> {
        val arr: ArrayList<String> = ArrayList()
        val db = this.getWritableDatabase()
        val cursor = db.rawQuery("SELECT * FROM playlist_song WHERE playlist_id = '$id' ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {

//            subject.setMon(cursor.getString(cursor.getColumnIndex("name")))
//            subject.setImage(cursor.getBlob(cursor.getColumnIndex("image")))
//            subject.setId(cursor.getString(cursor.getColumnIndex("id")))
            arr.add(cursor.getString(cursor.getColumnIndex("path_song")))
            cursor.moveToNext()
        }
        return arr
    }

    fun delete(path: String,id: Long): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection =COLUMN_PATH_SONG + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(path)
        // Issue SQL statement.
        db.delete(TABLE_NAME_PLAYLIST_SONG, selection, selectionArgs)
        return true
    }
//    fun deleteId(id: Long): Boolean {
//        // Gets the data repository in write mode
//        val db = writableDatabase
//        // Define 'where' part of query.
//        val selection =COLUMN_ID_PLAYLIST_ID + " LIKE ?"
//        // Specify arguments in placeholder order.
//        val selectionArgs = arrayOf(id.toString())
//        // Issue SQL statement.
//        db.delete(TABLE_NAME_PLAYLIST_SONG, selection, selectionArgs)
//
//        return true
//    }
    fun deleteId(id: Long):Int{
        val db = this.writableDatabase
//        val contentValues = ContentValues()
//        contentValues.put(KEY_ID, emp.userId) // EmpModelClass UserId
        // Deleting Row
        val success = db.delete(TABLE_NAME_PLAYLIST_SONG,"playlist_id="+id,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
}