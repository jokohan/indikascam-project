package com.example.indikascam.dialog

import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.DialogFragment
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

    override fun onResume() {
        super.onResume()

        val window: Window? = dialog!!.window
        val size = Point()

        val display: Display = window?.windowManager!!.defaultDisplay
        display.getSize(size)

        val width: Int = size.x

        window.setLayout((width * 0.99).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
    }
}