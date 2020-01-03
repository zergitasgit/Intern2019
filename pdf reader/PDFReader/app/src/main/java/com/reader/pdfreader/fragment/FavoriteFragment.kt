package com.reader.pdfreader.fragment


import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.document.pdfviewer.db.DbPDF
import com.reader.pdfreader.R
import com.reader.pdfreader.`object`.PDF
import com.reader.pdfreader.activity.MainActivity
import com.reader.pdfreader.adapter.PDFAdapter
import com.reader.pdfreader.fragment.DislayPDFFragment.Companion.dbPdf
import com.reader.pdfreader.helper.Helper
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.fragment_favorite.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 *
 */
class FavoriteFragment : Fragment() {
    var arr = ArrayList<PDF>()
    var adapter: PDFAdapter? = null
    var arrFileSearch = ArrayList<PDF>()
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var showPo=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        val intent = IntentFilter()
        intent.addAction("SEARCH")
        intent.addAction("FAVORITED")
        intent.addAction("FAVORITER")
        intent.addAction("FAVORITEF")
        intent.addAction("NAME")
        intent.addAction("DATE")
        intent.addAction("SIZE")
        intent.addAction("POPUP")
        context!!.registerReceiver(broadcastReceiver, intent)

        sharedPreferences = context!!.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var dbPdf = DbPDF(context!!, null)
        arr = dbPdf.getFavorite()
        view.rv_pdf.layoutManager = LinearLayoutManager(context)

        sort()
    }

    private fun sort() {
        when (sharedPreferences!!.getString("sort", "")) {
            "name" -> recycleview(arr.sortedWith(compareBy { it.name }))
            "date" -> recycleview(arr.sortedWith(compareBy { it.date }))
            "size" -> recycleview(arr.sortedWith(compareBy { it.sort }))
            "" -> recycleview(arr)
        }
    }

    private fun recycleview(arr: List<PDF>) {

        adapter = PDFAdapter(context!!, arr, object : PDFAdapter.ItemListener {
            override fun onClick(
                path: String,
                favorite: Int,
                name: String,
                date: String,
                size: String
            ) {
                if (showPo) {
                    (activity as MainActivity).set()
                    showPo=false


                } else {
                    dbPdf!!.updateHistory(path, System.currentTimeMillis())

                    val intent = Intent("HISTORY")
                    context!!.sendBroadcast(intent)
                    Helper.openSimpleReaderActivity(context!!, path)
                }
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
                if (showPo) {
                    (activity as MainActivity).set()
                    showPo=false


                } else {
                    if (arr[position].favorite == 0) {
                        arr[position].favorite = 1
                        dbPdf!!.updateFavorite(path, 1)
                    } else {
                        arr[position].favorite = 0
                        dbPdf!!.updateFavorite(path, 0)
                    }
                    val intent = Intent("FAVORITEF")
                    context!!.sendBroadcast(intent)
                }
            }

        })
        rv_pdf.adapter = adapter
    }


    private var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action
            var string = p1?.extras?.getString("string")
//
            if (action!!.equals("SEARCH", ignoreCase = true)) {
//                Toast.makeText(context, "hi", Toast.LENGTH_SHORT).show()
                dislaySearch(string)
            } else if (action.equals("FAVORITED", ignoreCase = true) || action.equals(
                    "FAVORITER",
                    ignoreCase = true
                ) ||
                action.equals("FAVORITEF", ignoreCase = true)
            ) {
                arr = dbPdf!!.getFavorite()
              sort()
            } else if (action.equals("NAME", ignoreCase = true)) {
                val list = arr.sortedWith(compareBy { it.name })

                recycleview(list)
            } else if (action.equals("DATE", ignoreCase = true)) {
                val list = arr.sortedWith(compareBy { it.date })

                recycleview(list)
            } else if (action.equals("SIZE", ignoreCase = true)) {
                val list = arr.sortedWith(compareBy { it.sort })

                recycleview(list)
            }else if (action.equals("POPUP", ignoreCase = true)) {
                var show = p1!!.extras.getBoolean("show")

                showPo = show


            }
        }

    }


    @SuppressLint("DefaultLocale")
    private fun dislaySearch(string: String?) {
        arrFileSearch.clear()
        for (pdf in arr) {
            if (pdf.name.toLowerCase().contains(string!!.toLowerCase())) {
                arrFileSearch.add(pdf)
            }
        }
//        arrFile = arrFileSearch

        var adapter = PDFAdapter(context!!, arrFileSearch, object : PDFAdapter.ItemListener {
            override fun onClick(
                path: String,
                favorite: Int,
                name: String,
                date: String,
                size: String
            ) {
                if (showPo) {
                    (activity as MainActivity).set()
                    showPo=false


                } else {
                    dbPdf!!.updateHistory(path, System.currentTimeMillis())

                    val intent = Intent("HISTORY")
                    context!!.sendBroadcast(intent)
                    Helper.openSimpleReaderActivity(context!!, path)
                }
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
                if (showPo) {
                    (activity as MainActivity).set()
                    showPo=false


                } else {
                    if (arrFileSearch[position].favorite == 0) {
                        arrFileSearch[position].favorite = 1
                        dbPdf!!.updateFavorite(path, 1)
                    } else {
                        arrFileSearch[position].favorite = 0
                        dbPdf!!.updateFavorite(path, 0)
                    }
                    val intent = Intent("FAVORITEF")
                    context!!.sendBroadcast(intent)
                }

            }

        })
        rv_pdf.adapter = adapter


    }

    override fun onDestroy() {
        super.onDestroy()
        context!!.unregisterReceiver(broadcastReceiver)


    }


}
