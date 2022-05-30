package com.example.indikascam.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R
import com.example.indikascam.modelsRcv.Notification
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(private val notificationList: List<Notification>):RecyclerView.Adapter<NotificationAdapter.NotificationListViewHolder>() {
    class NotificationListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val timeCategory: TextView = itemView.findViewById(R.id.cardListNotification_tv_timeCategory)
        val caption: TextView = itemView.findViewById(R.id.cardListNotification_tv_caption)
    }

    private var hariIni = true
    private var bulanIni = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_list_notification, parent, false)

        return NotificationListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationListViewHolder, position: Int) {
        val currentItem = notificationList[position]
        val sdf = SimpleDateFormat("d MMM yyyy", Locale("in", "ID"))
        val c: Calendar = GregorianCalendar()
        c.time = Date()

        if(sdf.format(currentItem.time) == sdf.format(c.time)){
            holder.timeCategory.apply {
                this.text = context.resources.getString(R.string.hari_ini)
            }
        } else{
            holder.timeCategory.apply {
                this.text = context.resources.getString(R.string.bulan_ini)
            }
        }

        when{
            hariIni -> {
                (holder.timeCategory.layoutParams as ConstraintLayout.LayoutParams).apply {
                    topMargin = 0
                }
                holder.timeCategory.visibility = View.VISIBLE
                hariIni = false
            }
            bulanIni -> {
                holder.timeCategory.visibility = View.VISIBLE
                bulanIni = false
            }
            else -> {
                holder.timeCategory.visibility = View.GONE
            }
        }

        holder.caption.text = currentItem.caption

        if(position == itemCount -1){
            hariIni = true
            bulanIni = true
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
}