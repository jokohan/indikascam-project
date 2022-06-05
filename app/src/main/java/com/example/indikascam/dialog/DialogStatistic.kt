package com.example.indikascam.dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.R
import com.example.indikascam.adapter.StatisticAdapter
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.api.responses.GetScamStatisticsResponse
import com.example.indikascam.modelsRcv.StatisticPlatformProduct
import com.example.indikascam.sessionManager.SessionManager
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class DialogStatistic(private val buttonCaption: String) : DialogFragment() {

    private lateinit var sessionManager: SessionManager

    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loadingDialog = DialogProgressBar.progressDialog(requireContext())
        sessionManager = SessionManager(requireContext())
        val rootView: View = inflater.inflate(R.layout.dialog_statistic, container, false)
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

        lifecycleScope.launchWhenCreated {
            val response = try{
                RetroInstance.apiHome.getScamStatistics(PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"))
            }catch (e: IOException) {
                Log.e("scamStatisticsErrorIO", e.message!!)
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e("scamStatisticsErrorHttp", e.message!!)
                return@launchWhenCreated
            }
            if(response.isSuccessful && response.body()!= null){
                var a = response.body()!!.data.total_loss.toDouble()
                var b = 0
                while(true){
                    if(a<999){
                        break
                    }else{
                        b++
                        a /= 1000.0
                    }
                }
                val c = when(b){
                    0 -> "Rupiah"
                    1 -> "Ribu"
                    2 -> "Juta"
                    3 -> "Miliar"
                    4 -> "Triliun"
                    5 -> "Kuadriliun"
                    6 -> "Kuantiliun"
                    else -> ""
                }
                rootView.findViewById<TextView>(R.id.dialogStatistic_tv_totalLoss).text = String.format(resources.getString(R.string.format_rupiah), a.toInt(), c)

                val platformList = emptyList<GetScamStatisticsResponse.Data.Platform>().toMutableList()
                for(data in response.body()!!.data.platforms){
                    platformList += data
                }
                val productList = emptyList<GetScamStatisticsResponse.Data.Product>().toMutableList()
                for(data in response.body()!!.data.products){
                    productList += data
                }
                val platformAdapter = StatisticAdapter(platformList, null)
                val productAdapter = StatisticAdapter(null, productList)
                val platform = rootView.findViewById<RecyclerView>(R.id.dialogStatistic_rcv_platform)
                platform.adapter = platformAdapter
                platform.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                val product = rootView.findViewById<RecyclerView>(R.id.dialogStatistic_rcv_product)
                product.adapter = productAdapter
                product.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                loadingDialog.dismiss()
            }else{
                try{
                    @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                    val errorMessage = jObjError.getJSONObject("error").getString("message")
                    Log.e("registerError", errorMessage)
                    Log.e("registerError", response.code().toString())
                }catch (e: Exception){
                    Log.e("registerError", e.toString())
                }
            }
        }

        return rootView
    }

    override fun onResume() {
        super.onResume()
        loadingDialog.show()
    }
}