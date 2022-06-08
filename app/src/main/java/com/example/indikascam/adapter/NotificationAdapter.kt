package com.example.indikascam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R
import com.example.indikascam.api.responses.GetNotificationResponse

class NotificationAdapter(private val notificationTodayList: List<GetNotificationResponse.Today>?, private val notificationMonthList: List<GetNotificationResponse.ThisMonth>?):RecyclerView.Adapter<NotificationAdapter.NotificationListViewHolder>() {
    class NotificationListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val caption: TextView = itemView.findViewById(R.id.cardListNotification_tv_caption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_list_notification, parent, false)

        return NotificationListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationListViewHolder, position: Int) {
        if(notificationTodayList != null){
            val currentItem = notificationTodayList[position]
            holder.caption.text = currentItem.message
        }
        if(notificationMonthList != null){
            val currentItem = notificationMonthList[position]
            holder.caption.text = currentItem.message
        }
    }

    override fun getItemCount(): Int {
        return notificationTodayList?.size ?: notificationMonthList!!.size
    }
}