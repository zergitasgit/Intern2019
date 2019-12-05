package com.example.swipenavigationbar.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.swipenavigationbar.`object`.Action
import com.example.swipenavigationbar.R
import kotlinx.android.synthetic.main.item_action.view.*

class ActionAdapter(private var context: Context,
                    private var arr:ArrayList<Action>,
                    private var listener:Listener
                    ) : RecyclerView.Adapter<ActionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_action,parent,false))

    }

    override fun getItemCount(): Int = arr.size


    override fun onBindViewHolder(holder: ActionAdapter.ViewHolder, position: Int) {
        if(arr[position].isCheck){
            holder.iv.setImageDrawable(context.resources.getDrawable(R.drawable.icon_select_round_2))
        }else{
            holder.iv.setImageDrawable(context.resources.getDrawable(R.drawable.icon_select_round_1))

        }
        holder.tv.text = arr[position].action
        holder.rl.setOnClickListener {
            listener.onClick(position,arr[position].code)
        }
    }
    class ViewHolder(v:View):RecyclerView.ViewHolder(v){
        val iv = v.iv
        val tv = v.tv
        val rl = v.rl

    }
    interface Listener{
        fun onClick(pos:Int,code:String)
    }
    fun setAllFalse(){
        for(action in arr){
            action.isCheck = false
        }
        notifyDataSetChanged()
    }
}