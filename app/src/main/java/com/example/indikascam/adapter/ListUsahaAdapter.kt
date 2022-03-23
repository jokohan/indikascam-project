package com.example.indikascam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R
import com.example.indikascam.model.UsahaItem

class ListUsahaAdapter(private val usahaList: List<UsahaItem>, val listener: (String) -> Unit) :
    RecyclerView.Adapter<ListUsahaAdapter.ListUsahaViewHolder>() {

    class ListUsahaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.cardListUsaha_iv_logoUsaha)
        val textView: TextView = itemView.findViewById(R.id.cardListUsaha_tv_namaUsaha)
        val detailUsahaView: ConstraintLayout = itemView.findViewById(R.id.cardListUsaha_cl_detailUsaha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListUsahaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_list_usaha, parent, false)
        return ListUsahaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListUsahaViewHolder, position: Int) {
        val currentItem = usahaList[position]
        holder.imageView.setImageResource(currentItem.usahaIcon)
        holder.textView.text = currentItem.usahaTitle
        holder.detailUsahaView.setOnClickListener {
            listener(currentItem.usahaTitle)
        }
    }

    override fun getItemCount(): Int {
        return usahaList.size
    }
}