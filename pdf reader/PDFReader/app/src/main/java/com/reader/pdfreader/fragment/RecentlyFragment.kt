package com.reader.pdfreader.fragment


import android.content.*
import android.os.Bundle

import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.document.pdfviewer.`object`.ItemMain
import com.document.pdfviewer.`object`.PDF

import com.document.pdfviewer.db.DbPDF

import com.reader.pdfreader.R
import com.reader.pdfreader.adapter.PDFAdapter
import com.reader.pdfreader.fragment.DislayPDFFragment.Companion.dbPdf
import kotlinx.android.synthetic.main.fragment_recently.*
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 *
 */
class RecentlyFragment : Fragment() {
    var arr = ArrayList<PDF>()
    var adapter: PDFAdapter? = null
    var arrFileSearch = ArrayList<PDF>()
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_recently, container, false)
        context!!.registerReceiver(brHistory, IntentFilter("HISTORY"))
        context!!.registerReceiver(brSearch, IntentFilter("SEARCH"))
        context!!.registerReceiver(brFav, IntentFilter("FAVORITE"))
        sharedPreferences = context!!.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()

        var dbPdf = DbPDF(context!!, null)
        arr = dbPdf.getPDFRecently()


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_pdf.layoutManager = LinearLayoutManager(context)
        recycleview()

    }

    var brHistory = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action
            if (action!!.equals("HISTORY", ignoreCase = true)) {
//                arr.clear()
                arr = dbPdf!!.getPDFRecently()
//                Toast.makeText(context,"hi",Toast.LENGTH_SHORT).show()
                recycleview()

            }
        }

    }
    var brSearch = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action
            var string = p1?.extras?.getString("string")


            if (action!!.equals("SEARCH", ignoreCase = true)) {
                Toast.makeText(context, "hi", Toast.LENGTH_SHORT).show()
                dislaySearch(string)
            }
        }

    }
    private var brFav = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action

            if (action!!.equals("FAVORITE", ignoreCase = true)) {
                arr = dbPdf!!.getPDFRecently()
                recycleview()
            }
        }

    }


    private fun dislaySearch(string: String?) {
        arrFileSearch.clear()
        for (pdf in arr) {
            if (pdf.name.toLowerCase().contains(string!!.toLowerCase())) {
                arrFileSearch.add(pdf)
            }
        }
//        arrFile = arrFileSearch

        var adapter = PDFAdapter(context!!, arrFileSearch, object : PDFAdapter.ItemListener {
            override fun onClick(path: String, favorite: Int, name: String, date: String, size: String) {
            }

        }, object : PDFAdapter.MenuItemListener {
            override fun onClick(position: Int, favorite: Int, name: String, path: String, date: String, size: String) {

            }

        })
        rv_pdf.adapter = adapter


    }

    private fun recycleview() {
        adapter = PDFAdapter(context!!, arr, object : PDFAdapter.ItemListener {
            override fun onClick(path: String, favorite: Int, name: String, date: String, size: String) {

                dbPdf!!.updateHistory(path, System.currentTimeMillis())

                val intent = Intent("HISTORY")
                context!!.sendBroadcast(intent)
            }

        }, object : PDFAdapter.MenuItemListener {
            override fun onClick(position: Int, favorite: Int, name: String, path: String, date: String, size: String) {
                if (arr[position].favorite == 0) {
                    arr[position].favorite = 1
                    dbPdf!!.updateFavorite(path, 1)
                } else {
                    arr[position].favorite = 0
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
            arr = dbPdf!!.getPDFRecently()
//                Toast.makeText(context,"hi",Toast.LENGTH_SHORT).show()
            recycleview()
        }
    }




    override fun onDestroy() {
        super.onDestroy()
        context!!.unregisterReceiver(brHistory)
        context!!.unregisterReceiver(brSearch)
        context!!.unregisterReceiver(brFav)

    }




}
