package com.example.indikascam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R

class CommentAdapter: RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    private var titles = arrayOf("Ucok", "Anonim", "Doe")

    private var description= arrayOf("Scam", "Scam", "Spam")

    private var icons = intArrayOf(R.drawable.business_partnership_illustration, R.drawable.ic_profil, R.drawable.configuration_protection)

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var commentIcon: ImageView
        var commentTitle: TextView
        var commentDesciption: TextView

        init {
            commentIcon = itemView.findViewById(R.id.item_comment_image)
            commentTitle = itemView.findViewById(R.id.item_comment_title)
            commentDesciption = itemView.findViewById(R.id.item_comment_description)
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_comment, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        holder.commentDesciption.text = description[position]
        holder.commentTitle.text = titles[position]
        holder.commentIcon.setImageResource(icons[position])
    }

    override fun getItemCount(): Int {
        return titles.size
    }
}