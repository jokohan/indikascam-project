package com.example.indikascam.fragmentsProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.indikascam.R
import com.example.indikascam.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.settingsFragmentClTingkatProteksi.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_protectionLevelFragment)
        }

        binding.settingsFragmentSwAnonymous.setOnCheckedChangeListener { _, b ->
            if(b){
                Toast.makeText(requireContext(), "Anonimus aja deh", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "Jangan Anonimus", Toast.LENGTH_SHORT).show()
            }
        }


        return view
    }
}