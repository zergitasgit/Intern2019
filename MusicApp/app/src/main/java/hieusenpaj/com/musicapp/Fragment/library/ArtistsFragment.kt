package hieusenpaj.com.musicapp.Fragment.library


import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hieusenpaj.com.musicapp.Fragment.contentlibrary.ContentArtistsFragment

import hieusenpaj.com.musicapp.R
import hieusenpaj.com.musicapp.`object`.Artists
import hieusenpaj.com.musicapp.`object`.Song
import hieusenpaj.com.musicapp.adapter.ArtistAdapter
import hieusenpaj.com.musicapp.db.DatabaseSong
import kotlinx.android.synthetic.main.fragment_artists.view.*
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 *
 */
class ArtistsFragment : Fragment() {
    val arrArtists : ArrayList<Artists> = ArrayList()
    var arr :ArrayList<Song> = ArrayList()
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_artists, container, false)




        var contentFragment:ContentArtistsFragment = ContentArtistsFragment()

        view.rv_artist.layoutManager = LinearLayoutManager(context)
        var artistAdapter: ArtistAdapter = ArtistAdapter(this!!.context!!, getList(), object : ArtistAdapter.ItemSongListener {
            override fun onClick(pos: Int) {
                openFragment(contentFragment, arrArtists!!.get(pos).title,arrArtists!!.get(pos).track,arrArtists!!.get(pos).art)
            }
        })
        view.rv_artist.adapter = artistAdapter

        return view
    }
    private fun openFragment(fragment: Fragment,name:String,track:Int,art:String) {
        val bundle = Bundle()
        bundle.putString("name", name)
        bundle.putInt("track", track)
        bundle.putString("art", art)
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()


        fragment.arguments = bundle
    }
    fun getList():ArrayList<Artists>{
        arrArtists.clear()
        val db = DatabaseSong(this!!.context!!,null)
        arr = db.getSong()
        val listArtist = arr.groupBy {
            it.artist
        }

        val artNames = listArtist.keys
        for (title in artNames){
            var list = listArtist[title]
            var artistData = Artists(title, list!!.size, list.get(0).art)
            arrArtists.add(artistData)

        }
        return  arrArtists
    }
//    @RequiresApi(Build.VERSION_CODES.KITKAT)
//    fun listArtists(): ArrayList<Artists> {
//        var title: String
//        var track: String
//        var art: String
//        var id: Int
//        //        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0" + " AND " + MediaStore.Audio.Media.DURATION + " > 10000";
//        val albumartData = HashMap<String, String>()
//        val projection1 = arrayOf(MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.ALBUM_ART)
//
//        val content = activity!!.contentResolver
//        val albumArtCursor = content.query(
//                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                projection1,
//                null, null, null)//new String[]{songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))},
//
//
//        while (Objects.requireNonNull<Cursor>(albumArtCursor).moveToNext()) {
//            albumartData[albumArtCursor!!.getString(albumArtCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST))] =
//                    albumArtCursor.getString(albumArtCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
//            //   Log.d(TAG,albumartData.get(AlbumArtCursor.getString(AlbumArtCursor.getColumnIndex(MediaStore.Audio.Albums._ID)))+"Albumdata");
//        }
//
//
//        val projection = arrayOf(MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
//
//        val content1 = activity!!.contentResolver
//        val artist_cursor = content1.query(
//                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
//                projection, null, null,
//                MediaStore.Audio.Artists.ARTIST + "")
//        val listOfSongs = ArrayList<Artists>()
//        artist_cursor!!.moveToFirst()
//
//        while (!artist_cursor.isAfterLast) {
//
//
//            id = artist_cursor.getInt(artist_cursor.getColumnIndex(MediaStore.Audio.Media._ID))
//            title = artist_cursor.getString(artist_cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
//            track = artist_cursor.getString(artist_cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS))
//            art = albumartData[artist_cursor.getString(artist_cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))]!!
//
//
//            val artistData = Artists(title, track.toInt(), art)
//            listOfSongs.add(artistData)
//
//            artist_cursor.moveToNext()
//        }
//        artist_cursor.close()
//
//        return listOfSongs
//
//    }

}
