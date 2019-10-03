package hieusenpaj.com.pdf.fragment


import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.*
import hieusenpaj.com.pdf.R
import hieusenpaj.com.pdf.`object`.PDF
import hieusenpaj.com.pdf.activity.PdfActivity
import hieusenpaj.com.pdf.adapter.PDFAdapter
import hieusenpaj.com.pdf.db.DbPDF
import kotlinx.android.synthetic.main.fragment_dislay_pdf.*
import kotlinx.android.synthetic.main.fragment_dislay_pdf.view.*
import java.io.File
import java.text.SimpleDateFormat
import android.support.v4.content.FileProvider
import hieusenpaj.com.pdf.dialog.DeleteDialog
import hieusenpaj.com.pdf.dialog.DetailDialog
import hieusenpaj.com.pdf.dialog.RenameDialog
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.view.menu.MenuPopupHelper
import android.support.v7.widget.ListPopupWindow
import hieusenpaj.com.pdf.`object`.ItemMain
import hieusenpaj.com.pdf.adapter.ItemMainAdapter
import hieusenpaj.com.pdf.activity.MainActivity


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 *
 */
class DislayPDFFragment : Fragment() {
    var arrFile = ArrayList<PDF>()
    var arrFileSearch = ArrayList<PDF>()
    var adapter: PDFAdapter? = null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    companion object {
        var dbPdf: DbPDF? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_dislay_pdf, container, false)
        context!!.registerReceiver(broadcastReceiver, IntentFilter("SEARCH"))
        context!!.registerReceiver(brPop, IntentFilter("POPMENU"))

        sharedPreferences = context!!.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()

        dbPdf = DbPDF(context!!, null)
        arrFile.clear()
        getFile(Environment.getExternalStorageDirectory().absoluteFile)
        if (dbPdf!!.getPdf().size != arrFile.size) {
            dbPdf?.delete()
            for (pdf in arrFile)
                dbPdf!!.insertSong(pdf.name, pdf.date, pdf.size, pdf.path, pdf.history, pdf.favorite)
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
            if (string!!.isEmpty() || string.length == 0) {
                recycleView()
            }

            if (action!!.equals("SEARCH", ignoreCase = true)) {
                dislaySearch(string)
            }
        }

    }
    private var brPop = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action

            if (action!!.equals("POPMENU", ignoreCase = true)) {


                arrFile = dbPdf!!.getPdf()
                recycleView()
            }
        }

    }

    private fun dislaySearch(string: String?) {
        arrFileSearch.clear()
        for (pdf in arrFile) {
            if (pdf.name.toLowerCase().contains(string!!.toLowerCase())) {
                arrFileSearch.add(pdf)
            }
        }
//        arrFile = arrFileSearch

        val adapter = PDFAdapter(context!!, arrFileSearch, object : PDFAdapter.ItemListener {
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

    private fun recycleView() {
        var adapter = PDFAdapter(context!!, arrFile, object : PDFAdapter.ItemListener {
            override fun onClick(path: String, favorite: Int, name: String, date: String, size: String) {
                startIntent(path, favorite, name, date, size)
//                Toast.makeText(context!!,renameFile(File(path),""),Toast.LENGTH_SHORT).show()
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
            arrFile = dbPdf!!.getPdf()
            recycleView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        context!!.unregisterReceiver(broadcastReceiver)
        context!!.unregisterReceiver(brPop)


//
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
                        val pdf = PDF(listFile[i].name, getModifile(listFile[i]), getSize(listFile[i]), listFile[i].absolutePath, 0, 0)
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
                            override fun onClick(name: String, pathNew: String) {
                                dbPdf!!.updateName(path, name)
                                dbPdf!!.updatePath(pathNew, path)
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
                                arrFile = dbPdf!!.getPdf()
                                recycleView()
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
        popup.setHeight(convertToPx(185))
        popup.setAdapter(adapter)
        return popup
    }

    private fun share(file: File) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("application/pdf")
        val fileUri = FileProvider.getUriForFile(context!!, "com.myfileprovider", file)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
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
