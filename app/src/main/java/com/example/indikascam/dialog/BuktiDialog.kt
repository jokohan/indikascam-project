package com.example.indikascam.dialog

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.indikascam.R

class BuktiDialog(imageResource: Uri?, text: String, type: Int) : DialogFragment() {

    private val imgRsc = imageResource
    private val nameRsc = text
    private val typeRsc = type

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_bukti_dialog, container, false)

        if(typeRsc == 12){
            rootView.findViewById<ImageView>(R.id.buktiDialogFragment_iv_buktiImage).setImageResource(R.drawable.ic_pdf)
        } else{
            rootView.findViewById<ImageView>(R.id.buktiDialogFragment_iv_buktiImage).setImageURI(imgRsc)
        }
        rootView.findViewById<TextView>(R.id.buktiDialogFragment_tv_buktiTitle).text = nameRsc

        return rootView
    }

}