package com.example.indikascam.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R
import com.example.indikascam.adapter.ScammedBannerAdapter
import com.example.indikascam.adapter.ScammedProductAdapter

class StatisticsDialog(buttonText: String): DialogFragment() {

    val text = buttonText
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_statistics_dialog, container, false)

        rootView.findViewById<Button>(R.id.statisticsDialogFragment_btn_mulaiBlokir).text = text

        var layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rootView.findViewById<RecyclerView>(R.id.statisticsDialogFragment_rcv_banner).layoutManager = layoutManager
        var adapter = ScammedBannerAdapter()
        rootView.findViewById<RecyclerView>(R.id.statisticsDialogFragment_rcv_banner).adapter = adapter

        var productLayoutManager =LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rootView.findViewById<RecyclerView>(R.id.statisticsDialogFragment_rcv_product).layoutManager = productLayoutManager
        var productAdapter = ScammedProductAdapter()
        rootView.findViewById<RecyclerView>(R.id.statisticsDialogFragment_rcv_product).adapter = productAdapter

        rootView.findViewById<Button>(R.id.statisticsDialogFragment_btn_mulaiBlokir).setOnClickListener {
            if(text == "Mulai Blokir"){
                val protectionDialog = ProtectionDialog()
                protectionDialog.isCancelable = false
                protectionDialog.show(parentFragmentManager, "Statistics Dialog")
            }
            dismiss()
        }

        return rootView
    }

}