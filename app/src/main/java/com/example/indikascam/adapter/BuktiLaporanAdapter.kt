package com.example.indikascam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R
import com.example.indikascam.model.BuktiLaporanItem

class BuktiLaporanAdapter(private val buktiLaporanList: List<BuktiLaporanItem>, val listener: (String) -> Unit) :
    RecyclerView.Adapter<BuktiLaporanAdapter.BuktiLaporanViewHolder>() {

    class BuktiLaporanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.item_bukti_image)
        val textView: TextView = itemView.findViewById(R.id.item_bukti_title)
        val deleteView: ImageView = itemView.findViewById(R.id.item_bukti_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuktiLaporanViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_bukti_laporan, parent, false)
        return BuktiLaporanViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BuktiLaporanViewHolder, position: Int) {
        val currentItem = buktiLaporanList[position]
        holder.deleteView.setOnClickListener {
            listener("2 $position")
        }
        holder.deleteView.bringToFront()
        if(currentItem.type == 12){
            holder.imageView.setImageResource(R.drawable.ic_pdf)
        } else{
            holder.imageView.setImageURI(currentItem.buktiImage)
        }
        holder.imageView.setOnClickListener {
            listener("1 $position")
        }
        holder.textView.text = currentItem.buktiTitle
    }

    override fun getItemCount(): Int {
        return buktiLaporanList.size
    }
}
