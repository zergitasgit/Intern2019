package com.office.viewer.smartoffice.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaScannerConnection
import android.os.*
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.office.viewer.smartoffice.helper.Helper
import com.office.viewer.smartoffice.helper.Helper.Companion.convertToPx
import com.office.viewer.ItemMain
import com.office.viewer.smartoffice.helper.KeyboardToggleListener
import com.office.viewer.smartoffice.`object`.Office
import com.office.viewer.smartoffice.adapter.FilesAdapter
import com.office.viewer.smartoffice.adapter.PopupFilterAdapter
import com.office.viewer.smartoffice.db.DBOffice
import com.google.android.material.navigation.NavigationView
import com.office.viewer.smartoffice.R
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
    var arrSearch = ArrayList<Office>()
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

        handlePermission()
        setUp()
        setUpSearch()
        setUpFilter()
        this.addKeyboardToggleListener {
            rl.viewTreeObserver.addOnGlobalLayoutListener {
                Handler().postDelayed({
                     val heightDiff = rl.rootView.height - rl.height
                    if (heightDiff > 100) {
                    } else {
                        if (TextUtils.isEmpty(ed_search.text.toString())) {
                            clearText()
                        }
                    }
                }, 500)



            }


        }


    }


    @SuppressLint("WrongConstant", "CommitPrefEdits")
    private fun setUp() {
//
        sharedPreferences = getSharedPreferences("hieu", Context.MODE_PRIVATE)
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


    }


    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        if (sharedPreferences!!.getBoolean("permission", false)) {
            when (p0.itemId) {
                R.id.nav_his -> {

                    hideKeybroad()
                    showFilterRv(dbOffice.getPDFRecently().toList())
                    tv_title.text = getString(R.string.history)
                    mTreeSteps++
                    ganFilPath()
                }
                R.id.nav_view -> {
                    hideKeybroad()
                    mTreeSteps++
                    Load(
                        Environment.getExternalStorageDirectory().absolutePath + "/All Documents",
                        true
                    ).execute()


                }
                R.id.nav_share -> {
                    Helper.shareApp(this)

                }
                R.id.nav_rate -> {
                    Helper.shareApp(this)
                }
            }

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setUpSearch() {

        iv_search.setOnClickListener {

            if (sharedPreferences!!.getBoolean("permission", false)) {
                iv_search.visibility = View.GONE
                ed_search.visibility = View.VISIBLE
                tv_title.visibility = View.GONE
                isSearch = true
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
                        arrSearch.clear()
                        for (office in arrFilter) {
                            if (office.title.toLowerCase().contains(p0.toString().toLowerCase())) {
                                arrSearch.add(office)
                            }
                        }
                        val list = arrSearch.sortedWith(compareBy { it.title })
                        showRv(list)

                        ganFilPath()
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                })

            }
        }

    }

    fun Context.updateContentProvider(vararg path: String) {
        MediaScannerConnection.scanFile(
            this,
            path, null
        ) { _, _ -> }
    }

    private fun setUpFilter() {

        iv_filter.setOnClickListener {
            if (sharedPreferences!!.getBoolean("permission", false)) {
                showListPopupWindow(it)
            }
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
//                    val intent = Intent(this@MainActivity,DislayOfficeActicity::class.java)
//                    intent.putExtra("path",path)
//                    startActivity(intent)
                    Helper.openSimpleReaderActivity(this@MainActivity, path)
                    if (dbOffice.checkPath(path)) {
                        dbOffice.updateHistory(path, System.currentTimeMillis())
                    } else {
                        dbOffice.insertOffice(title, size, path, System.currentTimeMillis())
                    }
                    edit!!.putString("title", title)
                    edit!!.apply()


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
            edit!!.putBoolean("permission", true)
            edit!!.apply()
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
                    edit!!.putBoolean("permission", true)
                    edit!!.apply()
                    btn_perm.visibility = View.GONE
                    rl_per.visibility = View.GONE
                    Helper.createFolder()
                    Load(Environment.getExternalStorageDirectory().absolutePath, false).execute()


                    val sharedPreferences = getSharedPreferences("hieu", Context.MODE_PRIVATE)
                    val path = sharedPreferences!!.getString("title", "hieu.pdf").substring(
                        0,
                        sharedPreferences.getString("title", "hieu.pdf").lastIndexOf(".")
                    )
                    val file =
                        File(Environment.getExternalStorageDirectory().absolutePath + "/" + path + ".pdf")

                    if (file.exists()) {
                        updateContentProvider(Environment.getExternalStorageDirectory().absolutePath + "/" + path + ".pdf")

                    }


                    arrFilter = Helper.getAllDocuments(this)
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

    override fun onBackPressed() {
        if (mTreeSteps > 0) {
            mTreeSteps--
            Load(getPreviousPath()!!, false).execute()
            if (TextUtils.isEmpty(ed_search.text.toString())) {

                clearText()
            } else {
            }
            return
        } else {

            if (ed_search.hasFocus()) {
                clearText()
//                ed_search.text.clear()
            } else {
                super.onBackPressed()

            }
        }

    }

    private fun clearText() {
        ed_search.visibility = View.GONE
        tv_title.visibility = View.VISIBLE
        iv_search.visibility = View.VISIBLE
        iv_search_logic.visibility = View.GONE
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
        listPopupItems.add(ItemMain(getString(R.string.all), R.drawable.ic_all))
        listPopupItems.add(ItemMain("Pdf", R.drawable.ic_pdf))
        listPopupItems.add(ItemMain("Word", R.drawable.ic_word))
        listPopupItems.add(ItemMain("Text", R.drawable.ic_txt))
        listPopupItems.add(ItemMain("Excel", R.drawable.ic_excel))
        listPopupItems.add(ItemMain("Power point", R.drawable.ic_ppt))


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
                        Load(
                            Environment.getExternalStorageDirectory().absolutePath + "/All Documents",
                            true
                        ).execute()
                        mTreeSteps++
                        popup.dismiss()
                    }
                    1 -> {
                        showFilter("pdf")

                        popup.dismiss()
                    }
                    2 -> {
                        showFilter("word")

                        popup.dismiss()
                    }
                    3 -> {
                        showFilter("txt")

                        popup.dismiss()

                    }
                    4 -> {
                        showFilter("excel")

                        popup.dismiss()
                    }
                    5 -> {
                        showFilter("ppt")

                        popup.dismiss()
                    }

                }

                edit!!.putInt("pos", position)
                edit!!.apply()
//                showListPopupWindow(it)
            }

        })
        popup.anchorView = anchor
        popup.width = (convertToPx(180, this))
        popup.height = (convertToPx(310, this))
        popup.setAdapter(adapter)
        return popup
    }

    private fun showFilter(filter: String) {

        val arr = ArrayList<Office>()
        when (filter) {
            "txt" -> {
                for (office in arrFilter) {
                    if (office.title.endsWith(".txt")) arr.add(office)
                }
            }
            "pdf" -> {
                for (office in arrFilter) {
                    if (office.title.endsWith(".pdf") || office.title.endsWith(".PDF")) arr.add(
                        office
                    )
                }
            }
            "word" -> {
                for (office in arrFilter) {
                    if (office.title.endsWith(".doc") || office.title.endsWith(".docx")
                        || office.title.endsWith(".DOC") || office.title.endsWith(".DOCX")
                    ) arr.add(office)
                }
            }
            "excel" -> {
                for (office in arrFilter) {
                    if (office.title.endsWith(".xls") || office.title.endsWith(".xlsx") || office.title.endsWith(
                            ".XLSX"
                        )
                    ) arr.add(office)
                }
            }
            "ppt" -> {
                for (office in arrFilter) {
                    if (office.title.endsWith(".ppt") || office.title.endsWith(".pptx") || office.title.endsWith(
                            ".PPTX"
                        )
                    ) arr.add(office)
                }
            }
        }

        showFilterRv(arr)
        ganFilPath()
    }


    private fun ganFilPath() {
        mTreeSteps++
        if (pathFile == Environment.getExternalStorageDirectory().absolutePath) {
            pathFile = Environment.getExternalStorageDirectory().absolutePath + "/All Documents"
        }

    }

    private fun showFilterRv(arr: List<Office>) {
        rv.layoutManager = LinearLayoutManager(this)
        fileAdapter = FilesAdapter(this, arr, object : FilesAdapter.Listener {
            override fun onClick(title: String, size: String, path: String, isFolder: Boolean) {
//                val intent = Intent(this@MainActivity,DislayOfficeActicity::class.java)
//                intent.putExtra("path",path)
//                startActivity(intent)

                Helper.openSimpleReaderActivity(this@MainActivity, path)
                if (dbOffice.checkPath(path)) {
                    dbOffice.updateHistory(path, System.currentTimeMillis())
                } else {
                    dbOffice.insertOffice(title, size, path, System.currentTimeMillis())
                }
                edit!!.putString("title", title)
                edit!!.apply()


            }

        })
        rv.adapter = fileAdapter
    }

    @SuppressLint("StaticFieldLeak")
    inner class Load(
        private var path: String, private var isAll: Boolean
    ) : AsyncTask<Void, Int, Void>() {

        override fun doInBackground(vararg void: Void): Void? {
            pathFile = path
            arrOffice.clear()

            if (!isAll) {
                if (File(path).exists()) {
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
                        val name = files[i].name
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
                }

            } else {


                arrOffice = Helper.getAllDocuments(this@MainActivity)
            }
            list = arrOffice.sortedWith(compareBy { it.title })
            for (i in list!!.indices) {
                if (list!![i].title == "All Documents") {
                    Collections.swap(list, i, 0)
                }
            }

            return null


        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: Void?) {
            //
            // Hide ProgressDialog here
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()

            }

            tv_title.text = pathFile
            showRv(list!!)

        }

        override fun onPreExecute() {
            progressDialog = ProgressDialog(this@MainActivity)
            progressDialog!!.setCancelable(true)
            progressDialog!!.isIndeterminate = false
            progressDialog!!.setMessage("Loading...")
            progressDialog!!.max = 100
            progressDialog!!.show()
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)

            progressDialog!!.progress = values[0]!!.inv()

        }


    }

    override fun onDestroy() {
        super.onDestroy()
        if ( progressDialog!=null && progressDialog!!.isShowing ){
            progressDialog!!.cancel()
        }
    }


    private fun hideKeybroad() {
//        ed_search.requestFocus()
//        ed_search.visibility = View.GONE
//        iv_search.visibility = View.VISIBLE
        val view: View = if (currentFocus == null) View(this) else currentFocus!!
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}




