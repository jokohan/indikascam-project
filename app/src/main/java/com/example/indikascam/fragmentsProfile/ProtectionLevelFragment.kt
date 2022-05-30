package com.example.indikascam.fragmentsProfile

import android.os.Bundle
import android.text.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.indikascam.R
import com.example.indikascam.databinding.FragmentProtectionLevelBinding

class ProtectionLevelFragment : Fragment() {

    private var _binding : FragmentProtectionLevelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProtectionLevelBinding.inflate(inflater, container, false)
        val view = binding.root

        var userProtectionLevel: Byte = 1

        binding.protectionLevelFragmentRbProteksiTinggi.setOnCheckedChangeListener { _, b ->
            if(b){
                binding.protectionLevelFragmentTvCaption.text = getString(R.string.proteksi_tinggi_caption)
                userProtectionLevel = 0
            }
        }
        binding.protectionLevelFragmentRbProteksiSedang.setOnCheckedChangeListener { _, b ->
            if(b){
                binding.protectionLevelFragmentTvCaption.text = getString(R.string.proteksi_sedang_caption)
                userProtectionLevel = 1
            }
        }
        binding.protectionLevelFragmentRbProteksiRendah.setOnCheckedChangeListener { _, b ->
            if(b){
                binding.protectionLevelFragmentTvCaption.text = getString(R.string.proteksi_rendah_caption)
                userProtectionLevel = 2

            }
        }

        binding.protectionLevelFragmentBtnApply.setOnClickListener {
            Toast.makeText(requireContext(), userProtectionLevel.toString(), Toast.LENGTH_SHORT).show()
        }

        return view
    }
}