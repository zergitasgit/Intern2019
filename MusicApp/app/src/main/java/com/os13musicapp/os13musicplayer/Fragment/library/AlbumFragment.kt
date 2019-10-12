package com.os13musicapp.os13musicplayer.fragment.library


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.os13musicapp.os13musicplayer.fragment.contentlibrary.ContentAlbumFragment

import com.os13musicapp.os13musicplayer.R
import com.os13musicapp.os13musicplayer.`object`.Album
import com.os13musicapp.os13musicplayer.`object`.Song
import com.os13musicapp.os13musicplayer.adapter.AlbumAdapter
import com.os13musicapp.os13musicplayer.db.DatabaseSong
import com.znitenda.A
import kotlinx.android.synthetic.main.fragment_album.view.*
import java.util.ArrayList
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
    var albumAdapter : AlbumAdapter?= null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view :View =  inflater.inflate(R.layout.fragment_album, container, false)
        view.ll_back.setOnClickListener(View.OnClickListener {
            activity!!.onBackPressed()
        })
        sharedPreferences = context!!.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()
        arrAlbum =getList()
        if (sharedPreferences?.getString("sort","").equals("title")){
            var list  = arrAlbum.sortedWith(compareBy({ it.title }))
            arrAlbum.clear()
            for(i in list){

                arrAlbum.add(i)
            }
        }else if(sharedPreferences?.getString("sort","").equals("artist")){
            var list = arrAlbum.sortedWith(compareBy({ it.artist }))
            arrAlbum.clear()
            for(i in list){

                arrAlbum.add(i)
            }
        }
        var fragment:ContentAlbumFragment = ContentAlbumFragment()
        view.rv_album.layoutManager = GridLayoutManager(context!!,2)
         albumAdapter  = AlbumAdapter(this!!.context!!, arrAlbum ,object : AlbumAdapter.ItemSongListener {
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


        A.f(activity)
        return view
    }
    private fun showBottomSheetDialog() {


        val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        val dialog = BottomSheetDialog(context!!,R.style.CustomBottomSheetDialogTheme)
//        dialog.window.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view)
        if (sharedPreferences?.getString("sort","").equals("title")){
            dialog.iv_title.visibility = View.VISIBLE
            dialog.iv_artist.visibility = View.GONE
        }else  if (sharedPreferences?.getString("sort","").equals("artist")){
            dialog.iv_artist.visibility = View.VISIBLE
            dialog.iv_title.visibility = View.GONE
        }
        dialog.rl_title.setOnClickListener(View.OnClickListener {
            dialog.iv_title.visibility = View.VISIBLE
            dialog.iv_artist.visibility = View.GONE
            var list  = arrAlbum.sortedWith(compareBy({ it.title }))
            arrAlbum.clear()
            for(i in list){

                arrAlbum.add(i)
            }
            albumAdapter?.notifyDataSetChanged()
            dialog.dismiss()
            editor!!.putString("sort","title")
            editor!!.apply()


        })
        dialog.rl_artist.setOnClickListener(View.OnClickListener {
            dialog.iv_artist.visibility = View.VISIBLE
            dialog.iv_title.visibility = View.GONE
            var list = arrAlbum.sortedWith(compareBy({ it.artist }))
            arrAlbum.clear()
            for(i in list){

                arrAlbum.add(i)
            }
            albumAdapter?.notifyDataSetChanged()
            dialog.dismiss()
            editor!!.putString("sort","artist")
            editor!!.apply()
        })
        dialog.rl_cancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
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
