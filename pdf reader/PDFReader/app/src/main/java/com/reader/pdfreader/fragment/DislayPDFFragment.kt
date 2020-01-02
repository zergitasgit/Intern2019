package com.reader.pdfreader.fragment


import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.*
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.document.pdfviewer.db.DbPDF
import com.reader.pdfreader.R
import com.reader.pdfreader.`object`.PDF
import com.reader.pdfreader.adapter.PDFAdapter
import com.reader.pdfreader.helper.Helper
import com.reader.pdfreader.helper.Helper.Companion.getModifile
import com.reader.pdfreader.helper.Helper.Companion.getSize
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
    private var progressDialog: ProgressDialog? = null
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
        val intent = IntentFilter()
        intent.addAction("SEARCH")
        intent.addAction("FAVORITEF")
        intent.addAction("FAVORITER")
        intent.addAction("NAME")
        intent.addAction("DATE")
        intent.addAction("SIZE")
        context!!.registerReceiver(broadcastReceiver, intent)
        sharedPreferences = context!!.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()




        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Load().execute()

    }
    inner class Load() : AsyncTask<Void,Int,Void>(){
        override fun doInBackground(vararg p0: Void): Void? {
            dbPdf = DbPDF(context!!, null)
            arrFile.clear()
            getFile(Environment.getExternalStorageDirectory().absoluteFile)
            if (dbPdf!!.getPdf().size == 0) {
                for (pdf in arrFile)
                    dbPdf!!.insertSong(
                        pdf.name,
                        pdf.date,
                        pdf.size,
                        pdf.path,
                        pdf.history,
                        pdf.sort,
                        pdf.favorite
                    )
            }
            if(arrFile.size> dbPdf!!.getPdf().size) {
                for (pdf in arrFile) {
                    if (!dbPdf!!.checkPath(pdf.path)) {
                        dbPdf!!.insertSong(
                            pdf.name,
                            pdf.date,
                            pdf.size,
                            pdf.path,
                            pdf.history,
                            pdf.sort,
                            pdf.favorite
                        )
                    }
                }
            }


            if (dbPdf!!.getPdf().size > 0) {
                arrFile = dbPdf!!.getPdf()


            }
            return null

        }

        override fun onPostExecute(result: Void?) {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()

            }
            rv_pdf.layoutManager = LinearLayoutManager(context)
            sort()
        }

        override fun onPreExecute() {
            progressDialog = ProgressDialog(context)
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

    private fun sort() {
        when (sharedPreferences!!.getString("sort", "")) {
            "name" -> recycleView(arrFile.sortedWith(compareBy { it.name }))
            "date" -> recycleView(arrFile.sortedWith(compareBy { it.date }))
            "size" -> recycleView(arrFile.sortedWith(compareBy { it.sort }))
            ""  -> recycleView(arrFile)
        }
    }

    private var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action
            val string = p1?.extras?.getString("string")

//                recycleView()

            if (action!!.equals("SEARCH", ignoreCase = true)) {
                dislaySearch(string)
//                Toast.makeText(context,string,Toast.LENGTH_SHORT).show()
            } else if (action.equals("FAVORITER", ignoreCase = true) || action.equals(
                    "FAVORITEF",
                    ignoreCase = true
                )
            ) {
                arrFile = dbPdf!!.getPdf()
                sort()
            } else if (action.equals("NAME", ignoreCase = true)) {

                val list = arrFile.sortedWith(compareBy { it.name })


                recycleView(list)
            } else if (action.equals("DATE", ignoreCase = true)) {

                val list = arrFile.sortedWith(compareBy { it.date })


                recycleView(list)
            } else if (action.equals("SIZE", ignoreCase = true)) {

                val list = arrFile.sortedWith(compareBy { it.sort })


                recycleView(list)
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
                dbPdf!!.updateHistory(path, System.currentTimeMillis())

                val intent = Intent("HISTORY")
                context!!.sendBroadcast(intent)
                Helper.openSimpleReaderActivity(context!!,path)
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
                if (arrFileSearch[position].favorite == 0) {
                    arrFileSearch[position].favorite = 1
                    dbPdf!!.updateFavorite(path, 1)
                } else {
                    arrFileSearch[position].favorite = 0
                    dbPdf!!.updateFavorite(path, 0)
                }
                val intent = Intent("FAVORITED")
                context!!.sendBroadcast(intent)

            }

        })
        rv_pdf.adapter = adapter

    }

    private fun recycleView(list: List<PDF>) {
        val adapter = PDFAdapter(context!!, list, object : PDFAdapter.ItemListener {
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
                Helper.openSimpleReaderActivity(context!!,path)
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
                if (list[position].favorite == 0) {
                    list[position].favorite = 1
                    dbPdf!!.updateFavorite(path, 1)
                } else {
                    list[position].favorite = 0
                    dbPdf!!.updateFavorite(path, 0)
                }
                val intent = Intent("FAVORITED")
                context!!.sendBroadcast(intent)



            }

        })
        rv_pdf.adapter = adapter
    }


//    override fun onResume() {
//        super.onResume()
//        if (sharedPreferences!!.getBoolean("clickFavorite", false)) {
//            arrFile = dbPdf!!.getPdf()
//            recycleView(arrFile)
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        context!!.unregisterReceiver(broadcastReceiver)

    }


    private fun getFile(dir: File): ArrayList<PDF> {
//
        val listFile = dir.listFiles()

        if (listFile != null) {
            for (i in listFile.indices) {

                if (listFile[i].isDirectory) {// if its a directory need to get the files under that directory
                    getFile(listFile[i])
                } else {// add path of  files to your arraylist for later use
                    if (listFile[i].name.endsWith(".pdf") && !listFile[i].name.startsWith(".")) {
                        //Do what ever u want
                        val pdf = PDF(
                            listFile[i].name,
                            getModifile(listFile[i]),
                            getSize(listFile[i]),
                            listFile[i].absolutePath,
                            0,
                            listFile[i].length(),0
                        )
                        arrFile.add(pdf)
                    }

                }
            }
        }
        return arrFile
    }




}
