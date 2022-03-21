package com.example.indikascam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R

class ScammedBannerAdapter: RecyclerView.Adapter<ScammedBannerAdapter.ViewHolder>() {

    private var titles = arrayOf("Facebook", "Instagram", "TikTok")

    private var counts= arrayOf("31 kasus", "20 kasus", "9 kasus")

    private var icons = intArrayOf(R.drawable.facebook, R.drawable.instagram, R.drawable.tiktok)

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var platformIcon: ImageView
        var platformTitle: TextView
        var platformScammedCount: TextView

        init {
            platformIcon = itemView.findViewById(R.id.item_image)
            platformTitle = itemView.findViewById(R.id.item_title)
            platformScammedCount = itemView.findViewById(R.id.item_description)
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScammedBannerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_banner, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ScammedBannerAdapter.ViewHolder, position: Int) {
        holder.platformScammedCount.text = counts[position]
        holder.platformTitle.text = titles[position]
        holder.platformIcon.setImageResource(icons[position])
    }

    override fun getItemCount(): Int {
        return titles.size
    }
}