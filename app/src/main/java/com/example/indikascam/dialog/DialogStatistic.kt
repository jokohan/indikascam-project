package com.example.indikascam.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R
import com.example.indikascam.adapter.StatisticAdapter
import com.example.indikascam.modelsRcv.StatisticPlatformProduct
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class DialogStatistic(
    private val buttonCaption: String,
    private val totalLost: Int,
    platformList: List<StatisticPlatformProduct>,
    productList: ArrayList<StatisticPlatformProduct>
) : DialogFragment() {

    private val productAdapter = StatisticAdapter(productList)
    private val platformAdapter = StatisticAdapter(platformList)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_statistic, container, false)

        val locale = Locale("id", "ID")
        val formattedNumber = NumberFormat.getNumberInstance(locale)
            .format(totalLost)
            .replace(",", ".")
        rootView.findViewById<TextView>(R.id.dialogStatistic_tv_totalLoss).text = String.format(resources.getString(R.string.format_rupiah), formattedNumber)
        rootView.findViewById<Button>(R.id.statisticsDialogFragment_btn_mulaiBlokir).text = buttonCaption
        rootView.findViewById<Button>(R.id.statisticsDialogFragment_btn_mulaiBlokir).setOnClickListener {
            when(rootView.findViewById<Button>(R.id.statisticsDialogFragment_btn_mulaiBlokir).text){
                "Mulai Blokir" -> {
                    val setProtectionLevelEarly = DialogSetProtectionLevelEarly()
                    setProtectionLevelEarly.show(parentFragmentManager, "")
                    dismiss()
                }
                "Tutup" -> {
                    dismiss()
                }
            }
        }
        val platform = rootView.findViewById<RecyclerView>(R.id.dialogStatistic_rcv_platform)
        platform.adapter = platformAdapter
        platform.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val product = rootView.findViewById<RecyclerView>(R.id.dialogStatistic_rcv_product)
        product.adapter = productAdapter
        product.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


        return rootView
    }
}