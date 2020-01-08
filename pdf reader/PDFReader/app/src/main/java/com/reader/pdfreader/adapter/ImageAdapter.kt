package com.camera.film.old.cameravintage.ui.gallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.camera.film.old.cameravintage.R
import com.camera.film.old.cameravintage.data.model.ImageMedia
import com.camera.film.old.cameravintage.ui.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_image.view.*


class ImageAdapter(
    private var images: ArrayList<ImageMedia>,
    private val listener: OnItemImageListener
) : RecyclerView.Adapter<ImageAdapter.AlbumViewHolder>() {
    var isLongClick = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        return AlbumViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_image,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bindData()
    }

    override fun onBindViewHolder(
        holder: AlbumViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && payloads[0] is Boolean) {
            holder.bindData(payloads[0] as Boolean)
        } else
            super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun getItem(pos: Int) = images[pos]

    fun updateData(data: ArrayList<ImageMedia>) {
        this.images = data
        notifyDataSetChanged()
    }

    fun resetList() {
        for ((i, image) in images.withIndex()) {
            if (image.status) {
                images[i].status = false
                notifyItemChanged(i, false)
            }

        }
        isLongClick=false
    }

    fun removeData() {
        val iterator: MutableIterator<ImageMedia> = images.iterator()
        while (iterator.hasNext()) {
            val image = iterator.next()
            if (image.status) { // languages.remove(language); // Don't use ArrayList.remove()
                iterator.remove()
            }
        }
        notifyDataSetChanged()
        isLongClick=false
    }

    inner class AlbumViewHolder(v: View) : BaseViewHolder(v) {
        override fun bindData() {
            val item = getItem(adapterPosition)
            bindData(item.status)
            Glide.with(itemView.context).load(item.pathThumb).into(itemView.iv_thumb)
            itemView.iv_thumb.setOnClickListener {
                if (isLongClick) {
                    images[adapterPosition].status = !item.status
                    val status = images[adapterPosition].status
                    notifyItemChanged(adapterPosition, status)
                    listener.onClickAddItemChecked(item.pathThumb, status)
                } else {
                    listener.onClickItem(adapterPosition)
                }
            }

            itemView.iv_thumb.setOnLongClickListener {
                listener.onLongClick()
                images[adapterPosition].status = !item.status
                val status = images[adapterPosition].status
                listener.onClickAddItemChecked(item.pathThumb, status)
                isLongClick = true
                notifyItemChanged(adapterPosition, status)
                true
            }
        }

        fun bindData(status: Boolean) {
            if (status) itemView.iv_checked.visibility = View.VISIBLE
            else itemView.iv_checked.visibility = View.GONE
        }
    }

    interface OnItemImageListener {
        fun onClickItem(pos: Int)
        fun onLongClick()
        fun onClickAddItemChecked(path: String, status: Boolean)
    }
}