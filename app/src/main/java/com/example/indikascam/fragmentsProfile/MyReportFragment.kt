package com.example.indikascam.fragmentsProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indikascam.adapter.ReportListAdapter
import com.example.indikascam.databinding.FragmentMyReportBinding
import com.example.indikascam.modelsRcv.MyReport
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

        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = SimpleDateFormat("dd MMM yyyy H:m:s", Locale("in", "ID"))
        if (myReportList.size == 0){
            for(i in 0..19){
                val list = when {
                    i < 5 -> {
                        MyReport("Laporan ${i+1}", "Penipuan", sdf.format(c.time),"Pending")
                    }
                    i < 10 -> {
                        MyReport("Laporan ${i+1}", "Panggilan Robot", sdf.format(c.time),"Diterima")
                    }
                    i < 15 -> {
                        MyReport("Laporan ${i+1}", "Spam", sdf.format(c.time),"Ditolak")
                    }
                    else -> {
                        MyReport("Laporan ${i+1}", "Penipuan", sdf.format(c.time),"Review Ulang")
                    }
                }
                myReportList.add(list)
            }
        }

        binding.myReportFragmentRcvReportList.adapter = myReportAdapter
        binding.myReportFragmentRcvReportList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        return view
    }
}