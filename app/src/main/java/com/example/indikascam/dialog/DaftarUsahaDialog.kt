package com.example.indikascam.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.indikascam.R

class DaftarUsahaDialog: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_daftar_usaha_dialog, container, false)

        rootView.findViewById<Button>(R.id.daftarUsahaDialog_btn_tutup).setOnClickListener {
            dismiss()
        }

        return rootView
    }

}