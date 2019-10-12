package com.document.pdfviewer.activity

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.ListPopupWindow
import android.view.*
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.document.pdfviewer.R
import com.document.pdfviewer.`object`.ItemPDF
import com.document.pdfviewer.adapter.ItemPDFAdapter
import com.document.pdfviewer.db.DbPDF
import com.document.pdfviewer.dialog.PageDialog
import kotlinx.android.synthetic.main.activity_pdf.*
import java.io.File
import com.document.pdfviewer.dialog.DetailDialog
import com.znitenda.A
import kotlinx.android.synthetic.main.activity_pdf.view.*


class PdfActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {
    var path: String? = null
    var name: String? = null
    var date: String? = null
    var size: String? = null
    var menuItem: MenuItem? = null
    var adapter: ItemPDFAdapter? = null

    var db = DbPDF(this, null)
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
//
        setBgStatusbar()
        setContentView(R.layout.activity_pdf)
//
//
        sharedPreferences = getSharedPreferences("hieu", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()


        setUpPdfView()

        A.f(this)

    }

    private fun setBgStatusbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            val background = resources.getDrawable(R.drawable.gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(android.R.color.transparent))
            window.setNavigationBarColor(resources.getColor(android.R.color.transparent))
            window.setBackgroundDrawable(background)
        }
    }

    private fun setUpPdfView() {
        path = intent.extras!!.getString("path")
        name = intent.extras!!.getString("name")
        date = intent.extras!!.getString("date")
        size = intent.extras!!.getString("size")


        setSupportActionBar(toolbar)
        toolbar.setTitle("PDF Viewer")
        toolbar.iv_back.setOnClickListener {
            onBackPressed()
        }
        toolbar.iv_more.setOnClickListener {
            showListPopupWindow(it, name!!, path!!, date!!, size!!)
        }


        dislayHorizontal()
        dislayNightMode()



        editor!!.putBoolean("clickFavorite", false)
        editor!!.apply()

        var favorite = intent.extras.getInt("favorite")
        if (favorite == 1) {
            iv_favorite.setImageDrawable(resources.getDrawable(R.drawable.ic_like_click))
        } else {
            iv_favorite.setImageDrawable(resources.getDrawable(R.drawable.ic_like))
        }
        pdfView.setBackgroundColor(Color.WHITE)

        iv_favorite.setOnClickListener {
            if (favorite == 0) {
                iv_favorite.setImageDrawable(resources.getDrawable(R.drawable.ic_like_click))
                favorite = 1
                db.updateFavorite(path!!, 1)
            } else {
                iv_favorite.setImageDrawable(resources.getDrawable(R.drawable.ic_like))
                favorite = 0
                db.updateFavorite(path!!, 0)
            }
//
            editor!!.putBoolean("clickFavorite", true)
            editor!!.apply()
        }
    }


    override fun onPageChanged(page: Int, pageCount: Int) {

    }

    override fun loadComplete(nbPages: Int) {
    }

    override fun onPageError(page: Int, t: Throwable?) {
    }

    private fun showListPopupWindow(anchor: View, name: String, path: String, date: String, size: String) {
        val listPopupItems = ArrayList<ItemPDF>()
        listPopupItems.add(ItemPDF(resources.getString(R.string.go_to_page), R.drawable.ic_go_to_page, 0))
        if (!sharedPreferences!!.getBoolean("night", false)) {
            listPopupItems.add(ItemPDF(resources.getString(R.string.night_mode), R.drawable.ic_night_mode, R.drawable.ic_ic_off))
        } else {
            listPopupItems.add(ItemPDF(resources.getString(R.string.night_mode), R.drawable.ic_night_mode, R.drawable.ic_ic_on))

        }
        if(!sharedPreferences!!.getBoolean("ishorizontal", false)) {
            listPopupItems.add(ItemPDF(resources.getString(R.string.h_mode), R.drawable.ic_horizontal, 0))
        }else{
            listPopupItems.add(ItemPDF(resources.getString(R.string.v_mode), R.drawable.ic_horizontal, 0))

        }
        listPopupItems.add(ItemPDF(resources.getString(R.string.properties), R.drawable.ic_detail, 0))

        val listPopupWindow = createListPopupWindow(anchor, name, path, date, size, listPopupItems)
        listPopupWindow.show()

    }


    private fun createListPopupWindow(anchor: View, name: String, path: String, date: String, size: String,
                                      items: ArrayList<ItemPDF>): ListPopupWindow {
        val popup = ListPopupWindow(this)
        adapter = ItemPDFAdapter(this, items, object : ItemPDFAdapter.ItemListener {
            override fun onClick(position: Int, it: View) {
                when (position) {
                    0 -> {
                        dialogPage()
                        popup.dismiss()
                    }
                    1 -> {
                        checkNightMode(it, adapter!!)
                        popup.dismiss()
                    }
                    2 -> {
                        checkHorizontal(it, adapter!!)
                        popup.dismiss()
                    }
                    3 -> {
                        val detailDialog = DetailDialog(this@PdfActivity, name, path, date, size)
                        detailDialog.show()
                        popup.dismiss()
                    }
                }
//                showListPopupWindow(it)
            }

        })
        popup.setAnchorView(anchor)

        popup.setHeight(convertToPx(180));
        popup.setWidth(convertToPx(200));
        popup.setAdapter(adapter)
        return popup
    }

    private fun dialogPage() {
        var dialog = PageDialog(this, object : PageDialog.OnClickDialog {
            override fun onClick(page: Int) {
                pdfView.jumpTo(page - 1)
            }

        }, pdfView.pageCount)

        dialog.show()

    }
    private fun convertToPx(dp: Int): Int {
        // Get the screen's density scale
        val scale = resources.displayMetrics.density
        // Convert the dps to pixels, based on density scale
        return (dp * scale + 0.5f).toInt()
    }
    private fun checkHorizontal(view: View, adapter: ItemPDFAdapter) {
        if (!sharedPreferences!!.getBoolean("ishorizontal", false)) {
            pdfView.fromFile(File(path))
                    .defaultPage(0)
                    .onPageChange(this)
                    .enableAnnotationRendering(true)
                    .onLoad(this)
                    .scrollHandle(DefaultScrollHandle(this))
                    .spacing(10) // in dp
                    .onPageError(this)
                    .swipeHorizontal(true)
                    .load()
            editor!!.putBoolean("ishorizontal", true)
            editor!!.apply()
            adapter.updateHorizontal(view, true)
        } else {
            pdfView.fromFile(File(path))
                    .defaultPage(0)
                    .onPageChange(this)
                    .enableAnnotationRendering(true)
                    .onLoad(this)
                    .scrollHandle(DefaultScrollHandle(this))
                    .spacing(10) // in dp
                    .onPageError(this)
                    .load()
            editor!!.putBoolean("ishorizontal", false)
            editor!!.apply()
            adapter.updateHorizontal(view, false)
        }
    }

    private fun dislayHorizontal() {
        if (!sharedPreferences!!.getBoolean("ishorizontal", false)) {
            pdfView.fromFile(File(path))
                    .defaultPage(0)
                    .onPageChange(this)
                    .enableAnnotationRendering(true)
                    .onLoad(this)
                    .scrollHandle(DefaultScrollHandle(this))
                    .spacing(10) // in dp
                    .onPageError(this)
                    .enableAnnotationRendering(true)
                    .load()

        } else {
            pdfView.fromFile(File(path))
                    .defaultPage(0)
                    .onPageChange(this)
                    .enableAnnotationRendering(true)
                    .onLoad(this)
                    .scrollHandle(DefaultScrollHandle(this))
                    .spacing(10) // in dp
                    .onPageError(this)
                    .swipeHorizontal(true)
                    .load()

        }
    }

    private fun checkNightMode(view: View, adapter: ItemPDFAdapter) {
        if (!sharedPreferences!!.getBoolean("night", false)) {
            frame_night_mode.visibility = View.VISIBLE
            adapter.updateNightMode(view, false)
            editor!!.putBoolean("night", true)
            editor!!.apply()
        } else {
            frame_night_mode.visibility = View.GONE
            adapter.updateNightMode(view, true)
            editor!!.putBoolean("night", false)
            editor!!.apply()
        }
    }

    private fun dislayNightMode() {
        if (!sharedPreferences!!.getBoolean("night", false)) {
            frame_night_mode.visibility = View.GONE

        } else {
            frame_night_mode.visibility = View.VISIBLE

        }
    }

}
