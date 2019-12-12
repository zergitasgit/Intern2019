package com.example.smartoffice.activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartoffice.R
import com.example.smartoffice.adapter.FilesAdapter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.io.FilenameFilter


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var fileAdapter: FilesAdapter
    private var mTreeSteps = 0
    private var pathFile :String?=null
    private var fileAllDoc = ArrayList<File>()


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

    private fun showFiles(path: String) {
        pathFile = path
        val files = getFiles(path)
        var filename: ArrayList<File> = ArrayList<File>()


        for (i in files!!.indices) {
            if (!files[i].isFile && !files[i].name.startsWith(".")) {

                    filename.add(files[i])

            }
            if (files[i].isFile && files[i].name.endsWith(".pdf")) {
                filename.add(files[i])
            }
        }
        var list = filename.sortedWith(compareBy { it.name })
        rv.layoutManager = LinearLayoutManager(this)
        fileAdapter = FilesAdapter(this, list, object : FilesAdapter.Listener {
            override fun onClick(file:File) {
                if (file.isDirectory) {
                    if(file.name == "All Documents"){
                        fileAllDoc.clear()
                        fileAllDoc = getAllDoc(File(Environment.getExternalStorageDirectory().absolutePath))
                        list = fileAllDoc.sortedWith(compareBy { it.name })
                        Toast.makeText(this@MainActivity,list.size.toString(),Toast.LENGTH_SHORT).show()
                        fileAdapter.notifyDataSetChanged()
                    }else {
                        mTreeSteps++
                        showFiles(file.absolutePath)
                    }
                } else {
                }
            }

        })
        rv.adapter = fileAdapter

    }

    fun getFiles(dir: String): List<File>? {
        return getFiles(dir, null)
    }

    fun getFiles(dir: String, matchRegex: String?): List<File>? {
        val file = File(dir)
        var files: Array<File?>? = null
        files = if (matchRegex != null) {
            val filter = FilenameFilter { dir, fileName -> fileName.matches(matchRegex.toRegex()) }
            file.listFiles(filter)
        } else {
            file.listFiles()
        }
        return if (files != null) listOf(*files) else null
    }

    fun getAllDoc(dir: File): ArrayList<File> {

        val listFile = dir.listFiles()
        if (listFile != null) {
            for (i in listFile.indices) {
                if (listFile[i].isDirectory) { // if its a directory need to get the files under that directory
                    getAllDoc(listFile[i])
                } else { // add path of  files to your arraylist for later use
                    if (listFile[i].name.endsWith(".pdf")) { //Do what ever u want
                        fileAllDoc.add(listFile[i])
                    }
                }
            }
        }
        return fileAllDoc
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun handlePermission() {
        val perms = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(perms, 3)
        } else {
            creatFolder()
            showFiles(Environment.getExternalStorageDirectory().absolutePath)

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
                    creatFolder()
                    showFiles(Environment.getExternalStorageDirectory().absolutePath)


                }

                return
            }
            else -> return
        }
    }

    override fun onBackPressed() {
        if (mTreeSteps > 0) {
            mTreeSteps--
            showFiles(getPreviousPath()!!)
            return
        }else{
            showFiles(pathFile!!)
        }
        super.onBackPressed()
    }

    private fun getPreviousPath(): String? {
        val path: String =pathFile!!
        val lastIndexOf = pathFile!!.lastIndexOf(File.separator)
        if (lastIndexOf < 0) {

            return pathFile
        }
        return path.substring(0, lastIndexOf)
    }

    private fun getSize(file: File): String {
        var size = file.length() // Get size and convert bytes into Kb.
        var suffix = ""
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }
        return size.toString() + suffix
    }
    private fun creatFolder(){
        val folder =  File(Environment.getExternalStorageDirectory().absolutePath +File.separator + "All Documents");

        if(!folder.exists()){
            folder.mkdir();
        }

    }
}




