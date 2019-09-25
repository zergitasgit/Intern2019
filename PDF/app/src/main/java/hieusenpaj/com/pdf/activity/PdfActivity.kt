package hieusenpaj.com.pdf.activity

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.view.Menu
import android.view.MenuItem
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import hieusenpaj.com.pdf.R
import hieusenpaj.com.pdf.db.DbPDF
import hieusenpaj.com.pdf.dialog.PageDialog
import kotlinx.android.synthetic.main.activity_pdf.*
import java.io.File
import hieusenpaj.com.pdf.dialog.DetailDialog
import android.view.View
import kotlinx.android.synthetic.main.activity_pdf.view.*
import kotlinx.android.synthetic.main.dialog_go_to_page.*
import kotlinx.android.synthetic.main.number_picker_layout_custom.*


class PdfActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {
    var path: String? = null
    var name: String? = null
    var date: String? = null
    var size: String? = null
    var menuItem: MenuItem? = null

    var db = DbPDF(this, null)
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
//
        setContentView(R.layout.activity_pdf)
//
//
        sharedPreferences = getSharedPreferences("hieu", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()

        setSupportActionBar(toolbar)
        toolbar.setTitle("PDF")
        toolbar.iv_back.setOnClickListener {
            onBackPressed()
        }
        setUpPdfView()

    }

    private fun setUpPdfView() {
        path = intent.extras!!.getString("path")
        name = intent.extras!!.getString("name")
        date = intent.extras!!.getString("date")
        size = intent.extras!!.getString("size")

        editor!!.putBoolean("clickFavorite",false)
        editor!!.apply()

        var favorite = intent.extras.getInt("favorite")
        if (favorite == 1) {
            iv_favorite.setImageDrawable(resources.getDrawable(R.drawable.favorite_sele_toolbar))
        } else {
            iv_favorite.setImageDrawable(resources.getDrawable(R.drawable.favorite_sele))
        }
        pdfView.setBackgroundColor(Color.WHITE)

        iv_favorite.setOnClickListener {
            if (favorite == 0) {
                iv_favorite.setImageDrawable(resources.getDrawable(R.drawable.favorite_sele_toolbar))
                favorite = 1
                db.updateFavorite(path!!, 1)
            } else {
                iv_favorite.setImageDrawable(resources.getDrawable(R.drawable.favorite_sele))
                favorite = 0
                db.updateFavorite(path!!, 0)
            }
//
            editor!!.putBoolean("clickFavorite",true)
            editor!!.apply()
        }
    }

    private fun checkFavorite() {

    }

    override fun onPageChanged(page: Int, pageCount: Int) {

    }

    override fun loadComplete(nbPages: Int) {
    }

    override fun onPageError(page: Int, t: Throwable?) {
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_pdf, menu)
        this.menuItem = menu.getItem(2)
        setUpPdfViewFalse(menu.getItem(2))
        dislayNightMode(menu.getItem(1))
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        when (item.getItemId()) {

            R.id.menu_page -> {
                dialogPage()
            }

            R.id.menu_night -> {
              checkNightMode(item)

            }
            R.id.menu_mode -> {

                setUpPdfViewTrue(item)
            }
            R.id.menu_properties -> {
                val detailDialog = DetailDialog(this,name!!,path!!,date!!,size!!)
                detailDialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogPage() {
        var dialog = PageDialog(this, object : PageDialog.OnClickDialog {
            override fun onClick(page: Int) {
                pdfView.jumpTo(page - 1)
            }

        },pdfView.pageCount)

        dialog.show()

    }

    private fun setUpPdfViewTrue(item: MenuItem?) {
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
            item?.title = "Vertical mode"
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
            item?.title = "Horizontal mode"
        }
    }

    private fun setUpPdfViewFalse(item: MenuItem) {
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
            item.title = "Horizontal mode"
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
            item.title = "Vertical mode"
        }
    }
    private fun checkNightMode(item: MenuItem){
        if(sharedPreferences!!.getBoolean("night",false)){
            frame_night_mode.visibility = View.GONE
            editor!!.putBoolean("night",false)
            editor!!.apply()
            item.title = "Enable night mode"
        }else{
            frame_night_mode.visibility = View.VISIBLE
            editor!!.putBoolean("night",true)
            editor!!.apply()
            item.title = "Disable night mode"
        }
    }
    private fun dislayNightMode(item: MenuItem){
        if(!sharedPreferences!!.getBoolean("night",false)){
            frame_night_mode.visibility = View.GONE
            item.title = "Enable night mode"
        }else{
            frame_night_mode.visibility = View.VISIBLE
            item.title = "Disable night mode"
        }
    }

}
