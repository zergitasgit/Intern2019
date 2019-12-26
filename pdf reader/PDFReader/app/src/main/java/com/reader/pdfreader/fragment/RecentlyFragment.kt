package com.document.pdfviewer.fragment


import android.content.*
import android.os.Bundle

import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.document.pdfviewer.`object`.ItemMain
import com.document.pdfviewer.`object`.PDF

import com.document.pdfviewer.db.DbPDF

import com.document.pdfviewer.fragment.DislayPDFFragment.Companion.dbPdf
import com.reader.pdfreader.R
import com.reader.pdfreader.adapter.PDFAdapter
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
            if (string!!.isEmpty() || string.length == 0) {
                recycleview()
            }

            if (action!!.equals("SEARCH", ignoreCase = true)) {
//                Toast.makeText(context, "hi", Toast.LENGTH_SHORT).show()
                dislaySearch(string)
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
                checkFa(path,favorite)
            }

        })
        rv_pdf.adapter = adapter


    }

    private fun recycleview() {
        adapter = PDFAdapter(context!!, arr, object : PDFAdapter.ItemListener {
            override fun onClick(path: String, favorite: Int, name: String, date: String, size: String) {
            }

        }, object : PDFAdapter.MenuItemListener {
            override fun onClick(position: Int, favorite: Int, name: String, path: String, date: String, size: String) {
                checkFa(path,favorite)
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

    private fun checkFa(path:String,check:Int){
        if (check==0){
            dbPdf!!.updateFavorite(path,1)
        }else{
            dbPdf!!.updateFavorite(path,0)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        context!!.unregisterReceiver(brHistory)
        context!!.unregisterReceiver(brSearch)

    }




}
