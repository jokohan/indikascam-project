package com.example.indikascam.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.indikascam.R

class WelcomeDialog() : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_welcome_dialog, container, false)

        rootView.findViewById<Button>(R.id.welcomeFragment_btn_lanjut).setOnClickListener {
            val statisticsDialog = StatisticsDialog("Mulai Blokir")
            statisticsDialog.isCancelable = false
            statisticsDialog.show(parentFragmentManager, "Statistics Dialog")
            dismiss()
        }

        return rootView
    }

}