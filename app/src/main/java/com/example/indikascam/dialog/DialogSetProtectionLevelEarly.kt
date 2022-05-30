package com.example.indikascam.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.indikascam.R

class DialogSetProtectionLevelEarly: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_set_protection_level_early, container, false)
        var userProtectionLevel: Byte = 1
        rootView.findViewById<RadioButton>(R.id.dialogSetProtectionLevelEarly_rb_proteksiTinggi).setOnCheckedChangeListener { _, b ->
            if(b){
                rootView.findViewById<TextView>(R.id.dialogSetProtectionLevelEarly_tv_caption).text = getString(R.string.proteksi_tinggi_caption)
                userProtectionLevel = 0
            }
        }

        rootView.findViewById<RadioButton>(R.id.dialogSetProtectionLevelEarly_rb_proteksiSedang).setOnCheckedChangeListener { _, b ->
            if(b){
                rootView.findViewById<TextView>(R.id.dialogSetProtectionLevelEarly_tv_caption).text = getString(R.string.proteksi_sedang_caption)
                userProtectionLevel = 1
            }
        }

        rootView.findViewById<RadioButton>(R.id.dialogSetProtectionLevelEarly_rb_proteksiRendah).setOnCheckedChangeListener { _, b ->
            if(b){
                rootView.findViewById<TextView>(R.id.dialogSetProtectionLevelEarly_tv_caption).text = getString(R.string.proteksi_rendah_caption)
                userProtectionLevel = 2
            }
        }

        rootView.findViewById<Button>(R.id.dialogSetProtectionLevelEarly_btn_applyProtection).setOnClickListener {
            dismiss()
        }

        return rootView
    }
}