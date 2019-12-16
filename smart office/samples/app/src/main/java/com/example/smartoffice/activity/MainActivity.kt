package com.example.smartoffice.activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
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
import com.example.smartoffice.adapter.ItemMainAdapter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var fileAdapter: FilesAdapter
    private var mTreeSteps = 0
    private var pathFile: String? = null
    private var fileAllDoc = ArrayList<File>()
    var isAllDoc = false
    var arrSearch  = ArrayList<Office>();
    var arrOffice = ArrayList<Office>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUp()
        handlePermission()


    }

    @SuppressLint("WrongConstant")
    private fun setUp() {
//
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
        val id = p0.itemId


        when (id) {
            R.id.nav_view -> Toast.makeText(this, "hi", Toast.LENGTH_LONG).show()
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchViewItem = menu!!.findItem(R.id.action_search)


        //getting the search view
        val searchView = searchViewItem.actionView as SearchView

        //making the searchview consume all the toolbar when open
        searchView.maxWidth = Int.MAX_VALUE
        searchView.queryHint = "Search"

//
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                arrSearch.clear()
                for (office in arrOffice) {
                    if (office.title.toLowerCase().contains(p0!!.toLowerCase())) {
                        arrSearch.add(office)
                    }
                }
                var list = arrSearch.sortedWith(compareBy { it.title })
                showRv(list)

                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }



    private fun showFiles(path: String, isAll: Boolean) {
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
                if (files[i].isFile && files[i].name.endsWith(".pdf")) {
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
            arrOffice = Helper.getAllDocuments(this)
        }
        var list = arrOffice.sortedWith(compareBy { it.title })
        showRv(list)

    }
    private fun showRv(arr:List<Office>){
        rv.layoutManager = LinearLayoutManager(this)
        fileAdapter = FilesAdapter(this, arr, object : FilesAdapter.Listener {
            override fun onClick(path: String, isFolder: Boolean, title: String) {
                if (isFolder) {
                    if (title == "All Documents") {
                        mTreeSteps++
                        isAllDoc = true
                        showFiles(path, true)
                    } else {
                        isAllDoc = false
                        mTreeSteps++
                        showFiles(path, false)
                    }
                } else {

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
            showFiles(Environment.getExternalStorageDirectory().absolutePath, false)

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
                    Helper.createFolder()
                    showFiles(Environment.getExternalStorageDirectory().absolutePath, false)


                }

                return
            }
            else -> return
        }
    }

    override fun onBackPressed() {
        if (mTreeSteps > 0) {
            mTreeSteps--

            showFiles(getPreviousPath()!!, false)

            return
        } else {
            showFiles(pathFile!!, false)
        }
        super.onBackPressed()
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
        listPopupItems.add(ItemMain("hieu", R.drawable.filte))
        listPopupItems.add(ItemMain("hiep", R.drawable.filte))



        val listPopupWindow = createListPopupWindow(anchor, listPopupItems)
        listPopupWindow.show()
    }


    private fun createListPopupWindow(anchor: View,
                                      items: ArrayList<ItemMain>): ListPopupWindow {
        val popup = ListPopupWindow(this)
        val adapter = ItemMainAdapter(this, items, object : ItemMainAdapter.ItemListener {
            override fun onClick(position: Int) {
                when (position) {
                    0 -> {

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
        popup.setAnchorView(anchor)
        popup.setWidth(convertToPx(150,this))
        popup.setHeight(convertToPx(185,this))
        popup.setAdapter(adapter)
        return popup
    }

}




