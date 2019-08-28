package hieusenpaj.com.musicapp.Fragment.library


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import hieusenpaj.com.musicapp.Fragment.contentlibrary.ContentAlbumFragment
import hieusenpaj.com.musicapp.Fragment.contentlibrary.ContentArtistsFragment

import hieusenpaj.com.musicapp.R
import hieusenpaj.com.musicapp.`object`.Album
import hieusenpaj.com.musicapp.`object`.Song
import hieusenpaj.com.musicapp.adapter.AlbumAdapter
import hieusenpaj.com.musicapp.db.DatabaseSong
import kotlinx.android.synthetic.main.fragment_album.*
import kotlinx.android.synthetic.main.fragment_album.view.*
import java.util.ArrayList
import android.support.design.widget.CoordinatorLayout
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.bottom_sheet_layout.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 *
 */
class AlbumFragment : Fragment() {
    var arrAlbum: ArrayList<Album> = ArrayList()
    var arr: ArrayList<Song> = ArrayList()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view :View =  inflater.inflate(R.layout.fragment_album, container, false)

        arrAlbum =getList()
        var fragment:ContentAlbumFragment = ContentAlbumFragment()
        view.rv_album.layoutManager = GridLayoutManager(context!!,2)
        var albumAdapter : AlbumAdapter = AlbumAdapter(this!!.context!!,arrAlbum,object : AlbumAdapter.ItemSongListener {
            override fun onClick(pos: Int) {
                openFragment(fragment,arrAlbum.get(pos).id,arrAlbum.get(pos).title,arrAlbum.get(pos).artist,arrAlbum.get(pos).art)
            }
        })
        view.rv_album.adapter = albumAdapter

//        bottomSheetBehavior = BottomSheetBehavior.from<ConstraintLayout>(bottomSheet)
//        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//
//            }
//
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                when (newState) {
//                    BottomSheetBehavior.STATE_COLLAPSED -> {
//
//                    }
//                    BottomSheetBehavior.STATE_HIDDEN -> {
//
//                    }
//                    BottomSheetBehavior.STATE_EXPANDED -> {
//
//                    }
//                    BottomSheetBehavior.STATE_DRAGGING -> {
//
//                    }
//                    BottomSheetBehavior.STATE_SETTLING -> {
//
//                    }
//                }
//            }
//        })
        view.tv_sort.setOnClickListener(View.OnClickListener {
            showBottomSheetDialog()
        })


        return view
    }
    private fun showBottomSheetDialog() {


        val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        val dialog = BottomSheetDialog(context!!,R.style.CustomBottomSheetDialogTheme)
//        dialog.window.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view)

        dialog.rl_title.setOnClickListener(View.OnClickListener {
            dialog.iv_title.visibility = View.VISIBLE
            dialog.iv_artist.visibility = View.GONE

        })
        dialog.rl_artist.setOnClickListener(View.OnClickListener {
            dialog.iv_artist.visibility = View.VISIBLE
            dialog.iv_title.visibility = View.GONE
        })
        dialog.show()

    }
    private fun openFragment(fragment: Fragment,id: Int,name:String,artist:String,art:String) {
        val bundle = Bundle()
        bundle.putString("name", name)
        bundle.putString("artist", artist)
        bundle.putString("art", art)
        bundle.putInt("id", id)

        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()


        fragment.arguments = bundle
    }
    fun getList() : ArrayList<Album>{
       arrAlbum.clear()
        val db = DatabaseSong(this!!.context!!,null)
        arr = db.getSong()
        val listArtist = arr.groupBy {
            it.album
        }

        val artNames = listArtist.keys
        for (title in artNames){
            val list = listArtist[title]
            val albumData = Album(list!!.get(0).albumId.toInt(), title, list.get(0).artist, list.get(0).art)
            arrAlbum.add(albumData)

        }
        return  arrAlbum
    }

//    fun listAlbum(): ArrayList<Album> {
//        var title: String
//        var artist: String
//        var art: String
//        var id: Int
//        val arrayList = ArrayList<Album>()
//        val projection = arrayOf(MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.ALBUM_ART)
//
//        val content = activity!!.contentResolver
//        val media_cursor = content.query(
//                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                projection, //Selection Statement
//                null, null, //Selection Arguments replacement for ? in where id=?
//                MediaStore.Audio.Albums.ALBUM + "")
//        media_cursor!!.moveToFirst()
//        while (!media_cursor.isAfterLast) {
//
//            title = media_cursor.getString(media_cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM))
//            artist = media_cursor.getString(media_cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST))
//            art = media_cursor.getString(media_cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
//            id = media_cursor.getInt(media_cursor.getColumnIndex(MediaStore.Audio.Albums._ID))
//
//
//
//            val albumData = Album(id,title,artist,art)
//            arrayList.add(albumData)
//            media_cursor.moveToNext()
//        }
//        media_cursor.close()
//        return arrayList
//    }
}
