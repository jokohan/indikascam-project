package com.example.indikascam.dialog

import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.indikascam.R
import com.example.indikascam.modelsRcv.ReportHistory

class DialogReview(private val reportHistoryList: ReportHistory, private val numberType: String, private val number: String) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_review, container, false)

        rootView.findViewById<TextView>(R.id.dialogReview_tv_numberType).text = if(numberType == "accountNumber") "Nomor Rekening" else "Nomor Telepon"
        rootView.findViewById<TextView>(R.id.dialogReview_tv_number).text = number

        rootView.findViewById<TextView>(R.id.dialogReview_tv_laporan).text = String.format(resources.getString(R.string.laporan_review), reportHistoryList.username, reportHistoryList.reportType)
        rootView.findViewById<Button>(R.id.dialogReview_btn_send).setOnClickListener {
            dismiss()
        }

        return rootView
    }

    @Suppress("DEPRECATION")
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