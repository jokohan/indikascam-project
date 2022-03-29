package com.example.indikascam.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R
import com.example.indikascam.model.CommentItem

class CommentAdapter(private val commentList: List<CommentItem>):
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {



    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.item_comment_title)
        val description: TextView = itemView.findViewById(R.id.item_comment_description)
        val image: ImageView = itemView.findViewById(R.id.item_comment_image)
        val tanggal: TextView = itemView.findViewById(R.id.item_comment_tanggalLapor)
        val status: TextView = itemView.findViewById(R.id.item_comment_statusLapor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.CommentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_comment, parent, false)
        return CommentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentAdapter.CommentViewHolder, position: Int) {
        val currentItem = commentList[position]
        holder.title.text = currentItem.title
        holder.description.text = currentItem.description
        holder.image.setImageResource(currentItem.image)
        holder.tanggal.text = currentItem.tanggal
        holder.status.text = currentItem.status
        when(currentItem.status){
            "Pending" -> holder.status.setTextColor(Color.parseColor("#FFCD44"))
            "Ditolak" -> holder.status.setTextColor(Color.parseColor("#DD5246"))
            "Diterima" -> holder.status.setTextColor(Color.parseColor("#19A15F"))
            else -> holder.status.setTextColor(Color.parseColor("#FFCD44"))
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}