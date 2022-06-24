package com.example.indikascam.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R
import com.example.indikascam.modelsRcv.ProofDetail

class ProofDetailAdapter(private val myProofDetailList: List<ProofDetail>, private val origin: String, val listener: (String) -> Unit):
    RecyclerView.Adapter<ProofDetailAdapter.ProofDetailListViewHolder>(){
    class ProofDetailListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.proofItem_tv_title)
        val file: ImageView = itemView.findViewById(R.id.proofItem_iv_file)
        val delete: ImageView = itemView.findViewById(R.id.proofItem_iv_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProofDetailListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_list_proof, parent, false)

        return ProofDetailListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProofDetailListViewHolder, position: Int) {
        val currentItem = myProofDetailList[position]

        holder.title.text = currentItem.title

        holder.delete.bringToFront()
        if (origin == "MyReportDetailFragment"){
            holder.delete.visibility = View.GONE
        } else{
            holder.delete.visibility = View.VISIBLE
            holder.delete.setOnClickListener {
                listener("deleteFile $position")
            }
        }

        if (currentItem.isItImage){
            holder.file.setImageBitmap(currentItem.image as Bitmap)
        } else{
            holder.file.setImageResource(R.drawable.ic_pdf)
        }
        holder.file.setOnClickListener {
            listener("zoomFile $position")
        }
    }

    override fun getItemCount(): Int {
        return myProofDetailList.size
    }
}