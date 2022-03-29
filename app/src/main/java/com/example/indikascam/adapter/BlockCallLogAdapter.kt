package com.example.indikascam.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.HasilPencarianFragmentDirections
import com.example.indikascam.NotifikasiFragmentDirections
import com.example.indikascam.R
import com.example.indikascam.model.NotifikasiBlockCallLogItem


class BlockCallLogAdapter(private val notifikasiList: List<NotifikasiBlockCallLogItem>, val listener: (String) -> Unit):
    RecyclerView.Adapter<BlockCallLogAdapter.BlockCallLogViewHolder>(){

    class BlockCallLogViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        val phoneNumber: TextView = itemView.findViewById(R.id.layoutItemNotifikasi_tv_caption)
        val blockType: TextView = itemView.findViewById(R.id.layoutItemNotifikasi_tv_blockType)
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): BlockCallLogViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_notifikasi, parent, false)
        return BlockCallLogViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BlockCallLogAdapter.BlockCallLogViewHolder, position: Int) {
        val currentItem = notifikasiList[position]
        holder.phoneNumber.text = currentItem.phoneNumber
        holder.blockType.text = currentItem.tipeBlokir
        if(position%2==0){
            holder.itemView.setBackgroundColor(Color.parseColor("#EDEDED"))
        }else{
            holder.itemView.setBackgroundColor(Color.WHITE)
        }
        holder.itemView.findViewById<ImageView>(R.id.layoutItemNotifikasi_iv_lapor).setOnClickListener {
            listener(position.toString())
        }
    }

    override fun getItemCount(): Int {
        return notifikasiList.size
    }


}