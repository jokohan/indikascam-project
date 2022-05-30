package com.example.indikascam.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.indikascam.R

class DialogThankYouForReporting : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView : View = inflater.inflate(R.layout.dialog_thank_you_for_reporting, container, false)

        rootView.findViewById<Button>(R.id.dialogThankYou_btn_tutup).setOnClickListener {
            dismiss()
        }

        return rootView
    }
}