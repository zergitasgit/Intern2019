package hieusenpaj.com.pdf.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.LinearLayout
import hieusenpaj.com.pdf.R
import kotlinx.android.synthetic.main.activity_pdf.*
import kotlinx.android.synthetic.main.dialog_go_to_page.*
import kotlinx.android.synthetic.main.number_picker_layout_custom.*

class PageDialog(context: Context, private val listener: OnClickDialog,private val size:Int) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setBackgroundDrawable(ColorDrawable(0))
        setContentView(R.layout.dialog_go_to_page)
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        tv_cancel_page.setOnClickListener {
            dismiss()
        }
        display.requestFocus()
        number_picker_custom.setDisplayFocusable(true)
        number_picker_custom.max = size
        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        tv_ok_page.setOnClickListener {
            if(!TextUtils.isEmpty(display.text.toString())){
//                pdfView.jumpTo(ed_page.text.toString().toInt())
                listener.onClick(display.text.toString().toInt())
            }
            dismiss()
        }
    }
    interface OnClickDialog {
        fun onClick(page:Int)
    }
}