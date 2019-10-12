package com.document.pdfviewer.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.LinearLayout
import com.document.pdfviewer.R
import kotlinx.android.synthetic.main.dialog_delete.*
import java.io.File

class DeleteDialog(context: Context,
                   private var path: String,
                   private val listener: OnClickDialog) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setBackgroundDrawable(ColorDrawable(0))
        setContentView(R.layout.dialog_delete)
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        tv_cancel_de.setOnClickListener {
            dismiss()
        }
        tv_ok_de.setOnClickListener {
            delete(path)
            dismiss()
            listener.onClick(path)
            val intentBr = Intent("POPMENU")
            context.sendBroadcast(intentBr)
        }
    }
    interface OnClickDialog {
        fun onClick(path: String)
    }
    private fun delete(path: String) {
        val file = File(path)
        file.delete()

    }
}