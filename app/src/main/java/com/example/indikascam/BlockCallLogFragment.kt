package com.example.indikascam

import android.os.Bundle
import android.util.Log
import com.example.indikascam.BlockCallLogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indikascam.R
import com.example.indikascam.adapter.BlockCallLogAdapter
import com.example.indikascam.adapter.BuktiLaporanAdapter
import com.example.indikascam.databinding.FragmentBlockCallLogBinding
import com.example.indikascam.databinding.FragmentLaporBinding
import com.example.indikascam.model.NotifikasiBlockCallLogItem
import com.google.android.material.textview.MaterialTextView

class BlockCallLogFragment : Fragment() {

    private var _binding: FragmentBlockCallLogBinding? = null
    private val binding get() = _binding!!

    private val blockCallLog = ArrayList<NotifikasiBlockCallLogItem>()
    private val blockCallLogAdapter = BlockCallLogAdapter(blockCallLog){
        val string = arrayOf(blockCallLog[it.toInt()].phoneNumber, "0")
        val action = BlockCallLogFragmentDirections.actionBlockCallLogFragmentToLaporFragment(string)
        Navigation.findNavController(binding.root).navigate(action)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBlockCallLogBinding.inflate(inflater, container, false)

        if(blockCallLog.size == 0){
            var i = 0
            while (true){
                if(i == 100){
                    break
                } else{
                    blockCallLog.add(NotifikasiBlockCallLogItem("081468761$i", if(i%10==0)"Blokir Pribadi" else "Blokir Otomatis"))
                    i++
                }
            }
        }

        binding.blockCallLogFragmentRcvNomor.adapter = blockCallLogAdapter
        binding.blockCallLogFragmentRcvNomor.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


        return binding.root
    }
}