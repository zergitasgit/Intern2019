package com.reader.pdfreader.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.fragment.app.Fragment
import com.document.pdfviewer.`object`.ItemMain
import com.reader.pdfreader.R
import com.reader.pdfreader.adapter.ItemMainAdapter
import com.reader.pdfreader.adapter.TabAdapter
import com.reader.pdfreader.fragment.DislayPDFFragment
import com.reader.pdfreader.fragment.FavoriteFragment
import com.reader.pdfreader.fragment.RecentlyFragment
import com.reader.pdfreader.helper.KeyboardToggleListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var arrFragment = ArrayList<Fragment>()
    var arrIcon = ArrayList<Int>()
    var pos: Int? = null
    var tabAdapter: TabAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handlePermission()
        setUpToolBar()
        this.addKeyboardToggleListener {
            rl.viewTreeObserver.addOnGlobalLayoutListener {


                Handler().postDelayed({
                    val heightDiff = rl.rootView.height - rl.height
                    if (heightDiff > 300) {
                    } else {

                        iv_search.visibility = View.VISIBLE
                        ed_search.visibility = View.GONE
                        tv_title.visibility = View.VISIBLE
                        iv_search_logic.visibility = View.GONE
                        ed_search.text.clear()

                    }
                }, 500)


            }


        }
    }

    private fun handlePermission() {
        val perms = arrayOf(
            "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"
        )
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(perms, 3)
        } else {
            setUpViewPager()
            setTabView()
            setUpSearch()
        }

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            3/*200*/ -> {
                if (grantResults[0] == 0) {
                    btn_perm.visibility = View.GONE
                    setUpViewPager()
                    setTabView()
                    setUpSearch()


                } else {
                    btn_perm.visibility = View.VISIBLE
                    rl_per.visibility = View.VISIBLE
                    btn_perm.setOnClickListener {
                        handlePermission()
                    }
                }

                return
            }
            else -> return
        }
    }

    private fun setUpToolBar() {
        setSupportActionBar(toolbar)
        toolbar.title = "PDF Reader"
    }

    private fun setUpViewPager() {
        tabAdapter = TabAdapter(this, arrFragment, arrIcon, supportFragmentManager)
        tabAdapter!!.addViewFragment(
            DislayPDFFragment(),
            R.drawable.tab_pdf
        )
        tabAdapter!!.addViewFragment(
            RecentlyFragment(),
            R.drawable.tab_history
        )
        tabAdapter!!.addViewFragment(
            FavoriteFragment(),
            R.drawable.tab_favorite
        )
        viewpager.offscreenPageLimit = 3
        viewpager.adapter = tabAdapter

    }

    private fun setTabView() {
        sliding_tabs.setupWithViewPager(viewpager)
        for (i in 0 until sliding_tabs.tabCount) {
            sliding_tabs.getTabAt(i)!!.customView = tabAdapter?.getTabView(i)
        }

    }

    private fun setUpSearch() {
        iv_search.setOnClickListener {
            iv_search.visibility = View.GONE
            ed_search.visibility = View.VISIBLE
            tv_title.visibility = View.GONE
            ed_search.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(ed_search, InputMethodManager.SHOW_IMPLICIT)
            ed_search.addTextChangedListener(object : TextWatcher {
                @SuppressLint("DefaultLocale")
                override fun afterTextChanged(p0: Editable?) {
                    if (!TextUtils.isEmpty(p0!!.toString())) {
                        iv_search_logic.visibility = View.VISIBLE
                        iv_search_logic.setImageDrawable(resources.getDrawable(R.drawable.close))
                        iv_search_logic.setOnClickListener {
                            ed_search.text.clear()

                        }

                    } else {
                        iv_search_logic.visibility = View.GONE
                    }
//                    arrFilter = Helper.getAllDocuments(this@MainActivity)
                    val intent = Intent("SEARCH")
                    intent.putExtra("string", p0.toString())
                    sendBroadcast(intent)
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

            })
        }
        iv_filter.setOnClickListener {
            showListPopupWindow(it)
        }
    }


    private fun Activity.addKeyboardToggleListener(onKeyboardToggleAction: (shown: Boolean) -> Unit): KeyboardToggleListener? {
        val root = findViewById<View>(android.R.id.content)
        val listener = KeyboardToggleListener(
            root,
            onKeyboardToggleAction
        )
        return root?.viewTreeObserver?.run {
            addOnGlobalLayoutListener(listener)
            listener
        }
    }

    private fun showListPopupWindow(anchor: View) {
        val listPopupItems = ArrayList<ItemMain>()
        listPopupItems.add(ItemMain(resources.getString(R.string.name), R.drawable.radio_off))
        listPopupItems.add(ItemMain(resources.getString(R.string.dateP), R.drawable.radio_off))
        listPopupItems.add(ItemMain(resources.getString(R.string.size), R.drawable.radio_off))


        val listPopupWindow = createListPopupWindow(anchor, listPopupItems)
        listPopupWindow.show()
    }


    private fun createListPopupWindow(
        anchor: View,
        items: ArrayList<ItemMain>
    ): ListPopupWindow {
        val popup = ListPopupWindow(this)
        val adapter = ItemMainAdapter(this, items, object : ItemMainAdapter.ItemListener {
            override fun onClick(position: Int) {
                when (position) {
                    0 -> {
                        val intent = Intent("NAME")
                        sendBroadcast(intent)
                    }
                    1 -> {

                    }
                    2 -> {

                    }
                    3 -> {


                    }
                }
//                showListPopupWindow(it)
            }

        })
        popup.anchorView = anchor
        popup.width = convertToPx(150)
        popup.height = convertToPx(150)
        popup.setAdapter(adapter)
        return popup
    }

    private fun convertToPx(dp: Int): Int {
        // Get the screen's density scale
        val scale = resources.displayMetrics.density
        // Convert the dps to pixels, based on density scale
        return (dp * scale + 0.5f).toInt()
    }
}
