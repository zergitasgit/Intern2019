package com.document.pdfviewer.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.view.WindowManager
import android.widget.LinearLayout
import com.document.pdfviewer.R
import kotlinx.android.synthetic.main.dialog_rename.*
import java.io.File

class RenameDialog(context: Context,private var name:String,
                   private var path: String,private val listener: OnClickDialog) :Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setBackgroundDrawable(ColorDrawable(0))
        setContentView(R.layout.dialog_rename)
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val dir = File(path).parentFile
        val pos = name.lastIndexOf(".")
        if (pos > 0) {
            var rename = name.substring(0, pos)
            ed_rename.text = Editable.Factory.getInstance().newEditable(rename)
            ed_rename.setSelectAllOnFocus(true)
            ed_rename.requestFocus();
            window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            tv_ok.setOnClickListener {
                if (ed_rename.text.toString()!=rename) {
                    renameFile(File(path), ed_rename.text.toString() + ".pdf")
                    dismiss()
                    listener.onClick(ed_rename.text.toString() + ".pdf",dir.toString()+ "/"+ ed_rename.text.toString() + ".pdf")
//                    Toast.makeText(context,"rename",Toast.LENGTH_SHORT).show()
                    val intentBr = Intent("POPMENU")
                    intentBr.putExtra("name",name)
                    intentBr.putExtra("path",path)
                    context.sendBroadcast(intentBr)
                } else {
                    dismiss()
                }
            }
        }

        tv_cancel.setOnClickListener {
            dismiss()
        }
    }


    private fun renameFile(file: File, suffix: String) {
        val ext = file.absolutePath
        val dir = file.parentFile

        if (dir.exists()) {
            val from = File(dir, file.name)
            var name = file.name
            val pos = name.lastIndexOf(".")
            if (pos > 0) {
                name = name.substring(0, pos)
            }
            val to = File(dir, suffix)
            if (from.exists())
                from.renameTo(to)
        }

    }
    interface OnClickDialog {
        fun onClick(name: String,pathNew: String)
    }

}