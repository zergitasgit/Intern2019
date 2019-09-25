package hieusenpaj.com.pdf.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import hieusenpaj.com.pdf.R
import kotlinx.android.synthetic.main.tab_item.view.*

class TabAdapter( private var context:Context,
                  private var list: ArrayList<Fragment>,
                  private var arrIcon :ArrayList<Int>, fm: FragmentManager?) : FragmentPagerAdapter(fm) {
    override fun getItem(p0: Int): Fragment = list.get(p0)



    override fun getCount(): Int =list.size
    fun addViewFragment(fragment: Fragment,tabIconResource: Int) {
        list.add(fragment)
        arrIcon.add(tabIconResource)
    }

    fun getTabView(position: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.tab_item, null)
        view.iv_tab.setImageResource(arrIcon.get(position))

        return view
    }



}