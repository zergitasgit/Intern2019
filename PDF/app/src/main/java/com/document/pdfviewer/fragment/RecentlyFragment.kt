package com.document.pdfviewer.fragment


import android.content.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.ListPopupWindow
import android.view.*

import com.document.pdfviewer.R
import com.document.pdfviewer.`object`.ItemMain
import com.document.pdfviewer.`object`.PDF
import com.document.pdfviewer.activity.PdfActivity
import com.document.pdfviewer.adapter.ItemMainAdapter
import com.document.pdfviewer.adapter.PDFAdapter
import com.document.pdfviewer.db.DbPDF
import com.document.pdfviewer.dialog.DeleteDialog
import com.document.pdfviewer.dialog.DetailDialog
import com.document.pdfviewer.dialog.RenameDialog
import com.document.pdfviewer.fragment.DislayPDFFragment.Companion.dbPdf
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
        context!!.registerReceiver(brPop, IntentFilter("POPMENU"))
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
    private var brPop = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action

            if (action!!.equals("POPMENU", ignoreCase = true)) {

//                dbPdf!!.updateName(p1?.extras?.getString("path").toString(), p1?.extras?.getString("name").toString())
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
                startIntent(path, favorite, name, date, size)
            }

        }, object : PDFAdapter.MenuItemListener {
            override fun onClick(position: Int, it: View, name: String, path: String, date: String, size: String) {
                showListPopupWindow(it, name, path, date, size)

            }

        })
        rv_pdf.adapter = adapter


    }

    private fun recycleview() {
        adapter = PDFAdapter(context!!, arr, object : PDFAdapter.ItemListener {
            override fun onClick(path: String, favorite: Int, name: String, date: String, size: String) {
                startIntent(path, favorite, name, date, size)
            }

        }, object : PDFAdapter.MenuItemListener {
            override fun onClick(position: Int, it: View, name: String, path: String, date: String, size: String) {
                showListPopupWindow(it, name, path, date, size)

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


    private fun startIntent(path: String, favorite: Int, name: String, date: String, size: String) {
        val intent = Intent(context, PdfActivity::class.java)
        intent.putExtra("path", path)
        intent.putExtra("favorite", favorite)
        intent.putExtra("name", name)
        intent.putExtra("date", date)
        intent.putExtra("size", size)
        startActivity(intent)
        dbPdf!!.updateHistory(path, System.currentTimeMillis())

        val intentBr = Intent("HISTORY")
        context!!.sendBroadcast(intentBr)
    }

    override fun onDestroy() {
        super.onDestroy()
        context!!.unregisterReceiver(brHistory)
        context!!.unregisterReceiver(brSearch)
        context!!.unregisterReceiver(brPop)

    }


    private fun showListPopupWindow(anchor: View, name: String, path: String, date: String, size: String) {
        val listPopupItems = ArrayList<ItemMain>()
        listPopupItems.add(ItemMain(resources.getString(R.string.rename), R.drawable.ic_edit))
        listPopupItems.add(ItemMain(resources.getString(R.string.share), R.drawable.ic_share))
        listPopupItems.add(ItemMain(resources.getString(R.string.delete), R.drawable.ic_delete))
        listPopupItems.add(ItemMain(resources.getString(R.string.detail), R.drawable.ic_detail))


        val listPopupWindow = createListPopupWindow(anchor, name, path, date, size, listPopupItems)
        listPopupWindow.show()
    }


    private fun createListPopupWindow(anchor: View, name: String, path: String, date: String, size: String,
                                      items: ArrayList<ItemMain>): ListPopupWindow {
        val popup = ListPopupWindow(context!!)
        val adapter = ItemMainAdapter(context!!, items, object : ItemMainAdapter.ItemListener {
            override fun onClick(position: Int) {
                when (position) {
                    0 -> {
                        val dialogRename = RenameDialog(context!!, name, path, object : RenameDialog.OnClickDialog {
                            override fun onClick(name: String,pathNew: String) {
                                dbPdf!!.updateName(path, name)
                                dbPdf!!.updatePath(pathNew,path)
                            }

                        })
                        dialogRename.show()
                        popup.dismiss()
                    }
                    1 -> {
                        share(File(path))
                        popup.dismiss()
                    }
                    2 -> {
                        val deleteDialog = DeleteDialog(context!!, path, object : DeleteDialog.OnClickDialog {
                            override fun onClick(path: String) {
                                dbPdf!!.deleteFile(path)
                                arr = dbPdf!!.getPDFRecently()
                                recycleview()
                            }
                        })
                        deleteDialog.show()
                        popup.dismiss()
                    }
                    3 -> {
                        val detailDialog = DetailDialog(context!!, name, path, date, size)
                        detailDialog.show()
                        popup.dismiss()
                    }
                }
//                showListPopupWindow(it)
            }

        })
        popup.setAnchorView(anchor)
        popup.setWidth(convertToPx(150))
        popup.setHeight(convertToPx(175))
        popup.setAdapter(adapter)
        return popup
    }
    private fun share(file: File) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("application/pdf")
        var fileUri = FileProvider.getUriForFile(context!!, "com.myfileprovider", file)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        context!!.startActivity(Intent.createChooser(shareIntent, "Share"))
    }
    private fun convertToPx(dp: Int): Int {
        // Get the screen's density scale
        val scale = resources.displayMetrics.density
        // Convert the dps to pixels, based on density scale
        return (dp * scale + 0.5f).toInt()
    }
}
