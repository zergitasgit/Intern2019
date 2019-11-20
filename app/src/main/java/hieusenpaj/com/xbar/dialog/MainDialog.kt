package hieusenpaj.com.xbar.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import hieusenpaj.com.xbar.R
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
            tv_sb.text = sharedPreferences!!.getInt("sbWidth", 0).toString() + "%"
            sb_main.progress = sharedPreferences!!.getInt("sbWidth", 100)
        }else if(status == "height"){
            tv_title.text = "Button height"
            tv_set.text = "Set the button's height"
            tv_sb.text = sharedPreferences!!.getInt("sbHeight", 0).toString() + "%"
            sb_main.progress = sharedPreferences!!.getInt("sbHeight", 50)
        }else if(status == "margin"){
            tv_title.text = "Bottom margin"
            tv_set.text = " Set the button's bottom margin"
            tv_sb.text = sharedPreferences!!.getInt("sbMargin", 0).toString() + "%"
            sb_main.progress = sharedPreferences!!.getInt("sbMargin", 0)
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
            }
            listener.onClick(sb_main.progress)
        }
        sb_main.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    tv_sb.text = p1.toString() + "%"
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