package com.example.tablayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = MyViewPaperAdapter(supportFragmentManager)
        adapter.addFragment(HomeFragment(), "Home")
        adapter.addFragment(PlaceFragment(), "Place")
        adapter.addFragment(ContactFragment(), "Contact")
        //adapter.addFragment(PromotionFragment(),"Promotion")
        vp_home.adapter = adapter
        tab_home.setupWithViewPager(vp_home)

    }

    class MyViewPaperAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        val fragmentList: MutableList<Fragment> = ArrayList()
        val titleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return fragmentList[position]

        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            titleList.add(title)

        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titleList[position]

        }


    }
}
