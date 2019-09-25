package hieusenpaj.com.pdf.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.LinearLayout
import hieusenpaj.com.pdf.R
import kotlinx.android.synthetic.main.dialog_detail.*
import java.util.*

class DetailDialog(context: Context,private var name: String,
                   private var path:String,
                   private var date: String,
                   private var size:String) :Dialog(context){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setBackgroundDrawable(ColorDrawable(0))
        setContentView(R.layout.dialog_detail)
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        tv_name.text = "File Name: " + name
        tv_path.text = "Path: " + path
        tv_date.text = "Last Modified: " + date
        tv_size.text = "Size: " + size
        tv_ok_detail.setOnClickListener {
            dismiss()
        }
    }
}