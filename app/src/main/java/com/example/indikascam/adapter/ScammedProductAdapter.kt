package com.example.indikascam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R

class ScammedProductAdapter: RecyclerView.Adapter<ScammedProductAdapter.ViewHolder>() {

    private var titles = arrayOf("Fashion", "Makanan", "Perabotan", "Elektronik", "Gadget", "Obat", "Jasa")

    private var counts= arrayOf("131 kasus", "100 kasus", "98 kasus", "79 kasus", "51 kasus", "13 kasus", "2 kasus")

    private var icons = intArrayOf(R.drawable.ic_fashion, R.drawable.ic_food, R.drawable.ic_furnitures, R.drawable.ic_electronic, R.drawable.ic_devices, R.drawable.ic_med, R.drawable.ic_services)

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var productIcon: ImageView
        var productTitle: TextView
        var productScammedCount: TextView

        init {
            productIcon = itemView.findViewById(R.id.item_product_image)
            productTitle = itemView.findViewById(R.id.item_product_title)
            productScammedCount = itemView.findViewById(R.id.item__product_description)
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScammedProductAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_product, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ScammedProductAdapter.ViewHolder, position: Int) {
        holder.productScammedCount.text = counts[position]
        holder.productTitle.text = titles[position]
        holder.productIcon.setImageResource(icons[position])
    }

    override fun getItemCount(): Int {
        return titles.size
    }
}