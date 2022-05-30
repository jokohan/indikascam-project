package com.example.indikascam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R
import com.example.indikascam.modelsRcv.StatisticPlatformProduct

class StatisticAdapter(private val statisticsList: List<StatisticPlatformProduct>): RecyclerView.Adapter<StatisticAdapter.StatisticListViewHolder>() {
    class StatisticListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.findViewById(R.id.card_list_product_platform_image)
        val title: TextView = itemView.findViewById(R.id.card_list_product_platform_title)
        val description: TextView = itemView.findViewById(R.id.card_list_product_platform_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_list_product_platform, parent, false)

        return StatisticListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StatisticListViewHolder, position: Int){
        val currentItem = statisticsList[position]
        if(currentItem.image == "Lainnya"){
            holder.image.setImageResource(R.drawable.ic_web)
        }
        holder.title.text = currentItem.title
        holder.description.text = currentItem.description.toString()
    }

    override fun getItemCount(): Int {
        return statisticsList.size
    }

}