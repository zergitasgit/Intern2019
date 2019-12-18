package com.example.smartoffice.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartoffice.Helper
import com.example.smartoffice.Helper.Companion.convertToPx
import com.example.smartoffice.ItemMain
import com.example.smartoffice.R
import com.example.smartoffice.`object`.Office
import com.example.smartoffice.adapter.FilesAdapter
import com.example.smartoffice.adapter.PopupFilterAdapter
import com.example.smartoffice.db.DBOffice
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var fileAdapter: FilesAdapter
    private var mTreeSteps = 0
    private var pathFile: String? = null
    private var fileAllDoc = ArrayList<File>()
    var isAllDoc = false
    var arrSearch = ArrayList<Office>();
    var arrOffice = ArrayList<Office>()
    var isSearch = false
    var sharedPreferences: SharedPreferences? = null
    var edit: SharedPreferences.Editor? = null
    var list: List<Office>? = null
    var dbOffice = DBOffice(this, null)
    var arrFilter = ArrayList<Office>()
    private var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUp()
        handlePermission()

        setUpSearch()
        setUpFilter()


    }

    @SuppressLint("WrongConstant")
    private fun setUp() {
//
        sharedPreferences = getSharedPreferences("hieu", Context.MODE_PRIVATE);
        edit = sharedPreferences!!.edit()
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        btn_perm.setOnClickListener {
            handlePermission()
        }

    }


    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        val id = p0.itemId


        when (id) {
            R.id.nav_his -> {showFilterRv(dbOffice.getPDFRecently().toList())
        }
            R.id.nav_view -> {
                mTreeSteps++
                Load(Environment.getExternalStorageDirectory().absolutePath+"/All Documents", true).execute()

            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setUpSearch() {
        iv_search.setOnClickListener {

            iv_search.visibility = View.GONE
            ed_search.visibility = View.VISIBLE
            isSearch = true
            ed_search.requestFocus()
            var imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(ed_search, InputMethodManager.SHOW_IMPLICIT)
            ed_search.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    if (!TextUtils.isEmpty(p0!!.toString())) {
                        iv_search_logic.visibility = View.VISIBLE
                        iv_search_logic.setImageDrawable(resources.getDrawable(R.drawable.close))
                        iv_search_logic.setOnClickListener {
                            ed_search.text.clear()

                        }
                        arrSearch.clear()
                        for (office in arrFilter) {
                            if (office.title.toLowerCase().contains(p0.toString().toLowerCase())) {
                                arrSearch.add(office)
                            }
                        }
                        var list = arrSearch.sortedWith(compareBy { it.title })
                        showRv(list)
                    } else {
                        iv_search_logic.visibility = View.GONE
                    }

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

            })

        }

    }

    private fun setUpFilter() {
        iv_filter.setOnClickListener {
            showListPopupWindow(it)
        }
    }



    private fun showRv(arr: List<Office>) {
        rv.layoutManager = LinearLayoutManager(this)
        fileAdapter = FilesAdapter(this, arr, object : FilesAdapter.Listener {
            override fun onClick(title: String, size: String, path: String, isFolder: Boolean) {
                if (isFolder) {
                    if (title == "All Documents") {
                        mTreeSteps++
                        isAllDoc = true
                        Load(path, true).execute()
                    } else {
                        isAllDoc = false
                        mTreeSteps++
                        Load(path, false).execute()
                    }
                } else {
                    if(dbOffice.checkPath(path)){
                        dbOffice.updateHistory(path,System.currentTimeMillis())
                    }else{
                        dbOffice.insertOffice(title, size, path, System.currentTimeMillis())
                    }

                }
            }

        })
        rv.adapter = fileAdapter

    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun handlePermission() {
        val perms = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(perms, 3)
        } else {
            Helper.createFolder()
            Load(Environment.getExternalStorageDirectory().absolutePath, false)

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
            3 -> {

                if (grantResults[0] == 0) {
                    btn_perm.visibility = View.GONE
                    Helper.createFolder()
                    Load(Environment.getExternalStorageDirectory().absolutePath, false).execute()
                    arrFilter = Helper.getAllDocuments(this)
                }


                return
            }
            else -> return
        }
    }

    override fun onBackPressed() {
        if (mTreeSteps > 0) {
            mTreeSteps--

            Load(getPreviousPath()!!, false).execute()

            return
        } else {
            if (ed_search.hasFocus()) {
                ed_search.visibility = View.GONE
                iv_search.visibility = View.VISIBLE
                iv_search_logic.visibility = View.GONE
                ed_search.text.clear()
                Load(pathFile!!, false).execute()

            } else {
                super.onBackPressed()

            }

        }


    }

    private fun getPreviousPath(): String? {
        val path: String = pathFile!!
        val lastIndexOf = pathFile!!.lastIndexOf(File.separator)
        if (lastIndexOf < 0) {

            return pathFile
        }
        return path.substring(0, lastIndexOf)
    }

    private fun showListPopupWindow(anchor: View) {
        val listPopupItems = ArrayList<ItemMain>()
        listPopupItems.add(ItemMain("All"))
        listPopupItems.add(ItemMain("Pdf"))
        listPopupItems.add(ItemMain("Word"))
        listPopupItems.add(ItemMain("Text"))
        listPopupItems.add(ItemMain("Excel"))
        listPopupItems.add(ItemMain("Power point"))


        val listPopupWindow = createListPopupWindow(anchor, listPopupItems)
        listPopupWindow.show()
    }


    private fun createListPopupWindow(
        anchor: View,
        items: ArrayList<ItemMain>
    ): ListPopupWindow {
        val popup = ListPopupWindow(this)
        val adapter = PopupFilterAdapter(this, items, object : PopupFilterAdapter.ItemListener {
            override fun onClick(position: Int) {
                when (position) {
                    0 -> {
                        showFilterRv(list!!)
                        popup.dismiss()
                    }
                    1 -> {
                        showFilterPdf()
                        popup.dismiss()
                    }
                    2 -> {
                        showFilterWord()
                        popup.dismiss()
                    }
                    3 -> {
                        showFilterTxt()
                        popup.dismiss()

                    }
                    4 -> {
                        showFilterExcel()
                        popup.dismiss()
                    }
                    5 -> {
                        showFilterPpt()
                        popup.dismiss()
                    }

                }

                edit!!.putInt("pos", position)
                edit!!.apply()
//                showListPopupWindow(it)
            }

        })
        popup.setAnchorView(anchor)
        popup.setWidth(convertToPx(180, this))
        popup.setHeight(convertToPx(310, this))
        popup.setAdapter(adapter)
        return popup
    }

    private fun showFilterTxt() {

        var arr = ArrayList<Office>()
        for (office in arrFilter) {
            if (office.title.endsWith(".txt")) arr.add(office)
        }
        showFilterRv(arr)
    }

    private fun showFilterPdf() {
        var arr = ArrayList<Office>()
        for (office in arrFilter) {
            if (office.title.endsWith(".pdf") || office.title.endsWith(".PDF")) arr.add(office)
        }
        showFilterRv(arr)
    }

    private fun showFilterWord() {
        var arr = ArrayList<Office>()
        for (office in arrFilter) {
            if (office.title.endsWith(".doc") || office.title.endsWith(".docx")
                || office.title.endsWith(".DOC") || office.title.endsWith(".DOCX")
            ) arr.add(office)
        }
        showFilterRv(arr)
    }

    private fun showFilterExcel() {
        var arr = ArrayList<Office>()
        for (office in arrFilter) {
            if (office.title.endsWith(".xls") || office.title.endsWith(".xlsx") || office.title.endsWith(
                    ".XLSX"
                )
            ) arr.add(office)
        }
        showFilterRv(arr)
    }

    private fun showFilterPpt() {
        var arr = ArrayList<Office>()
        for (office in arrOffice) {
            if (office.title.endsWith(".ppt") || office.title.endsWith(".pptx") || office.title.endsWith(
                    ".PPTX"
                )
            ) arr.add(office)
        }
        showFilterRv(arr)
    }

    private fun showFilterRv(arr: List<Office>) {
        rv.layoutManager = LinearLayoutManager(this)
        fileAdapter = FilesAdapter(this, arr, object : FilesAdapter.Listener {
            override fun onClick(title: String, size: String, path: String, isFolder: Boolean) {
                if(dbOffice.checkPath(path)){
                    dbOffice.updateHistory(path,System.currentTimeMillis())
                }else{
                    dbOffice.insertOffice(title, size, path, System.currentTimeMillis())
                }


            }

        })
        rv.adapter = fileAdapter
    }
    inner class Load(private var path: String, private var isAll: Boolean) : AsyncTask<Void, Int, Void>() {

        override fun doInBackground(vararg void: Void): Void? {
            pathFile = path
            arrOffice.clear()

            if (!isAll) {
                val files = Helper.getFiles(path)
                for (i in files!!.indices) {
                    if (!files[i].isFile && !files[i].name.startsWith(".")) {
                        arrOffice.add(
                            Office(
                                files[i].name,
                                Helper.getSize(files[i]),
                                files[i].absolutePath,
                                true
                            )
                        )

                    }
                    var name = files[i].name
                    if (files[i].isFile && name.endsWith(".pdf") || name.endsWith(".PDF") || name.endsWith(
                            ".txt"
                        ) || name.endsWith(".doc") ||
                        name.endsWith(".docx") || name.endsWith(".DOC") || name.endsWith(".DOCX") || name.endsWith(
                            ".xls"
                        ) ||
                        name.endsWith(".xlsx") ||
                        name.endsWith(".XLSX") || name.endsWith(".ppt") || name.endsWith(".pptx") || name.endsWith(
                            "PPTX"
                        )
                    ) {
                        arrOffice.add(
                            Office(
                                files[i].name,
                                Helper.getSize(files[i]),
                                files[i].absolutePath,
                                false
                            )
                        )
                    }
                }
            } else {
                arrOffice = Helper.getAllDocuments(this@MainActivity)
            }
            list = arrOffice.sortedWith(compareBy { it.title })
            for (i in list!!.indices){
                if (list!![i].title =="All Documents"){
                    Collections.swap(list, i, 0);
                }
            }

            return null


        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: Void?) {
            //
            // Hide ProgressDialog here
            if (progressDialog != null && progressDialog!!.isShowing() ) {
                progressDialog!!.dismiss()

            }
            showRv(list!!)

        }

        override fun onPreExecute() {
            progressDialog = ProgressDialog(this@MainActivity)
            progressDialog!!.setCancelable(true)
            progressDialog!!.isIndeterminate= false
            progressDialog!!.setMessage("Loading...")
            progressDialog!!.max = 100
            progressDialog!!.show()
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)

                progressDialog!!.progress =values[0]!!.inv()

        }


    }
}




