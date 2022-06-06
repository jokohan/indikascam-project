package com.example.indikascam.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R
import com.example.indikascam.modelsRcv.MyReport

class ReportListAdapter(private val myReportList: List<MyReport>, val listener: (Int) -> Unit):
    RecyclerView.Adapter<ReportListAdapter.ReportListViewHolder>(){

        class ReportListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val title: TextView = itemView.findViewById(R.id.item_laporan_title)
            val reportType: TextView = itemView.findViewById(R.id.item_laporan_reportType)
            val time: TextView = itemView.findViewById(R.id.item_laporan_time)
            val status: TextView = itemView.findViewById(R.id.item_laporan_status)
            val card: ConstraintLayout = itemView.findViewById(R.id.item_laporan)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_list_laporan, parent, false)

        return  ReportListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReportListViewHolder, position: Int) {
        val currentItem = myReportList[position]
        holder.title.text = currentItem.title
        holder.reportType.text = currentItem.reportType
        holder.time.text = currentItem.time
        holder.status.text = currentItem.status
        when(currentItem.status){
            "Pending" -> holder.status.setTextColor(Color.parseColor("#FFCD44"))
            "Ditolak" -> holder.status.setTextColor(Color.parseColor("#DD5246"))
            "Diterima" -> holder.status.setTextColor(Color.parseColor("#19A15F"))
            else -> holder.status.setTextColor(Color.parseColor("#FFCD44"))
        }
        holder.card.setOnClickListener {
            listener(currentItem.id)
        }
    }

    override fun getItemCount(): Int {
        return myReportList.size
    }

}