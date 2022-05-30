package com.example.indikascam.fragmentsHome

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indikascam.R
import com.example.indikascam.adapter.ReportHistoryAdapter
import com.example.indikascam.databinding.FragmentSearchResultBinding
import com.example.indikascam.dialog.DialogReview
import com.example.indikascam.modelsRcv.ReportHistory
import java.util.*
import kotlin.collections.ArrayList


class SearchResultFragment : Fragment() {

    private var _binding : FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private val args: SearchResultFragmentArgs by navArgs()

    private val reportHistoryList = ArrayList<ReportHistory>()
    private val reportHistoryAdapter = ReportHistoryAdapter(reportHistoryList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.searchResultFragmentTilChooseBankFirstIfAccountNumberAppearInTwoDifferentBanksOrMore.visibility = View.GONE

        val numberType = args.searchNumber[0]
        val number = args.searchNumber[1]

        if(number.isEmpty()){
            binding.searchResultFragmentTvNumber.text = resources.getString(R.string.nomor_tidak_ditemukan)
        }else{
            binding.searchResultFragmentTvNumber.text = number
        }

        if(numberType == "phoneNumber"){
            binding.searchResultFragmentTvNumberCaption.text = resources.getString(R.string.nomor_telepon)
        } else if(numberType == "accountNumber"){
            binding.searchResultFragmentTvNumberCaption.text = resources.getString(R.string.nomor_rekening)
            binding.searchResultFragmentBtnBlock.visibility = View.GONE
        }

        setupBlockPhoneNumber(number)
        val c: Calendar = GregorianCalendar()
        for(i in 0..4){

            when(i){
                0 -> reportHistoryList.add(ReportHistory(R.drawable.ic_web,"Joko","Penipuan", c.time, "Pending"))
                1 -> reportHistoryList.add(ReportHistory(R.drawable.ic_web,"Joko","Penipuan", c.time, "Diterima"))
                2 -> reportHistoryList.add(ReportHistory(R.drawable.ic_web,"Joko","Penipuan", c.time, "Ditolak"))
                3 -> reportHistoryList.add(ReportHistory(R.drawable.ic_web,"Joko","Penipuan", c.time, "Review Ulang"))
            }
        }

        binding.searchResultFragmentRcvHistory.adapter = reportHistoryAdapter
        binding.searchResultFragmentRcvHistory.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.searchResultFragmentIvReview.setOnClickListener {
            val reportToReview = ArrayList<ReportHistory>()
            for(report in reportHistoryList){
                if(report.reportStatus == "Diterima"){
                    reportToReview.add(report)
                }
            }
            val reviewDialog = DialogReview(reportToReview, numberType, number)
            reviewDialog.show(parentFragmentManager, "Dialog Review")
        }

        return view
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    private fun setupBlockPhoneNumber(number: String) {
        binding.searchResultFragmentBtnBlock.setOnClickListener {
            if (number.isEmpty()){
                val toast = Toast.makeText(requireContext(), "Silahkan cari nomor telepon yang ingin diblokir", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }else{
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

                if(binding.searchResultFragmentBtnBlock.text == "Blokir"){
                    builder.setTitle("Blokir")
                    builder.setMessage("Anda ingin blokir panggilan masuk dari $number?")
                    builder.setIcon(R.drawable.ic_phone_disabled)
                }else if(binding.searchResultFragmentBtnBlock.text == "Buka Blokir"){
                    builder.setTitle("Buka Blokir")
                    builder.setMessage("Anda ingin buka blokir panggilan masuk dari $number?")
                    builder.setIcon(R.drawable.ic_phone_enabled)
                }

                builder.setPositiveButton("Ya"){dialogInterface,_->
                    if(binding.searchResultFragmentBtnBlock.text == "Blokir"){
                        binding.searchResultFragmentBtnBlock.text = resources.getString(R.string.buka_blokir)
                        binding.searchResultFragmentBtnBlock.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_phone_enabled, 0, 0)

                    }else if(binding.searchResultFragmentBtnBlock.text == "Buka Blokir"){
                        binding.searchResultFragmentBtnBlock.text = resources.getString(R.string.blokir)
                        binding.searchResultFragmentBtnBlock.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_phone_disabled, 0, 0)
                    }
                    binding.searchResultFragmentBtnBlock.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                    dialogInterface.dismiss()
                }

                builder.setNegativeButton("Tidak"){dialogInterface,_->
                    dialogInterface.dismiss()
                }

                builder.show()
            }

        }
    }
}