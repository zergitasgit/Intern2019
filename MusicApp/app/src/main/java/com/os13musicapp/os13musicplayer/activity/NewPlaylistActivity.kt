package com.os13musicapp.os13musicplayer.activity

import android.app.Dialog
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.os13musicapp.os13musicplayer.R
import com.os13musicapp.os13musicplayer.`object`.Song
import com.os13musicapp.os13musicplayer.`object`.SongAdd
import com.os13musicapp.os13musicplayer.adapter.AdapterAddSong
import com.os13musicapp.os13musicplayer.adapter.AdapterContentAddSong
import com.os13musicapp.os13musicplayer.db.DatabasePlaylist
import com.os13musicapp.os13musicplayer.db.DatabasePlaylistSong
import com.os13musicapp.os13musicplayer.db.DatabaseSong
import kotlinx.android.synthetic.main.activity_new_playlist.*
import kotlinx.android.synthetic.main.dialog_add_song.*
import java.util.ArrayList


class NewPlaylistActivity : AppCompatActivity() {
    val dbSong: DatabaseSong = DatabaseSong(this, null)
    var arrayList = ArrayList<Song>()
    var arraySongAdd = ArrayList<SongAdd>()
    var arraySongAddTrue = ArrayList<SongAdd>()
    private val dbPlaylist = DatabasePlaylist(this, null)
    private val dbPlaylistSong = DatabasePlaylistSong(this, null)
    val time = System.currentTimeMillis()
    var adapter: AdapterAddSong? = null
    var adapterChoose: AdapterContentAddSong?= null
    private val p = Paint()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_playlist)


        arrayList = dbSong.getSong()
        for (i in arrayList.indices) {
            var songAdd = SongAdd(arrayList.get(i), false)
            arraySongAdd.add(songAdd)
        }
        ll_add_song.setOnClickListener {
            val dialog = Dialog(this)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            dialog.setContentView(R.layout.dialog_add_song)
            dialog.show()
            val window = dialog.window
            window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)


//            arraySongAddTrue.clear()
            for (i in arraySongAdd.indices){
                for (k in arraySongAddTrue.indices)
                    if(arraySongAddTrue[k].song.path == arraySongAdd[i].song.path){
                        arraySongAdd[i].ischeck=true
                    }
            }
            dialog.rv_dialog_add_song.layoutManager = LinearLayoutManager(this)
            adapter = AdapterAddSong(this, arraySongAdd, object : AdapterAddSong.ItemSongListener {
                override fun onClick(pos: Int) {
                    arraySongAdd[pos].ischeck = !arraySongAdd[pos].ischeck
                    adapter?.notifyDataSetChanged()
                }

            })

            dialog.rv_dialog_add_song.adapter = adapter
            dialog.rl_done.setOnClickListener {
                dialog.dismiss()
                arraySongAddTrue.clear()
                for (song in arraySongAdd) {
                    if (song.ischeck) {
                        arraySongAddTrue.add(song)
                    }
                }
                Glide
                        .with(this)
                        .load(arraySongAddTrue[0].song.art)
                        .apply(RequestOptions()
                                .placeholder(R.drawable.ic_playlist)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .centerCrop()
                        )
                        .thumbnail(0.5f)
                        .transition(DrawableTransitionOptions()
                                .crossFade()
                        )
                        .into(iv_art)
                enableSwipeToDeleteAndUndo(arraySongAddTrue)


            }
            dialog.rl_cancle.setOnClickListener {
                dialog.dismiss()
                adapter!!.setAllFalse()
            }

        }
        tv_cancel.setOnClickListener {

            onBackPressed()
        }
        tv_done.setOnClickListener {
            if (ed_name_playlist.text.trim().isNotEmpty()) {
                for (song in arraySongAddTrue) {
                    dbPlaylistSong.insert(time, song.song.path, song.song.title)
                }
                dbPlaylist.insert(time, ed_name_playlist.text.toString(),arraySongAddTrue.get(0).song.art)
                onBackPressed()

            } else {
                Toast.makeText(this, this.getString(R.string.enter_name_playlist), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun enableSwipeToDeleteAndUndo(arraySongAddTrue: ArrayList<SongAdd>) {
        rv_new_playlist.layoutManager = LinearLayoutManager(this)
        adapterChoose = AdapterContentAddSong(this, arraySongAddTrue)
        rv_new_playlist.adapter = adapterChoose
        rv_new_playlist.setHasFixedSize(true)
        adapterChoose?.notifyDataSetChanged()
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT
                or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder):
                    Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                adapterChoose?.removeItem(position)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                val icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val itemView = viewHolder.itemView
                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3

                    if (dX > 0) {
                        p.color = Color.parseColor("#388E3C")
                        val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.baseline_delete_outline_white_48dp)
                        val iconDest = RectF(itemView.left.toFloat() + width, itemView.top.toFloat() + width,
                                itemView.left.toFloat() + 2 * width, itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, iconDest, p)
                    } else {
                        p.color = Color.parseColor("#D32F2F")
                        val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(),
                                itemView.bottom.toFloat())
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.baseline_delete_outline_white_48dp)
                        val iconDest = RectF(itemView.right.toFloat() - 2 * width, itemView.top.toFloat() + width,
                                itemView.right.toFloat() - width, itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, iconDest, p)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(rv_new_playlist)


    }
}
