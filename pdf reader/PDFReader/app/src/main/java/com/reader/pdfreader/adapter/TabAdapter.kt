package com.reader.pdfreader.adapter

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.reader.pdfreader.R
import kotlinx.android.synthetic.main.tab_item.view.*

class TabAdapter(private var context:Context,
                 private var list: ArrayList<Fragment>,
                 private var arrIcon :ArrayList<Int>, fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
    override fun getItem(p0: Int): Fragment = list.get(p0)



    override fun getCount(): Int =list.size
    fun addViewFragment(fragment: Fragment,tabIconResource: Int) {
        list.add(fragment)
        arrIcon.add(tabIconResource)
    }

    fun getTabView(position: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.tab_item, null)
        Glide
            .with(context)
            .load(arrIcon[position])
            .thumbnail(0.5f)
            .transition(
                DrawableTransitionOptions()
                    .crossFade()
            )
            .into(view.iv_tab)
        return view
    }



}