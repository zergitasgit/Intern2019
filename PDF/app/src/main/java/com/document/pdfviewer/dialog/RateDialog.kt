package com.document.pdfviewer.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.document.pdfviewer.R
import kotlinx.android.synthetic.main.dialog_rate.*

class RateDialog(context: Context, private val listener: OnClickDialog) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_rate)
        tv_rate_now.setOnClickListener {
            listener.onRate()

            dismiss()
        }

        tv_not_now.setOnClickListener {
            listener?.onCancel()

            dismiss()
        }
    }

    interface OnClickDialog {
        fun onRate()
        fun onCancel()
    }
}