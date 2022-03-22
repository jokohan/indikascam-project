package com.example.indikascam.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.indikascam.R

class ReviewUlangDialog(private val nomor: String, private val caption: String): DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_review_ulang, container, false)

        view.findViewById<TextView>(R.id.dialogReviewUlang_tv_nomor).text = nomor
        view.findViewById<TextView>(R.id.dialogReviewUlang_tv_nomorCaption).text = if(caption == "0") "Nomor Telepon" else "Nomor Rekening"

        return view
    }

}