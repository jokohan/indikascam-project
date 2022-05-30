package com.example.indikascam.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R
import com.example.indikascam.modelsRcv.BlockedNumber

class BlockedNumberAdapter(private val blockedNumberList: List<BlockedNumber>, val listener: (Int) -> Unit): RecyclerView.Adapter<BlockedNumberAdapter.BlockedNumberListViewHolder>() {
    class BlockedNumberListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val phoneNumber: TextView = itemView.findViewById(R.id.cardListBlockedNumber_tv_phoneNumber)
        val blockedReason: TextView = itemView.findViewById(R.id.cardListBlockedNumber_tv_blockedReason)
        val searchNumber: ImageView = itemView.findViewById(R.id.cardListBlockedNumber_iv_searchNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedNumberListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_list_blocked_number, parent, false)

        return BlockedNumberListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BlockedNumberListViewHolder, position: Int) {
        val currentItem = blockedNumberList[position]
        holder.phoneNumber.text = currentItem.phoneNumber
        holder.blockedReason.text = currentItem.blockedReason
        if(position%2==0){
            holder.itemView.setBackgroundColor(Color.parseColor("#EDEDED"))
        }else{
            holder.itemView.setBackgroundColor(Color.WHITE)
        }
        holder.searchNumber.setOnClickListener {
            listener(position)
        }
    }

    override fun getItemCount(): Int {
        return blockedNumberList.size
    }
}