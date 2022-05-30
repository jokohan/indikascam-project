package com.example.indikascam.fragmentsNotification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indikascam.adapter.BlockedNumberAdapter
import com.example.indikascam.databinding.FragmentBlockListBinding
import com.example.indikascam.modelsRcv.BlockedNumber

class BlockListFragment : Fragment() {

    private var _binding : FragmentBlockListBinding? = null
    private val binding get() = _binding!!

    private val blockedNumberList = ArrayList<BlockedNumber>()
    private val blockedNumberAdapter = BlockedNumberAdapter(blockedNumberList){
        val phoneNumber = arrayOf("phoneNumber", blockedNumberList[it].phoneNumber)
        val action = BlockListFragmentDirections.actionBlockListFragmentToSearchResultFragment(phoneNumber)
        Navigation.findNavController(binding.root).navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlockListBinding.inflate(inflater, container, false)
        val view = binding.root

        if(blockedNumberList.size == 0){
            var i = 0
            while (true){
                if(i == 100){
                    break
                } else{
                    blockedNumberList.add(BlockedNumber("081468761$i", if(i%10==0)"Blokir Pribadi" else "Blokir Otomatis"))
                    i++
                }
            }
        }

        binding.blockListFragmentRcvNumber.adapter = blockedNumberAdapter
        binding.blockListFragmentRcvNumber.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        return view
    }
}