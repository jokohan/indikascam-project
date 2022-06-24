package com.example.indikascam.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R
import com.example.indikascam.api.responses.GetScamStatisticsResponse

class StatisticAdapter(
    private val platformList: List<GetScamStatisticsResponse.Data.Platform>?,
    private val productList: List<GetScamStatisticsResponse.Data.Product>?
    ): RecyclerView.Adapter<StatisticAdapter.StatisticListViewHolder>() {

    class StatisticListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.findViewById(R.id.card_list_product_platform_image)
        val title: TextView = itemView.findViewById(R.id.card_list_product_platform_title)
        val description: TextView = itemView.findViewById(R.id.card_list_product_platform_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_list_product_platform, parent, false)

        return StatisticListViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StatisticListViewHolder, position: Int){
        if(!platformList.isNullOrEmpty()){
            val currentItem = platformList[position]
            holder.title.text = currentItem.name
            holder.image.setImageResource( when(currentItem.name){
                "Situs Web" -> R.drawable.svg_web
                "Aplikasi Mobile" -> R.drawable.ic_mobile_app
                "Tinder" -> R.drawable.ic_tinder
                "WhatsApp" -> R.drawable.ic_whatsapp
                "TikTok" -> R.drawable.ic_tiktok
                "Line" -> R.drawable.ic_line
                "Twitter" -> R.drawable.ic_twitter
                "Instagram" -> R.drawable.ic_instagram
                "Facebook" -> R.drawable.ic_facebook
                else -> R.drawable.ic_question_mark
            })
            holder.image.setColorFilter(Color.rgb(74,20,140))
            holder.description.text = "${currentItem.total} kasus"
        } else {
            val currentItem = productList!![position]
            holder.title.text = currentItem.name
            holder.image.setImageResource( when(currentItem.name){
                "Kendaraan" -> R.drawable.ic_car
                "Produk Digital" -> R.drawable.ic_digital_product
                "Pulsa" -> R.drawable.ic_4g
                "Penipuan Berhadiah" -> R.drawable.ic_mobile_warning
                "Kecantikan" -> R.drawable.ic_beauty_product
                "Elektronik" -> R.drawable.ic_electrical
                "Makanan" -> R.drawable.ic_food
                "Investasi" -> R.drawable.ic_dollar
                "Busana" -> R.drawable.ic_fashion
                "Perabotan" -> R.drawable.ic_chair
                "Obat" -> R.drawable.ic_med
                "Olahraga" -> R.drawable.ic_sports

                else -> R.drawable.ic_question_mark
            })
            holder.image.setColorFilter(Color.rgb(74,20,140))
            holder.description.text = "${currentItem.total} kasus"
        }

    }

    override fun getItemCount(): Int {
        return if(!platformList.isNullOrEmpty()){
            platformList.size
        }else{
            productList!!.size
        }
    }

}