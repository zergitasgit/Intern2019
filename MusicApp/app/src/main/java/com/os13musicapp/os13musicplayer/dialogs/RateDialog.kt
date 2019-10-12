package com.os13musicapp.os13musicplayer.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.os13musicapp.os13musicplayer.R
import kotlinx.android.synthetic.main.dialog_rate.*

class RateDialog(context: Context, private val listener: OnClickDialog) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_rate)
        tv_not_now.setOnClickListener {
            dismiss()
            listener?.onCancel()
        }

        tv_rate_now.setOnClickListener {
            dismiss()
            listener.onRate()
        }
    }

    interface OnClickDialog {
        fun onRate()
        fun onCancel()
    }
}