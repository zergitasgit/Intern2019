package com.example.swipenavigationbar.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import com.example.swipenavigationbar.R
import kotlinx.android.synthetic.main.dialog_width.*


class MainDialog(context: Context, private var status: String,
                 private var listener:OnClickDialog) : Dialog(context) {
    var sharedPreferences: SharedPreferences? = null
    var edit: SharedPreferences.Editor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = context.getSharedPreferences("hieu", Context.MODE_PRIVATE)
        edit = sharedPreferences?.edit()
        window!!.setBackgroundDrawable(ColorDrawable(0))
        setContentView(R.layout.dialog_width)
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )


        if (status == "width") {
            tv_sb.text = sharedPreferences!!.getInt("sbWidth", 100).toString() + "%"
            sb_main.progress = sharedPreferences!!.getInt("sbWidth", 100)
        }else if(status == "height"){
            tv_title.text = context.resources.getString(R.string.bu_height)
            tv_set.text = context.resources.getString(R.string.set_height)
            tv_sb.text = sharedPreferences!!.getInt("sbHeight", 50).toString() + "%"
            sb_main.progress = sharedPreferences!!.getInt("sbHeight", 50)
        }else if(status == "margin"){
            tv_title.text = context.resources.getString(R.string.bo_margin)
            tv_set.text = context.resources.getString(R.string.mar)
            tv_sb.text = sharedPreferences!!.getInt("sbMargin", 0).toString() + "%"
            sb_main.progress = sharedPreferences!!.getInt("sbMargin", 0)
        }else{
            tv_title.text =context.resources.getString(R.string.vib_str)
            tv_set.text = context.resources.getString(R.string.set_vib_str)
            tv_sb.text = sharedPreferences!!.getInt("sbVib", 0).toString() + "%"
            sb_main.progress = sharedPreferences!!.getInt("sbVib", 0)
        }
        tv_cancel.setOnClickListener {
            dismiss()
        }
        tv_ok.setOnClickListener {
            dismiss()
            if (status == "width") {
                val intent = Intent("WIDTH")
                intent.putExtra("sbWidth", sb_main.progress)
                context.sendBroadcast(intent)
                edit!!.putInt("sbWidth", sb_main.progress)
                edit!!.apply()
            }else if(status == "height"){
                val intent = Intent("HEIGHT")
                intent.putExtra("sbHeight", sb_main.progress)
                context.sendBroadcast(intent)
                edit!!.putInt("sbHeight", sb_main.progress)
                edit!!.apply()
            }else if(status == "margin"){
                val intent = Intent("MARGIN")
                intent.putExtra("sbMargin", sb_main.progress)
                context.sendBroadcast(intent)
                edit!!.putInt("sbMargin", sb_main.progress)
                edit!!.apply()
            }else{
                edit!!.putInt("sbVib", sb_main.progress)
                edit!!.apply()
            }
            listener.onClick(sb_main.progress)
        }
        sb_main.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    tv_sb.text = "$p1%"
                }

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

    }

    interface OnClickDialog {
        fun onClick(value: Int)
    }

}