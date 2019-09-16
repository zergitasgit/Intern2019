package com.os13musicapp.os13musicplayer.db

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.os13musicapp.os13musicplayer.`object`.Song

class DatabaseSong(private val context: Context,
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
                    + " TEXT," + COLUMN_ARTIST + " TEXT," + COLUMN_ALBUM + " TEXT," +
                    COLUMN_PATH + " TEXT," + COLUMN_DURATION + " REAL, " +
                    COLUMN_AIBUM_ID + " REAL," + COLUMN_ARTIST_ID + " INTEGER," +
                    COLUMN_ART + " TEXT," + COLUMN_RECENTLY + " REAL," + COLUMN_FAVORITE + " INTEGER" + ")")

            p0!!.execSQL(CREATE_PRODUCTS_TABLE)
            edit.putBoolean("saved", true)
            edit.apply()
        } else {

        }
    }

    fun saveDB() {


    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
//        p0!!.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
//        onCreate(p0)
    }

    companion object {
        private val DATABASE_VERSION = 1
        //
        private val DATABASE_NAME = "song.db"
        val TABLE_NAME = "song"
        val COLUMN_ID = "_id"
        val COLUMN_NAME = "name"
        val COLUMN_ARTIST = "artist"
        val COLUMN_ALBUM = "album"
        val COLUMN_PATH = "path"
        val COLUMN_DURATION = "duration"
        val COLUMN_AIBUM_ID = "albumId"
        val COLUMN_ARTIST_ID = "artistId"
        val COLUMN_ART = "art"
        val COLUMN_RECENTLY = "recently"
        val COLUMN_FAVORITE = "favorite"

//        //
//        private val DATABASE_NAME_PLAYLIST = "playlist.db"
//        val TABLE_NAME_PLAYLIST = "playlist"
//        val COLUMN_ID_PLAYLIST = "_id"
//        val COLUMN_NAME_PLAYLIST = "name"
//
//        //
//        private val DATABASE_NAME_PLAYLIST_SONG = "playlist_song.db"
//        val TABLE_NAME_PLAYLIST_SONG  = "playlist_song"
//        val COLUMN_ID_PLAYLIST_SONG  = "_id"
//        val COLUMN_ID_PLAYLIST_ID  = "playlist_id"
//        val COLUMN_ID_SONG_ID  = "song_id"
    }

    fun insertSong(name: String, artist: String, album: String, path: String, duration: Long, albumId: Long, art: String, time: Long) {
        val values = ContentValues()
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_ARTIST, artist)
        values.put(COLUMN_ALBUM, album)
        values.put(COLUMN_PATH, path)
        values.put(COLUMN_DURATION, duration)
        values.put(COLUMN_AIBUM_ID, albumId)
        values.put(COLUMN_ART, art)
        values.put(COLUMN_RECENTLY, time)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getSongOfArttist(name: String): ArrayList<Song> {
        var arr: ArrayList<Song> = ArrayList()
        val db = this.getWritableDatabase()
        val cursor = db.rawQuery("SELECT * FROM song WHERE artist = '$name' ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {

            var song = Song(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("artist")),
                    cursor.getString(cursor.getColumnIndex("album")),
                    cursor.getString(cursor.getColumnIndex("path")), cursor.getLong(cursor.getColumnIndex("duration")),
                    cursor.getLong(cursor.getColumnIndex("albumId")), cursor.getString(cursor.getColumnIndex("art")),
                    cursor.getInt(cursor.getColumnIndex("favorite")))
//            subject.setMon(cursor.getString(cursor.getColumnIndex("name")))
//            subject.setImage(cursor.getBlob(cursor.getColumnIndex("image")))
//            subject.setId(cursor.getString(cursor.getColumnIndex("id")))
            arr.add(song)
            cursor.moveToNext()
        }
        return arr
    }

    fun getSongOfAlbum(albumId: Long): ArrayList<Song> {
        var arr: ArrayList<Song> = ArrayList()
        val db = this.getWritableDatabase()
        val cursor = db.rawQuery("SELECT * FROM song WHERE albumId = '$albumId' ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {

            var song = Song(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("artist")),
                    cursor.getString(cursor.getColumnIndex("album")),
                    cursor.getString(cursor.getColumnIndex("path")), cursor.getLong(cursor.getColumnIndex("duration")),
                    cursor.getLong(cursor.getColumnIndex("albumId")), cursor.getString(cursor.getColumnIndex("art")),
                    cursor.getInt(cursor.getColumnIndex("favorite")))
//            subject.setMon(cursor.getString(cursor.getColumnIndex("name")))
//            subject.setImage(cursor.getBlob(cursor.getColumnIndex("image")))
//            subject.setId(cursor.getString(cursor.getColumnIndex("id")))
            arr.add(song)
            cursor.moveToNext()
        }
        return arr
    }

    fun getPositionSong(path: String): Int {
        var position: Int? = null
        val db = this.getWritableDatabase()
        val cursor = db.rawQuery("SELECT * FROM song WHERE path = '$path' ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            position = cursor.getInt(cursor.getColumnIndex("_id"))
            cursor.moveToNext()
        }
        return position!!
    }

    fun getSongByPath(path: String): ArrayList<Song> {
        var arr = ArrayList<Song>()
        val db = this.getWritableDatabase()
        val cursor = db.rawQuery("SELECT * FROM song WHERE path = '$path' ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            var song = Song(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("artist")),
                    cursor.getString(cursor.getColumnIndex("album")),
                    cursor.getString(cursor.getColumnIndex("path")), cursor.getLong(cursor.getColumnIndex("duration")),
                    cursor.getLong(cursor.getColumnIndex("albumId")), cursor.getString(cursor.getColumnIndex("art")),
                    cursor.getInt(cursor.getColumnIndex("favorite")))
//            subject.setMon(cursor.getString(cursor.getColumnIndex("name")))
//            subject.setImage(cursor.getBlob(cursor.getColumnIndex("image")))
//            subject.setId(cursor.getString(cursor.getColumnIndex("id")))
            arr.add(song)
            cursor.moveToNext()
        }
        return arr
    }

    fun getSongFavorite(): ArrayList<Song> {
        var arr = ArrayList<Song>()
        val db = this.getWritableDatabase()
        val cursor = db.rawQuery("SELECT * FROM song WHERE favorite = 1 ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            var song = Song(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("artist")),
                    cursor.getString(cursor.getColumnIndex("album")),
                    cursor.getString(cursor.getColumnIndex("path")), cursor.getLong(cursor.getColumnIndex("duration")),
                    cursor.getLong(cursor.getColumnIndex("albumId")), cursor.getString(cursor.getColumnIndex("art")),
                    cursor.getInt(cursor.getColumnIndex("favorite")))
//            subject.setMon(cursor.getString(cursor.getColumnIndex("name")))
//            subject.setImage(cursor.getBlob(cursor.getColumnIndex("image")))
//            subject.setId(cursor.getString(cursor.getColumnIndex("id")))
            arr.add(song)
            cursor.moveToNext()
        }
        return arr
    }

    fun updateFavorite(path: String, favorite: Int) {
        val contentValues = ContentValues()
        val db = this.readableDatabase
        contentValues.put(COLUMN_FAVORITE, favorite)
        db.update(TABLE_NAME, contentValues, "path = '$path'", null)
        return
    }

    fun getSong(): ArrayList<Song> {
        var arr: ArrayList<Song> = ArrayList()
        val db = this.getWritableDatabase()
        val cursor = db.rawQuery("SELECT * FROM song ", null)
        cursor.moveToFirst()
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {

                var song = Song(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("artist")),
                        cursor.getString(cursor.getColumnIndex("album")),
                        cursor.getString(cursor.getColumnIndex("path")), cursor.getLong(cursor.getColumnIndex("duration")),
                        cursor.getLong(cursor.getColumnIndex("albumId")), cursor.getString(cursor.getColumnIndex("art")),
                        cursor.getInt(cursor.getColumnIndex("favorite")))
//            subject.setMon(cursor.getString(cursor.getColumnIndex("name")))
//            subject.setImage(cursor.getBlob(cursor.getColumnIndex("image")))
//            subject.setId(cursor.getString(cursor.getColumnIndex("id")))
                arr.add(song)
                cursor.moveToNext()
            }
        }
        return arr
    }

    fun updateRecently(path: String, time: Long) {
        val contentValues = ContentValues()
        val db = this.readableDatabase
        contentValues.put(COLUMN_RECENTLY, time)
        db.update(TABLE_NAME, contentValues, "path = '$path'", null)
        return
    }
//    fun update(id:Long,name:String) {
//        val contentValues = ContentValues()
//        val db = this.getReadableDatabase()
//        contentValues.put(COLUMN_NAME_PLAYLIST, name)
//        db.update(TABLE_NAME_PLAYLIST, contentValues, "id_playlist = '$id'", null)
//        return
//
//    }

    fun getSongRecently(): ArrayList<Song> {
        var arr: ArrayList<Song> = ArrayList()
        val db = this.getWritableDatabase()
        val cursor = db.rawQuery("SELECT * FROM song ORDER BY recently DESC LIMIT 6 ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            var song = Song(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("artist")),
                    cursor.getString(cursor.getColumnIndex("album")),
                    cursor.getString(cursor.getColumnIndex("path")), cursor.getLong(cursor.getColumnIndex("duration")),
                    cursor.getLong(cursor.getColumnIndex("albumId")), cursor.getString(cursor.getColumnIndex("art")),
                    cursor.getInt(cursor.getColumnIndex("favorite")))
//            subject.setMon(cursor.getString(cursor.getColumnIndex("name")))
//            subject.setImage(cursor.getBlob(cursor.getColumnIndex("image")))
//            subject.setId(cursor.getString(cursor.getColumnIndex("id")))
            arr.add(song)
            cursor.moveToNext()
        }
        return arr
    }
    fun deleteId(name: String):Int{
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