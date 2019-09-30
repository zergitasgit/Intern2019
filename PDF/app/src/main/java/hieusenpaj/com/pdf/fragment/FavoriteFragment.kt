package hieusenpaj.com.pdf.fragment


import android.content.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import hieusenpaj.com.pdf.R
import hieusenpaj.com.pdf.`object`.PDF
import hieusenpaj.com.pdf.activity.PdfActivity
import hieusenpaj.com.pdf.adapter.PDFAdapter
import hieusenpaj.com.pdf.db.DbPDF
import hieusenpaj.com.pdf.dialog.DeleteDialog
import hieusenpaj.com.pdf.dialog.DetailDialog
import hieusenpaj.com.pdf.dialog.RenameDialog
import hieusenpaj.com.pdf.fragment.DislayPDFFragment.Companion.dbPdf
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.fragment_favorite.view.*
import java.io.File

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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_favorite, container, false)
        context!!.registerReceiver(brSearch, IntentFilter("SEARCH"))
        context!!.registerReceiver(brPop, IntentFilter("POPMENU"))
        sharedPreferences = context!!.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var dbPdf = DbPDF(context!!, null)
        arr = dbPdf.getFavorite()
        view.rv_pdf.layoutManager = LinearLayoutManager(context)
        recycleview()
    }

    private fun recycleview() {

        adapter = PDFAdapter(context!!, arr, object : PDFAdapter.ItemListener {
            override fun onClick(path: String, favorite: Int, name: String, date: String, size: String) {
                startIntent(path, favorite, name, date, size)
            }

        }, object : PDFAdapter.MenuItemListener {
            override fun onClick(position: Int, it: View, name: String, path: String, date: String, size: String) {
                showPopup(it, name, path, date, size)

            }

        })
        rv_pdf.adapter = adapter
    }


    var brSearch = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action
            var string = p1?.extras?.getString("string")
            if (string!!.isEmpty() || string!!.length == 0) {
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
                arr = dbPdf!!.getFavorite()
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
                showPopup(it, name, path, date, size)

            }

        })
        rv_pdf.adapter = adapter


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

    override fun onResume() {
        super.onResume()
        if (sharedPreferences!!.getBoolean("clickFavorite", false)) {
            arr = dbPdf!!.getFavorite()
//                Toast.makeText(context,"hi",Toast.LENGTH_SHORT).show()
            recycleview()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        context!!.unregisterReceiver(brSearch)
        context!!.unregisterReceiver(brPop)

    }

    private fun showPopup(view: View, name: String, path: String, date: String, size: String) {
        var popup: PopupMenu? = null;
        popup = PopupMenu(context!!, view)
        popup.inflate(R.menu.menu_pop_pdf)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.rename -> {
//
                    val dialogRename = RenameDialog(context!!, name, path, object : RenameDialog.OnClickDialog {
                        override fun onClick(name: String,pathNew:String) {
                            dbPdf!!.updateName(path, name)
                            dbPdf!!.updatePath(pathNew,path)
                        }

                    })
                    dialogRename.show()

                }
                R.id.share -> {
                    share(File(path))
                }
                R.id.delete -> {
//
                    val deleteDialog = DeleteDialog(context!!, path, object : DeleteDialog.OnClickDialog {
                        override fun onClick(path: String) {
                            dbPdf!!.deleteFile(path)
                            arr = dbPdf!!.getFavorite()
                            recycleview()
                        }
                    })
                    deleteDialog.show()

                }
                R.id.detail -> {
                    val detailDialog = DetailDialog(context!!, name, path, date, size)
                    detailDialog.show()

                }
            }

            true
        })

        popup.show()
    }

    private fun share(file: File) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("application/pdf")
        var fileUri = FileProvider.getUriForFile(context!!, "com.myfileprovider", file)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        context!!.startActivity(Intent.createChooser(shareIntent, "Share"))
    }
}
