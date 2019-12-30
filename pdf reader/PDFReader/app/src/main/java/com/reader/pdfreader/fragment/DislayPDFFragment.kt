package com.reader.pdfreader.fragment


import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.document.pdfviewer.`object`.PDF
import com.document.pdfviewer.db.DbPDF
import com.reader.pdfreader.R
import com.reader.pdfreader.adapter.PDFAdapter
import kotlinx.android.synthetic.main.fragment_dislay_pdf.*
import kotlinx.android.synthetic.main.fragment_dislay_pdf.view.*
import java.io.File
import java.text.SimpleDateFormat


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 *
 */
class DislayPDFFragment : Fragment() {
    var arrFile = ArrayList<PDF>()
    var arrFileSearch = ArrayList<PDF>()
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    companion object {
        var dbPdf: DbPDF? = null
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_dislay_pdf, container, false)
        context!!.registerReceiver(broadcastReceiver, IntentFilter("SEARCH"))
        context!!.registerReceiver(brFav, IntentFilter("FAVORITE"))
        context!!.registerReceiver(brName, IntentFilter("NAME"))
        sharedPreferences = context!!.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()

        dbPdf = DbPDF(context!!, null)

        arrFile.clear()
        getFile(Environment.getExternalStorageDirectory().absoluteFile)
        if (dbPdf!!.getPdf().size != arrFile.size) {
            for (pdf in arrFile)
                dbPdf!!.insertSong(
                    pdf.name,
                    pdf.date,
                    pdf.size,
                    pdf.path,
                    pdf.history,
                    pdf.favorite
                )
        }


        if (dbPdf!!.getPdf().size > 0) {
            arrFile = dbPdf!!.getPdf()
        }
        v.rv_pdf.layoutManager = LinearLayoutManager(context)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleView()
    }

    private var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action
            val string = p1?.extras?.getString("string")

//                recycleView()


            if (action!!.equals("SEARCH", ignoreCase = true)) {
                dislaySearch(string)
//                Toast.makeText(context,string,Toast.LENGTH_SHORT).show()
            }
        }

    }
    private var brFav = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action

            if (action!!.equals("FAVORITE", ignoreCase = true)) {
                arrFile = dbPdf!!.getPdf()
                recycleView()
            }
        }

    }
    private var brName = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action

            if (action!!.equals("NAME", ignoreCase = true)) {

                val list = arrFile.sortWith(compareBy{ it.name})
                arrFile.clear()
                arrFile.addAll(list)
                recycleView()
            }
        }

    }

    @SuppressLint("DefaultLocale")
    private fun dislaySearch(string: String?) {
        arrFileSearch.clear()
        for (pdf in arrFile) {
            if (pdf.name.toLowerCase().contains(string!!.toLowerCase())) {
                arrFileSearch.add(pdf)
            }
        }
//        arrFile = arrFileSearch

        val adapter = PDFAdapter(context!!, arrFileSearch, object : PDFAdapter.ItemListener {
            override fun onClick(
                path: String,
                favorite: Int,
                name: String,
                date: String,
                size: String
            ) {

            }

        }, object : PDFAdapter.MenuItemListener {
            override fun onClick(
                position: Int,
                favorite: Int,
                name: String,
                path: String,
                date: String,
                size: String
            ) {

            }

        })
        rv_pdf.adapter = adapter

    }

    private fun recycleView() {
        val adapter = PDFAdapter(context!!, arrFile, object : PDFAdapter.ItemListener {
            override fun onClick(
                path: String,
                favorite: Int,
                name: String,
                date: String,
                size: String
            ) {

                dbPdf!!.updateHistory(path, System.currentTimeMillis())

                val intent = Intent("HISTORY")
                context!!.sendBroadcast(intent)
            }

        }, object : PDFAdapter.MenuItemListener {
            override fun onClick(
                position: Int,
                favorite: Int,
                name: String,
                path: String,
                date: String,
                size: String
            ) {
                if (arrFile[position].favorite == 0) {
                    arrFile[position].favorite = 1
                    dbPdf!!.updateFavorite(path, 1)
                } else {
                    arrFile[position].favorite = 0
                    dbPdf!!.updateFavorite(path, 0)
                }
                val intent = Intent("FAVORITE")
                context!!.sendBroadcast(intent)


            }

        })
        rv_pdf.adapter = adapter
    }


    override fun onResume() {
        super.onResume()
        if (sharedPreferences!!.getBoolean("clickFavorite", false)) {
            arrFile = dbPdf!!.getPdf()
            recycleView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        context!!.unregisterReceiver(broadcastReceiver)
        context!!.unregisterReceiver(brFav)
    }

    override fun onStop() {
        super.onStop()
//
    }


    private fun getFile(dir: File): ArrayList<PDF> {
//
        val listFile = dir.listFiles()

        if (listFile != null) {
            for (i in listFile.indices) {

                if (listFile[i].isDirectory) {// if its a directory need to get the files under that directory
                    getFile(listFile[i])
                } else {// add path of  files to your arraylist for later use
                    if (listFile[i].name.endsWith(".pdf")) {
                        //Do what ever u want
                        val pdf = PDF(
                            listFile[i].name,
                            getModifile(listFile[i]),
                            getSize(listFile[i]),
                            listFile[i].absolutePath,
                            0,
                            0
                        )
                        arrFile.add(pdf)
                    }

                }
            }
        }
        return arrFile
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

    private fun getModifile(file: File): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        return sdf.format(file.lastModified())
    }




}
