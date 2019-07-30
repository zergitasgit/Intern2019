package com.vunhiem.appnhacdemo

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.vunhiem.appnhacdemo.adapter.SongListAdapter
import com.vunhiem.appnhacdemo.model.Song
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    var songModelData:ArrayList<Song> = ArrayList()
    var songListAdapter:SongListAdapter?=null

    companion object{
        val PERMISSION_REQUEST_CODE = 12
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE) }
        else{
            loadData()
        }



        }
    fun loadData(){
        var songCursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,null)
        while (songCursor!=null && songCursor.moveToNext()){
            var songName = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
            var songDuration = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
            var songPath = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DATA))
            songModelData.add(Song(songName,songDuration,songPath))
        }
        songListAdapter = SongListAdapter(songModelData, this@MainActivity)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = songListAdapter

}
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"xin quyen doc ghi bo nho", Toast.LENGTH_LONG).show()
                loadData()
            }
        }
    }
    }
