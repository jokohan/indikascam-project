package com.example.indikascam.dialog

import android.content.DialogInterface
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.indikascam.R
import com.example.indikascam.model.CommentItem

class ReviewUlangDialog(
    private val nomor: String,
    private val caption: String,
    private val laporan: ArrayList<CommentItem>
): DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_review_ulang, container, false)

        view.findViewById<TextView>(R.id.dialogReviewUlang_tv_nomor).text = nomor
        view.findViewById<TextView>(R.id.dialogReviewUlang_tv_nomorCaption).text = if(caption == "0") "Nomor Telepon" else "Nomor Rekening"

        val list = emptyList<String>().toMutableList()
        for(lapor in laporan){
            list += arrayOf("${lapor.title}, Tuduhan: ${lapor.description}, Status: ${lapor.status}")
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, list)
        view.findViewById<AutoCompleteTextView>(R.id.dialogReviewUlang_ac_laporan).setAdapter(adapter)

        view.findViewById<Button>(R.id.dialogReviewUlang_btn_kirim).setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Peringatan")
            builder.setMessage("Anda hanya bisa ajukan 1x dalam 24 jam, kirim?")
            builder.setIcon(R.drawable.ic_warning)
            builder.setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
                dismiss()
            })
            builder.setNegativeButton("Tidak", DialogInterface.OnClickListener{ dialogInterface, i ->
                dialogInterface.dismiss()
            })
            builder.show()
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        val window: Window? = dialog!!.window
        val size = Point()

        val display: Display = window?.windowManager!!.defaultDisplay
        display.getSize(size)

        val width: Int = size.x

        window.setLayout((width * 1), WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
    }
}