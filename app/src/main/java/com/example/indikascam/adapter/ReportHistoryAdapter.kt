package com.example.indikascam.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R
import com.example.indikascam.dialog.DialogReview
import com.example.indikascam.modelsRcv.ReportHistory
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

class ReportHistoryAdapter(private val reportHistoryList: List<ReportHistory>, val listener: (Int) -> Unit): RecyclerView.Adapter<ReportHistoryAdapter.ReportHistoryViewHolder>() {


    class ReportHistoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.findViewById(R.id.cardListReportHistory_iv_userImage)
        val username: TextView = itemView.findViewById(R.id.cardListReportHistory_tv_username)
        val reportType: TextView = itemView.findViewById(R.id.cardListReportHistory_tv_reportType)
        val date: TextView = itemView.findViewById(R.id.cardListReportHistory_tv_date)
        val status: TextView = itemView.findViewById(R.id.cardListReportHistory_tv_reportStatus)
        val cardView: MaterialCardView = itemView.findViewById(R.id.cardListReportHistory_cv_layout)
        val flag: ImageView = itemView.findViewById(R.id.cardListReportHistory_iv_flag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportHistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_list_report_history, parent, false)

        return ReportHistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReportHistoryViewHolder, position: Int) {
        val currentItem = reportHistoryList[position]
        val sdf = SimpleDateFormat("d MMM yyyy", Locale("in", "ID"))

        holder.date.text = sdf.format(currentItem.date)
        if(currentItem.image == null){
            holder.image.setImageResource(R.drawable.ic_profile)
        }else{
            holder.image.setImageBitmap(currentItem.image)
        }
        holder.reportType.text = currentItem.reportType
        when (currentItem.reportStatus) {
            "Diterima" -> {
                holder.status.setTextColor(Color.parseColor("#19A15F"))
                holder.cardView.strokeWidth = 12
                holder.flag.visibility = View.VISIBLE
                holder.cardView.setOnClickListener {
                    listener(position)
                }
            }
            "Ditolak" -> {
                holder.status.setTextColor(Color.parseColor("#DD5246"))
            }
            else -> {
                holder.status.setTextColor(Color.parseColor("#FFCD44"))
                holder.cardView.strokeWidth = 0
                holder.flag.visibility = View.GONE
            }
        }
        holder.status.text = currentItem.reportStatus
        holder.username.text = currentItem.username
    }

    override fun getItemCount(): Int {
        return reportHistoryList.size
    }
}