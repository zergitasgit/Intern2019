package com.reader.pdfreader.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.reader.pdfreader.R
import com.reader.pdfreader.`object`.Office
import com.reader.pdfreader.adapter.FilesAdapter
import com.reader.pdfreader.fragment.DislayPDFFragment
import com.reader.pdfreader.helper.Helper
import com.reader.pdfreader.helper.Helper.Companion.getModifile
import com.reader.pdfreader.helper.Helper.Companion.getPreviousPath
import kotlinx.android.synthetic.main.activity_browse_files.*
import java.io.File
import java.util.*

class BrowseFilesActivity : AppCompatActivity() {
    var arrOffice = ArrayList<Office>()
    var list: List<Office>? = null
    private var progressDialog: ProgressDialog? = null
    private var mTreeSteps = 0
    private var pathFile: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_files)
        Load(Environment.getExternalStorageDirectory().absolutePath).execute()
        iv_back.setOnClickListener { onBackPressed() }
    }

    @SuppressLint("StaticFieldLeak")
    inner class Load(
        private var path: String
    ) : AsyncTask<Void, Int, Void>() {

        override fun doInBackground(vararg void: Void): Void? {
            pathFile = path
            arrOffice.clear()
                if (File(path).exists()) {
                    val files = Helper.getFiles(path)
                    for (i in files!!.indices) {
                        if (!files[i].isFile && !files[i].name.startsWith(".")) {
                            arrOffice.add(
                                Office(
                                    files[i].name,
                                    Helper.getSize(files[i]),
                                    files[i].absolutePath, getModifile(files[i]),
                                    true
                                )
                            )

                        }
                        val name = files[i].name
                        if (!files[i].name.startsWith(".") && files[i].isFile && name.endsWith(".pdf") || name.endsWith(".PDF")) {
                            arrOffice.add(
                                Office(
                                    files[i].name,
                                    Helper.getSize(files[i]),
                                    files[i].absolutePath,getModifile(files[i]),
                                    false
                                )
                            )
                        }
                    }
                }


            list = arrOffice.sortedWith(compareBy { it.title })



            return null


        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: Void?) {
            //
            // Hide ProgressDialog here
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()

            }

            tv.text = pathFile
            showRv(list!!)

        }

        override fun onPreExecute() {
            progressDialog = ProgressDialog(this@BrowseFilesActivity)
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

    private fun showRv(arr: List<Office>) {
        rv.layoutManager = LinearLayoutManager(this)
        val fileAdapter = FilesAdapter(this, arr, object : FilesAdapter.Listener {
            override fun onClick(title: String, size: String, path: String, isFolder: Boolean) {
                if (isFolder) {
                    mTreeSteps++
                    Load(path).execute()

                } else {
//
                    DislayPDFFragment.dbPdf!!.updateHistory(path, System.currentTimeMillis())

                    val intent = Intent("HISTORY")
                    sendBroadcast(intent)
                    Helper.openSimpleReaderActivity(this@BrowseFilesActivity,path)
                }
            }

        })
        rv.adapter = fileAdapter

    }
    override fun onBackPressed() {
        if (mTreeSteps > 0) {
            val path: String =getPreviousPath(pathFile!!)
            mTreeSteps--

            Load(path).execute()
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.cancel()
        }
    }
}
