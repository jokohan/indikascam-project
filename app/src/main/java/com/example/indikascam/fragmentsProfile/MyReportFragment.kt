package com.example.indikascam.fragmentsProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indikascam.adapter.ReportListAdapter
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.databinding.FragmentMyReportBinding
import com.example.indikascam.dialog.DialogProgressBar
import com.example.indikascam.modelsRcv.MyReport
import com.example.indikascam.sessionManager.SessionManager
import com.example.indikascam.util.Util
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MyReportFragment : Fragment() {

    private var _binding : FragmentMyReportBinding? = null
    private val binding get() = _binding!!

    private val myReportList = ArrayList<MyReport>()
    private val myReportAdapter = ReportListAdapter(myReportList){
        val action = MyReportFragmentDirections.actionMyReportFragmentToMyReportDetailFragment(it)
        Navigation.findNavController(binding.root).navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyReportBinding.inflate(inflater, container, false)
        val view = binding.root
        val sessionManager = SessionManager(requireContext())

        if(myReportList.size == 0){
            lifecycleScope.launchWhenCreated {
                val loadingDialog = DialogProgressBar.progressDialog(requireContext())
                loadingDialog.show()
                val response = try{
                    RetroInstance.apiProfile.getMyReports(PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"))
                }catch (e: IOException){
                    Log.e("MyReportIOError", e.message!!)
                    loadingDialog.dismiss()
                    return@launchWhenCreated
                }catch (e: HttpException){
                    Log.e("MyReportHttpError", e.message!!)
                    loadingDialog.dismiss()
                    return@launchWhenCreated
                }
                if(response.isSuccessful && response.body() != null){
                    val sdf = SimpleDateFormat("dd MMM yyyy H:m:s", Locale("in", "ID"))

                    for(report in response.body()!!.data){
                        val stringToDate = Util().stringToDate(report.created_at)
                        myReportList.add(MyReport(report.id, report.report_number, report.report_type, sdf.format(stringToDate), report.status))
                    }
                    binding.myReportFragmentRcvReportList.adapter = myReportAdapter
                    binding.myReportFragmentRcvReportList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }else{
                    try{
                        @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getJSONObject("error").getString("message")
                        Log.e("logoutError", errorMessage)
                        Log.e("logoutError", response.code().toString())
                    }catch (e: Exception){
                        Log.e("logoutError", e.toString())
                    }
                }
                loadingDialog.dismiss()
            }
        }else{
            binding.myReportFragmentRcvReportList.adapter = myReportAdapter
            binding.myReportFragmentRcvReportList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        return view
    }
}