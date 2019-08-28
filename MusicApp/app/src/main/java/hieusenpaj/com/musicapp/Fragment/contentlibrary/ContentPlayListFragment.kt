package hieusenpaj.com.musicapp.Fragment.contentlibrary


import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import hieusenpaj.com.musicapp.R
import hieusenpaj.com.musicapp.`object`.Song
import hieusenpaj.com.musicapp.`object`.SongAdd
import hieusenpaj.com.musicapp.activity.MainActivity
import hieusenpaj.com.musicapp.adapter.AdapterAddSong
import hieusenpaj.com.musicapp.adapter.AdapterContentAddSong
import hieusenpaj.com.musicapp.adapter.AdapterContentPlaylist
import hieusenpaj.com.musicapp.db.DatabasePlaylist
import hieusenpaj.com.musicapp.db.DatabasePlaylistSong
import hieusenpaj.com.musicapp.db.DatabaseSong
import kotlinx.android.synthetic.main.activity_new_playlist.*
import kotlinx.android.synthetic.main.dialog_add_song.*
import kotlinx.android.synthetic.main.fragment_content_play_list.*
import kotlinx.android.synthetic.main.fragment_content_play_list.view.*
import android.support.v7.widget.helper.ItemTouchHelper.Callback.makeMovementFlags



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 *
 */
class ContentPlayListFragment : Fragment() {
    var arrPath = ArrayList<String>()
    //    var arrSong = ArrayList<Song>()
    var arrByPath = ArrayList<Song>()
    var arr = ArrayList<SongAdd>()
    var arrayList = ArrayList<Song>()
    var arraySongAdd = ArrayList<SongAdd>()
    var arraySongAddTrue = ArrayList<SongAdd>()
    var checkSua: Boolean = false
    private val p = Paint()
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var adapter: AdapterContentPlaylist? = null
    var adapterAdd: AdapterAddSong? = null
    //    var adapter: AdapterAddSong? = null
//    var adapter: AdapterAddSong? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_content_play_list, container, false)

        sharedPreferences = context!!.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()
        val dbPlaylistSong = DatabasePlaylistSong(context!!, null)
        val dbSong = DatabaseSong(context!!, null)
        val dbPlaylist = DatabasePlaylist(context!!, null)
        val id = arguments!!.getLong("id")
        val name = arguments!!.getString("name")



        arrPath = dbPlaylistSong.getPath(id)
//        arrSong = dbSong.getSong()
        arr.clear()
        for (i in arrPath) {
            arrByPath.clear()
            arrByPath = dbSong.getSongByPath(i)
            var songAdd = SongAdd(arrByPath.get(0), false)
            arr.add(songAdd)
        }



        view.ed_name.text = Editable.Factory.getInstance().newEditable(name)
        setSua(view, arr, dbPlaylistSong, dbPlaylist, id, context!!)


        return view
    }

    fun setSua(view: View, arraySongAddTrue: ArrayList<SongAdd>, db: DatabasePlaylistSong, dbPlaylist: DatabasePlaylist, id: Long
               , context: Context) {
        view.rv_content_playlist.layoutManager = LinearLayoutManager(context)
        view.rv_content_playlist.setHasFixedSize(true)
        adapter = AdapterContentPlaylist(context!!, arraySongAddTrue, object : AdapterContentPlaylist.ItemSongListener {
            override fun onClick(position: Int, art: String, title: String, artist: String, path: String, duration: Long,favorite : Int) {

//                var song = arr.get(pos).song
                editor!!.putString("path", path)
                editor!!.putString("art", art)
                editor!!.putString("artist", artist)
                editor!!.putString("name", title)
//                editor!!.putInt("pos", pos)
                editor!!.putLong("time", duration)
                editor!!.putBoolean("isplay", true)
                editor!!.putLong("playlistId", id)
                editor!!.putString("array", "playlist")
//                    editor.putLong("albumid",id.toLong())
                editor!!.apply()


                (activity as MainActivity).songClicked(art, title, artist, path, duration, "playlist",favorite)
            }


        })
        view.rv_content_playlist.adapter = adapter
        setUpItemTouchCallBack(arraySongAddTrue, db)
        view.tv_sua.setOnClickListener(View.OnClickListener {
            if (checkSua == false) {
                view.tv_sua.text = "Xong"
                checkSua = true

                view.ll_add_song.visibility = View.VISIBLE
                view.ed_name.isEnabled = true
                setUpItemTouchCallBack(arraySongAddTrue, db)
                dialogAddSong(view, context, arraySongAddTrue, id, db)
                enableSwipe(view)

            } else {
                view.tv_sua.text = "Sửa"
                checkSua = false

                view.ll_add_song.visibility = View.GONE
                view.ed_name.isEnabled = false

                db.deleteId(id)
                var time = System.currentTimeMillis()
                dbPlaylist.update(id, view.ed_name.text.toString())
                for (song in arraySongAddTrue) {
                    db.insert(id, song.song.path, song.song.title)
                }
//                dbPlaylist.insert(time, view.ed_name.text.toString())
//                view.rv_content_playlist.layoutManager = LinearLayoutManager(context)
//                view.rv_content_playlist.setHasFixedSize(true)
//                adapter = AdapterContentPlaylist(context!!, arraySongAddTrue, object : AdapterContentPlaylist.ItemSongListener {
//                    override fun onClick(pos: Int) {
//
//
//                        var song = arr.get(pos).song
//                        editor!!.putString("path", song.path)
//                        editor!!.putString("art", song.art)
//                        editor!!.putString("artist", song.artist)
//                        editor!!.putString("name", song.title)
////                        editor!!.putInt("pos", pos)
//                        editor!!.putLong("time", song.duration)
//                        editor!!.putBoolean("isplay", true)
//                        editor!!.putLong("playlistId", id)
//                        editor!!.putString("array", "playlist")
////                    editor.putLong("albumid",id.toLong())
//                        editor!!.apply()
//
//                        (activity as MainActivity).songClicked(song.art, song.title, song.artist, song.path, song.duration, "playlist")
//                    }
//
//                })
//                view.rv_content_playlist.adapter = adapter

//                setUpItemTouchCallBack(arraySongAddTrue, db)
                disableSwip()
//                adapter!!.notifyDataSetChanged()

            }


        })
    }

    private var itemTouchHelper: ItemTouchHelper? = null
    private fun setUpItemTouchCallBack(arraySongAddTrue: ArrayList<SongAdd>, db: DatabasePlaylistSong) {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT
                or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {
                    val deletedModel = arraySongAddTrue.get(position)

                    db.delete(deletedModel.song.path)
                    adapter!!.removeItem(position)


                } else {
                    val deletedModel = arraySongAddTrue.get(position)
                    db.delete(deletedModel.song.path)
                    adapter!!.removeItem(position)
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                val icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val itemView = viewHolder.itemView
                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"))
                        val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.baseline_photo_camera_white_48dp)
                        val icon_dest = RectF(itemView.left.toFloat() + width, itemView.top.toFloat() + width,
                                itemView.left.toFloat() + 2 * width, itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, icon_dest, p)
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"))
                        val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(),
                                itemView.bottom.toFloat())
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.baseline_photo_camera_white_48dp)
                        val icon_dest = RectF(itemView.right.toFloat() - 2 * width, itemView.top.toFloat() + width,
                                itemView.right.toFloat() - width, itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, icon_dest, p)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val dragFlags = 0
                val swipeFlags = ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT

                return makeMovementFlags(dragFlags, swipeFlags)
            }
        }
        itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
    }

    private fun enableSwipe(view: View) {
        itemTouchHelper!!.attachToRecyclerView(view.rv_content_playlist)
    }

    fun disableSwip() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT
                or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {
//                    val deletedModel = arraySongAddTrue.get(position)



                } else {
//                    val deletedModel = arraySongAddTrue.get(position)

                }
            }
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val dragFlags = 0
                val swipeFlags = ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT

                return makeMovementFlags(dragFlags, swipeFlags)
            }
        }
        itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper!!.attachToRecyclerView(null)
    }

    fun dialogAddSong(view: View, context: Context, arraySongAddTrue: ArrayList<SongAdd>, id: Long, db: DatabasePlaylistSong) {
//        arraySongAddTrue.clear()
        val dbSong: DatabaseSong = DatabaseSong(context, null)
        arraySongAdd.clear()
        arrayList = dbSong.getSong()
        for (i in arrayList.indices) {
            var songAdd = SongAdd(arrayList.get(i), false)
            arraySongAdd.add(songAdd)
        }
        view.ll_add_song.setOnClickListener(View.OnClickListener {
            val dialog = Dialog(context)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            dialog.setContentView(R.layout.dialog_add_song)
            dialog.show()
            val window = dialog.window
            window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)


//            arraySongAddTrue.clear()
            for (i in arraySongAdd.indices) {
                for (k in arraySongAddTrue.indices)
                    if (arraySongAddTrue.get(k).song.path.equals(arraySongAdd.get(i).song.path)) {
                        arraySongAdd.get(i).ischeck = true
                    }
            }
            dialog.rv_dialog_add_song.layoutManager = LinearLayoutManager(context)
            adapterAdd = AdapterAddSong(context, arraySongAdd, object : AdapterAddSong.ItemSongListener {
                override fun onClick(pos: Int) {
                    arraySongAdd.get(pos).ischeck = !arraySongAdd.get(pos).ischeck
                    adapterAdd!!.notifyDataSetChanged()
                }

            })

            dialog.rv_dialog_add_song.adapter = adapterAdd
            dialog.rl_done.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
                arraySongAddTrue.clear()

                for (song in arraySongAdd) {
                    if (song.ischeck == true) {
                        arraySongAddTrue.add(song)
                    }
                }
//                view.rv_content_playlist.layoutManager = LinearLayoutManager(context)
//                view.rv_content_playlist.setHasFixedSize(true)
//                adapter = AdapterContentPlaylist(context!!, arraySongAddTrue, object : AdapterContentPlaylist.ItemSongListener {
//                    override fun onClick(pos: Int) {
//
//                    }
//
//                })
//                view.rv_content_playlist.adapter = adapter
                adapter!!.notifyDataSetChanged()
                setUpItemTouchCallBack(arraySongAddTrue, db)



//                enableSwipe()

//                (arraySongAddTrue, arraySongAdd)


            })
            dialog.rl_cancle.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
                setUpItemTouchCallBack(arraySongAddTrue, db)
                adapterAdd!!.setAllFalse()
            })
        })
    }


}
