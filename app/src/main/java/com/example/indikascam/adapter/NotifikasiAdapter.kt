package com.example.indikascam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R
import com.example.indikascam.model.NotifikasiItem
import java.text.SimpleDateFormat
import java.util.*

class NotifikasiAdapter(private val notifikasiList: List<NotifikasiItem>):
    RecyclerView.Adapter<NotifikasiAdapter.NotifikasiViewHolder>(){

        class NotifikasiViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val waktu: TextView = itemView.findViewById(R.id.layoutItemNotifikasi2_tv_waktu)
            val caption: TextView = itemView.findViewById(R.id.layoutItemNotifikasi2_tv_caption)
        }

    var hariIni = true
    var bulanIni = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifikasiViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_notifikasi2, parent, false)

        return NotifikasiViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotifikasiViewHolder, position: Int) {
        val currentItem = notifikasiList[position]
        val sdf = SimpleDateFormat("d MMM yyyy", Locale("in", "ID"))
        val c: Calendar = GregorianCalendar()
        c.time = Date()

        if(sdf.format(currentItem.waktu) == sdf.format(c.time)){
            holder.waktu.text = "Hari Ini"
        } else{
            holder.waktu.text = "Bulan ini"
        }

        when{
            hariIni -> {
                (holder.waktu.layoutParams as ConstraintLayout.LayoutParams).apply {
                    topMargin = 0
                }
                holder.waktu.visibility = View.VISIBLE
                hariIni = false
            }
            bulanIni -> {
                holder.waktu.visibility = View.VISIBLE
                bulanIni = false
            }
            else -> {
                holder.waktu.visibility = View.GONE
            }
        }

        holder.caption.text = currentItem.caption

        if(position == itemCount -1){
            hariIni = true
            bulanIni = true
        }
    }

    override fun getItemCount(): Int {
        return notifikasiList.size
    }

}