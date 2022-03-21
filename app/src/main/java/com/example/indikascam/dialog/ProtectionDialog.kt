package com.example.indikascam.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation
import com.example.indikascam.R

class ProtectionDialog: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_konfigurasi_proteksi, container, false)

        rootView.findViewById<RadioGroup>(R.id.konfigurasiProteksiFragment_rg_proteksi).setOnCheckedChangeListener{ _, checkedId ->
            when(checkedId){
                R.id.konfigurasiProteksiFragment_rb_proteksiTinggi -> {
                    rootView.findViewById<TextView>(R.id.konfigurasiProteksiFragment_tv_caption).text = getText(R.string.proteksi_tinggi)
                    rootView.findViewById<Button>(R.id.konfigurasiProteksiFragment_btn_terapkanProteksi).text ="Rp 14k/bulan"
                }
                R.id.konfigurasiProteksiFragment_rb_proteksiSedang -> {
                    rootView.findViewById<TextView>(R.id.konfigurasiProteksiFragment_tv_caption).text = getText(R.string.proteksi_sedang)
                    rootView.findViewById<Button>(R.id.konfigurasiProteksiFragment_btn_terapkanProteksi).text = "Gratis"
                }
                R.id.konfigurasiProteksiFragment_rb_proteksiRendah -> {
                    rootView.findViewById<TextView>(R.id.konfigurasiProteksiFragment_tv_caption).text = getText(R.string.proteksi_rendah)
                    rootView.findViewById<Button>(R.id.konfigurasiProteksiFragment_btn_terapkanProteksi).text = "Gratis"
                }
            }
        }

        rootView.findViewById<Button>(R.id.konfigurasiProteksiFragment_btn_terapkanProteksi).setOnClickListener {
            if (rootView.findViewById<RadioButton>(R.id.konfigurasiProteksiFragment_rb_proteksiTinggi).isChecked){
//                Navigation.findNavController(rootView).navigate()
            }
            dismiss()
        }

        return rootView
    }
}