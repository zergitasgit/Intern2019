package com.document.pdfviewer.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.LinearLayout
import com.document.pdfviewer.R
import kotlinx.android.synthetic.main.dialog_detail.*

class DetailDialog(context: Context,private var name: String,
                   private var path:String,
                   private var date: String,
                   private var size:String) :Dialog(context){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setBackgroundDrawable(ColorDrawable(0))
        setContentView(R.layout.dialog_detail)
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        tv_name.text = context.resources.getString(R.string.file_name)+ " " +name
        tv_path.text = context.resources.getString(R.string.path) +" " + path
        tv_date.text = context.resources.getString(R.string.last_modifiled) +" " + date
        tv_size.text = context.resources.getString(R.string.size) +" " + size
        tv_ok_detail.setOnClickListener {
            dismiss()
        }
    }
}